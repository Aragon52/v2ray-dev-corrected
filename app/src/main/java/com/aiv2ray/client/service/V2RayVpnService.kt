package com.aiv2ray.client.service

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.ServiceInfo
import android.net.VpnService
import android.os.Build
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.core.app.NotificationCompat
import com.aiv2ray.client.AiV2RayApplication.Companion.CHANNEL_VPN
import com.aiv2ray.client.R
import com.aiv2ray.client.ui.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Tun-based VPN. Reserves 10.66.0.2/24, routes all IPv4+IPv6 traffic through
 * the Xray core, and runs a persistent typed foreground-service notification
 * (FGS type = specialUse, subtype = vpn_tunnel).
 */
class V2RayVpnService : VpnService() {

    private var tunFd: ParcelFileDescriptor? = null
    private var xray: XrayController? = null
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action

        if (action == ACTION_STOP) {
            stopSelf()
            return START_NOT_STICKY
        }

        val configJson = intent?.getStringExtra(EXTRA_CONFIG_JSON).orEmpty()

        // Start foreground BEFORE establish() so Android does not tear us down
        // immediately under the API 34+ typed-FGS rules.
        startForegroundCompat(buildNotification())

        if (xray?.isRunning == true || tunFd != null) return START_STICKY
        if (configJson.isBlank() || configJson == "{}") {
            Log.w(TAG, "no config supplied")
            stopForegroundCompat()
            stopSelf()
            return START_NOT_STICKY
        }

        val builder = Builder()
            .setSession(SESSION_NAME)
            .setMtu(1500)
            .addAddress(VPN_ADDRESS, 32)
            .addRoute(VPN_ROUTE, 0)   // 0.0.0.0/0 — full tunnel
            .addRoute("::", 0)         // IPv6
            .addDnsServer("1.1.1.1")
            .addDnsServer("1.0.0.1")
            .setBlocking(true)         // kill-switch: drop traffic when the tunnel is down

        try {
            builder.addDisallowedApplication(packageName)
        } catch (_: Throwable) {
        }

        tunFd = builder.establish()
        // Keep the ParcelFileDescriptor alive for the whole session and close it
        // in onDestroy(). Do NOT detachFd() — detaching leaks the tun fd and
        // breaks teardown.
        val fd = tunFd?.fd ?: run {
            stopForegroundCompat()
            stopSelf()
            return START_NOT_STICKY
        }

        val controller = XrayController(applicationContext)
        xray = controller
        scope.launch {
            try {
                controller.start(configJson, fd)
            } catch (t: Throwable) {
                Log.e(TAG, "core start failed", t)
                stopSelf()
            }
        }
        return START_STICKY
    }

    private fun buildNotification(): Notification {
        val launchIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pi = PendingIntent.getActivity(
            this, 0, launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_VPN)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(SESSION_NAME)
            .setContentText("Tunnel active")
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pi)
            .build()
    }

    private fun startForegroundCompat(n: Notification) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(NOTIF_ID, n, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else {
            startForeground(NOTIF_ID, n)
        }
    }

    private fun stopForegroundCompat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        }
    }

    override fun onDestroy() {
        scope.cancel()
        xray?.stop()
        xray = null
        try {
            tunFd?.close()
        } catch (_: Throwable) {
        }
        tunFd = null
        stopForegroundCompat()
        super.onDestroy()
    }

    override fun onRevoke() {
        // User revoked the VPN profile from system settings.
        stopSelf()
    }

    companion object {
        private const val TAG = "V2RayVpnService"
        const val SESSION_NAME = "Ai V2Ray Client"
        const val VPN_ADDRESS = "10.66.0.2"
        const val VPN_ROUTE = "0.0.0.0"
        const val EXTRA_CONFIG_JSON = "xray_config_json"
        const val ACTION_STOP = "com.aiv2ray.client.action.STOP"
        private const val NOTIF_ID = 4242
    }
}
