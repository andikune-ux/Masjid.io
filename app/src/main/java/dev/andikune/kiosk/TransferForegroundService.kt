package dev.andikune.masjidio.kiosk

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log

/**
 * TransferForegroundService — Foreground Service untuk jaga server & WiFi aktif
 * saat proses transfer media dari HP ke TV.
 *
 * V1.04.426 BARU.
 *
 * Fungsi:
 *   1. Foreground Service dengan notifikasi permanen
 *      → Android tidak boleh bunuh proses ini
 *   2. WiFiLock → WiFi tidak mati saat layar TV mati
 *   3. WakeLock → CPU tetap jalan saat layar mati
 *
 * Cara pakai (dari MainActivity atau IoControlScreen):
 *   TransferForegroundService.start(context)
 *   TransferForegroundService.stop(context)
 */
class TransferForegroundService : Service() {

    companion object {
        private const val TAG = "TransferFgService"
        private const val CHANNEL_ID = "masjid_io_transfer"
        private const val NOTIF_ID = 8888

        const val ACTION_START = "dev.andikune.masjidio.START_TRANSFER"
        const val ACTION_STOP = "dev.andikune.masjidio.STOP_TRANSFER"

        /**
         * Mulai service — panggil dari Activity/Composable.
         */
        fun start(context: Context) {
            try {
                val intent = Intent(context, TransferForegroundService::class.java).apply {
                    action = ACTION_START
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
                Log.d(TAG, "Service diminta start")
            } catch (e: Exception) {
                Log.e(TAG, "Gagal start service: ${e.message}")
            }
        }

        /**
         * Hentikan service — panggil setelah transfer selesai.
         */
        fun stop(context: Context) {
            try {
                val intent = Intent(context, TransferForegroundService::class.java).apply {
                    action = ACTION_STOP
                }
                context.startService(intent)
                Log.d(TAG, "Service diminta stop")
            } catch (e: Exception) {
                Log.e(TAG, "Gagal stop service: ${e.message}")
            }
        }
    }

    private var wifiLock: WifiManager.WifiLock? = null
    private var wakeLock: PowerManager.WakeLock? = null

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action

        when (action) {
            ACTION_START -> {
                Log.d(TAG, "START — aktifkan foreground + lock")
                startForeground(NOTIF_ID, buildNotification())
                acquireLocks()
            }
            ACTION_STOP -> {
                Log.d(TAG, "STOP — release lock + stop self")
                releaseLocks()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
            else -> {
                // Service di-restart oleh Android (START_STICKY)
                Log.d(TAG, "Restart tanpa action — aktifkan default")
                startForeground(NOTIF_ID, buildNotification())
                acquireLocks()
            }
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        releaseLocks()
        super.onDestroy()
    }

    // ============================================================
    // NOTIFICATION
    // ============================================================
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Transfer Media",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notifikasi saat transfer media dari HP ke TV"
                setShowBadge(false)
            }
            val mgr = getSystemService(NotificationManager::class.java)
            mgr.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(): Notification {
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, CHANNEL_ID)
        } else {
            @Suppress("DEPRECATION")
            Notification.Builder(this)
        }

        return builder
            .setContentTitle("MASJID.IO — Transfer Aktif")
            .setContentText("Menerima file dari HP. WiFi & CPU dikunci agar tidak putus.")
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setOngoing(true)
            .setPriority(Notification.PRIORITY_LOW)
            .build()
    }

    // ============================================================
    // WIFI LOCK & WAKE LOCK
    // ============================================================
    private fun acquireLocks() {
        // WiFiLock — biar WiFi tidak mati saat layar TV off
        try {
            if (wifiLock == null) {
                val wifiMgr = applicationContext
                    .getSystemService(Context.WIFI_SERVICE) as WifiManager
                wifiLock = wifiMgr.createWifiLock(
                    WifiManager.WIFI_MODE_FULL_HIGH_PERF,
                    "masjid_io:transfer_wifi_lock"
                ).apply {
                    setReferenceCounted(false)
                    acquire()
                }
                Log.d(TAG, "WiFiLock acquired")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Gagal acquire WiFiLock: ${e.message}")
        }

        // WakeLock — biar CPU tetap jalan saat layar TV off
        try {
            if (wakeLock == null) {
                val powerMgr = applicationContext
                    .getSystemService(Context.POWER_SERVICE) as PowerManager
                wakeLock = powerMgr.newWakeLock(
                    PowerManager.PARTIAL_WAKE_LOCK,
                    "masjid_io:transfer_wake_lock"
                ).apply {
                    setReferenceCounted(false)
                    acquire(30 * 60 * 1000L) // Max 30 menit
                }
                Log.d(TAG, "WakeLock acquired (max 30 menit)")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Gagal acquire WakeLock: ${e.message}")
        }
    }

    private fun releaseLocks() {
        try {
            if (wifiLock?.isHeld == true) {
                wifiLock?.release()
                Log.d(TAG, "WiFiLock released")
            }
            wifiLock = null
        } catch (e: Exception) {
            Log.e(TAG, "Gagal release WiFiLock: ${e.message}")
        }

        try {
            if (wakeLock?.isHeld == true) {
                wakeLock?.release()
                Log.d(TAG, "WakeLock released")
            }
            wakeLock = null
        } catch (e: Exception) {
            Log.e(TAG, "Gagal release WakeLock: ${e.message}")
        }
    }
}
