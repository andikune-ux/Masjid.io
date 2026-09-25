package com.example.kiosk

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.util.Log

object KioskManager {

    private const val TAG = "KioskManager"

    @Volatile
    var isMainActivityForeground: Boolean = false

    @Volatile
    private var isKioskActive: Boolean = false

    fun enableKiosk(activity: Activity) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val am = activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                if (am.lockTaskModeState == ActivityManager.LOCK_TASK_MODE_NONE) {
                    activity.startLockTask()
                    isKioskActive = true
                    Log.d(TAG, "Kiosk mode AKTIF")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Gagal aktifkan kiosk: ${e.message}")
        }
    }

    fun disableKiosk(activity: Activity) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val am = activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                if (am.lockTaskModeState != ActivityManager.LOCK_TASK_MODE_NONE) {
                    activity.stopLockTask()
                    isKioskActive = false
                    Log.d(TAG, "Kiosk mode NONAKTIF")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Gagal nonaktifkan kiosk: ${e.message}")
        }
    }

    fun isKioskActive(): Boolean = isKioskActive
}
