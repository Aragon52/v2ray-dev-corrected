package com.aiv2ray.client.service

import android.content.Context
import android.provider.Settings
import android.util.Base64
import android.util.Log
import go.Seq
import libv2ray.CoreCallbackHandler
import libv2ray.CoreController
import libv2ray.Libv2ray

/**
 * Wraps the Xray core (gomobile AAR) and drives its lifecycle.
 *
 * The real binding is [CoreController] + [CoreCallbackHandler] (see the
 * generated `libv2ray` bindings). The core is initialised once via
 * [Libv2ray.initCoreEnv] before the first [CoreController.startLoop] call.
 *
 * Tun integration: the core reads the tun file descriptor from the
 * `xray.tun.fd` environment variable, which `startLoop(config, tunFd)` sets
 * internally. The config must therefore contain a `"protocol": "tun"` inbound.
 */
class XrayController(private val appContext: Context) {

    private val callback = object : CoreCallbackHandler {
        override fun startup(): Long {
            Log.i(TAG, "core startup")
            return 0L
        }

        override fun shutdown(): Long {
            Log.i(TAG, "core shutdown")
            return 0L
        }

        override fun onEmitStatus(level: Long, message: String?): Long {
            Log.i(TAG, "core[$level] $message")
            return 0L
        }
    }

    private val lock = Any()
    private var controller: CoreController? = null
    private var envInitialised = false

    private fun ensureEnv() {
        if (envInitialised) return
        try {
            // gomobile needs the JVM context for RunOnJvm.
            Seq.setContext(appContext)
            val assetPath = appContext.getDir("assets", Context.MODE_PRIVATE).absolutePath
            Libv2ray.initCoreEnv(assetPath, xudpBaseKey())
            envInitialised = true
        } catch (t: Throwable) {
            Log.e(TAG, "initCoreEnv failed", t)
        }
    }

    /** Stable per-install key for the core's XUDP obfuscation. */
    private fun xudpBaseKey(): String = try {
        val androidId = Settings.Secure.getString(appContext.contentResolver, Settings.Secure.ANDROID_ID).orEmpty()
        val bytes = androidId.toByteArray(Charsets.UTF_8).copyOf(32)
        Base64.encodeToString(bytes, Base64.NO_PADDING or Base64.URL_SAFE)
    } catch (t: Throwable) {
        ""
    }

    /** Start the core with a full Xray config JSON and the tun fd (0 = no tun). */
    @Throws(Exception::class)
    fun start(configJson: String, tunFd: Int) {
        synchronized(lock) {
            ensureEnv()
            controller?.let { if (it.isRunning) return }
            val c = Libv2ray.newCoreController(callback)
            c.startLoop(configJson, tunFd)
            controller = c
        }
    }

    /** Stop the core; safe to call repeatedly. */
    fun stop() {
        synchronized(lock) {
            try {
                controller?.stopLoop()
            } catch (t: Throwable) {
                Log.w(TAG, "stopLoop threw", t)
            }
            controller = null
        }
    }

    val isRunning: Boolean
        get() = controller?.isRunning == true

    companion object {
        private const val TAG = "XrayController"

        /** Engine version string, or "—" if the native lib is unavailable. */
        fun engineVersion(): String = try {
            Libv2ray.checkVersionX()
        } catch (t: Throwable) {
            "—"
        }
    }
}
