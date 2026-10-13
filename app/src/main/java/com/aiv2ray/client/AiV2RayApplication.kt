package com.aiv2ray.client

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class AiV2RayApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(NotificationManager::class.java)
            val channel = NotificationChannel(
                CHANNEL_VPN,
                "VPN tunnel",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Persistent notification while a V2Ray tunnel is active"
                setShowBadge(false)
            }
            nm.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_VPN = "vpn_tunnel_v1"
    }
}
