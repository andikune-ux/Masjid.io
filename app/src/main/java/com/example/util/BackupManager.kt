package com.example.util

import android.content.Context
import android.os.Build
import android.os.Environment
import android.util.Log
import com.example.BuildConfig
import com.example.data.AppKnowledge
import com.example.data.model.AppSettings
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object BackupManager {

    private const val TAG = "BackupManager"
    private const val FOLDER_APP = "masjid.io"
    private const val FOLDER_BACKUP = "backup aman"

    data class BackupResult(
        val success: Boolean,
        val filePath: String? = null,
        val errorMessage: String? = null
    )

    fun generateBackupContent(settings: AppSettings): String {
        val timestamp = SimpleDateFormat(
            "dd-MM-yyyy HH:mm:ss",
            Locale.getDefault()
        ).format(Date())

        val version = try {
            BuildConfig.VERSION_NAME
        } catch (e: Exception) {
            "Unknown"
        }

        val header = """
============================================================
BACKUP AMAN - MASJID.IO
============================================================
Tanggal Export  : $timestamp
Versi Aplikasi  : $version
Device          : ${Build.MANUFACTURER} ${Build.MODEL}
Android Version : ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})
============================================================
        """.trimIndent()

        val userSettings = generateUserSettings(settings)

        return buildString {
            appendLine(header)
            appendLine()
            appendLine(userSettings)
            appendLine()
            appendLine(AppKnowledge.APP_STRUCTURE)
            appendLine()
            appendLine(AppKnowledge.APP_FEATURES)
            appendLine()
            appendLine(AppKnowledge.UPDATE_HISTORY)
            appendLine()
            appendLine(AppKnowledge.KNOWN_ISSUES)
            appendLine()
            appendLine(AppKnowledge.DEVELOPER_INSTRUCTION)
            appendLine()
            appendLine("=".repeat(60))
            appendLine("END OF BACKUP")
            appendLine("=".repeat(60))
        }
    }

    private fun generateUserSettings(settings: AppSettings): String {
        return """
============================================================
PENGATURAN USER SAAT INI
============================================================

IDENTITAS MASJID
- Nama Masjid : ${settings.mosqueName}

LOKASI
- Kota        : ${settings.city}
- Provinsi    : ${settings.province}
- Latitude    : ${settings.latitude}
- Longitude   : ${settings.longitude}

TAMPILAN
- Background Mode     : ${settings.backgroundMode}
- Keep Screen On      : ${settings.keepScreenOn}
- Kiosk Mode          : ${settings.kioskModeEnabled}
- Animasi             : ${settings.animationsEnabled}
- Burung Terbang      : ${settings.showBirdsAnimation}

AUDIO
- Mode Audio          : ${settings.audioMode}
- Volume Beep         : ${settings.beepVolume}%
- Jumlah Beep         : ${settings.beepCount}x
- File Adzan          : ${settings.adzanFile}
- Volume Adzan        : ${settings.adzanVolume}%

RUNNING TEXT
- Isi Running Text    : ${settings.runningText}

MODE FOKUS
- Durasi Mode Fokus   : ${settings.prayerFocusDurationMinutes} menit
- Jeda Iqamah         : ${settings.iqamahWaitMinutes} menit
- Countdown Qobliyah  : ${settings.qobliyahWaitMinutes} menit
        """.trimIndent()
    }

    fun saveBackupToFile(context: Context, content: String): BackupResult {
        return try {
            val dateString = SimpleDateFormat(
                "dd-MM-yyyy",
                Locale.getDefault()
            ).format(Date())

            val fileName = "Backup Aman-masjid.io-$dateString.TXT"

            val backupDir = File(
                Environment.getExternalStorageDirectory(),
                "$FOLDER_APP/$FOLDER_BACKUP"
            )

            if (!backupDir.exists()) {
                val created = backupDir.mkdirs()
                if (!created) {
                    Log.w(TAG, "Folder tidak bisa dibuat: ${backupDir.absolutePath}")
                }
            }

            val file = File(backupDir, fileName)
            file.writeText(content, Charsets.UTF_8)

            Log.d(TAG, "Backup tersimpan: ${file.absolutePath}")
            BackupResult(success = true, filePath = file.absolutePath)
        } catch (e: Exception) {
            Log.e(TAG, "Backup gagal: ${e.message}", e)
            BackupResult(
                success = false,
                errorMessage = e.message ?: "Terjadi kesalahan tidak diketahui"
            )
        }
    }

    fun isBackupFolderAccessible(): Boolean {
        return try {
            val dir = File(
                Environment.getExternalStorageDirectory(),
                "$FOLDER_APP/$FOLDER_BACKUP"
            )
            dir.exists() || dir.mkdirs() || Environment.getExternalStorageDirectory().canWrite()
        } catch (e: Exception) {
            false
        }
    }
}
