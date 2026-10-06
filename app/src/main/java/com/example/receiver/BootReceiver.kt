package dev.andikune.masjidio.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import dev.andikune.masjidio.kiosk.AutoStartService

class BootReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "BootReceiver"
        const val PREFS_BOOT = "masjid_boot_prefs"
        const val KEY_JUST_BOOTED = "just_booted"
        const val KEY_BOOT_TIME = "boot_time"
        const val KEY_CHECK_INSTALL_PERM = "check_install_perm"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        Log.d(TAG, "Menerima action: $action")

        if (action == Intent.ACTION_BOOT_COMPLETED ||
            action == "android.intent.action.QUICKBOOT_POWERON" ||
            action == Intent.ACTION_LOCKED_BOOT_COMPLETED
        ) {
            // ============================================================
            // V1.04.425 — SET FLAG BOOT
            // ============================================================
            try {
                val prefs = context.getSharedPreferences(PREFS_BOOT, Context.MODE_PRIVATE)
                prefs.edit()
                    .putBoolean(KEY_JUST_BOOTED, true)
                    .putLong(KEY_BOOT_TIME, System.currentTimeMillis())
                    .putBoolean(KEY_CHECK_INSTALL_PERM, true)
                    .apply()
                Log.d(TAG, "Flag just_booted diset")
            } catch (e: Exception) {
                Log.e(TAG, "Gagal set flag boot: ${e.message}")
            }

            // ============================================================
            // START AUTO-START SERVICE
            // ============================================================
            try {
                val serviceIntent = Intent(context, AutoStartService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    ContextCompat.startForegroundService(context, serviceIntent)
                } else {
                    context.startService(serviceIntent)
                }
                Log.d(TAG, "AutoStartService diluncurkan")
            } catch (e: Exception) {
                Log.e(TAG, "Gagal meluncurkan service: ${e.message}")
            }
        }
    }
}
