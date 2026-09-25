package com.example.kiosk

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.example.MainActivity

class WatchdogService : Service() {

    companion object {
        private const val TAG = "WatchdogService"
        private const val CHECK_INTERVAL_MS = 30_000L
    }

    private val handler = Handler(Looper.getMainLooper())
    private val checkRunnable = object : Runnable {
        override fun run() {
            try {
                if (!KioskManager.isMainActivityForeground) {
                    Log.d(TAG, "MainActivity tidak aktif - meluncurkan ulang")
                    val intent = Intent(this@WatchdogService, MainActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    }
                    startActivity(intent)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Watchdog error: ${e.message}")
            } finally {
                handler.postDelayed(this, CHECK_INTERVAL_MS)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        handler.postDelayed(checkRunnable, CHECK_INTERVAL_MS)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = START_STICKY

    override fun onDestroy() {
        handler.removeCallbacks(checkRunnable)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
