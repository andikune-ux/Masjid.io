package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log
import com.example.BuildConfig
import com.example.data.AppKnowledge
import com.example.data.UpdateHistory
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
        val errorMessage: String? = null,
        val needPermission: Boolean = false
    )

    /**
     * Cek apakah butuh izin MANAGE_EXTERNAL_STORAGE (Android 11+)
     */
    fun needsStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            !Environment.isExternalStorageManager()
        } else {
            false
        }
    }

    /**
     * Buka halaman pengaturan izin "Akses semua file"
     */
    fun openPermissionSettings(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                    data = Uri.parse("package:${context.packageName}")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            } catch (e: Exception) {
                try {
                    val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                } catch (e2: Exception) {
                    Log.e(TAG, "Gagal buka permission settings: ${e2.message}")
                }
            }
        }
    }

    /**
     * Generate isi file backup dalam bentuk TXT.
     * Parameter buildHistoryText: teks dari BuildHistoryFetcher (bisa null).
     */
    fun generateBackupContent(
        settings: AppSettings,
        buildHistoryText: String? = null
    ): String {
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
        val dynamicUpdateHistory = generateDynamicUpdateHistory()

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
            appendLine(dynamicUpdateHistory)
            appendLine()
            appendLine(AppKnowledge.KNOWN_ISSUES)
            appendLine()
            appendLine(AppKnowledge.BUILD_ERROR_HISTORY)
            appendLine()
            if (!buildHistoryText.isNullOrBlank()) {
                appendLine(buildHistoryText)
                appendLine()
            }
            appendLine(AppKnowledge.DEVELOPER_INSTRUCTION)
            appendLine()
            appendLine(AppKnowledge.MEMORY_INSTRUCTION)
            appendLine()
            appendLine("=".repeat(60))
            appendLine("END OF BACKUP")
            appendLine("=".repeat(60))
        }
    }

    /**
     * Generate riwayat update dari UpdateHistory.kt (dinamis).
     */
    private fun generateDynamicUpdateHistory(): String {
        return buildString {
            appendLine("=".repeat(60))
            appendLine("RIWAYAT UPDATE LENGKAP (DARI UpdateHistory.kt)")
            appendLine("=".repeat(60))
            appendLine()
            appendLine("Total: ${UpdateHistory.entries.size} versi tercatat")
            appendLine()

            UpdateHistory.entries.forEach { entry ->
                appendLine("┌─ ${entry.version}  (${entry.date})")
                appendLine("│  ${entry.title}")
                appendLine("│")
                entry.features.forEach { feature ->
                    appendLine("│  • $feature")
                }
                appendLine("└─")
                appendLine()
            }
            appendLine("=".repeat(60))
        }
    }

    /**
     * Generate bagian pengaturan user saat ini.
     */
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
- Durasi Beep         : ${settings.beepDurationMs}ms
- Jeda Beep           : ${settings.beepIntervalMs}ms
- File Adzan          : ${settings.adzanFile}
- Volume Adzan        : ${settings.adzanVolume}%

VIDEO & FOTO
- Video Enabled       : ${settings.videoEnabled}
- Video Smart Full    : ${settings.videoSmartFullscreen}
- Photo Slideshow     : ${settings.photoSlideshowEnabled}
- Jumlah Foto         : ${settings.photoSlideshowUris.size} foto
- Interval Foto       : ${settings.photoSlideshowIntervalSeconds} detik

RUNNING TEXT
- Isi Running Text    : ${settings.runningText}

MODE FOKUS
- Durasi Mode Fokus   : ${settings.prayerFocusDurationMinutes} menit
- Jeda Iqamah         : ${settings.iqamahWaitMinutes} menit
- Countdown Qobliyah  : ${settings.qobliyahWaitMinutes} menit

WHATSAPP FONNTE
- WA Report Enabled   : ${settings.whatsappReportEnabled}
- Token Fonnte        : ${if (settings.fonnteToken.isBlank()) "(kosong)" else "(terisi)"}
- Group ID            : ${if (settings.fonnteGroupId.isBlank()) "(kosong)" else "(terisi)"}
        """.trimIndent()
    }

    /**
     * Simpan file backup ke folder publik:
     * /sdcard/masjid.io/backup aman/Backup Aman-masjid.io-DD-MM-YYYY.TXT
     */
    fun saveBackupToFile(context: Context, content: String): BackupResult {
        // Cek permission Android 11+
        if (needsStoragePermission()) {
            return BackupResult(
                success = false,
                errorMessage = "Izin akses penyimpanan diperlukan. Tap tombol IZIN untuk membuka pengaturan.",
                needPermission = true
            )
        }

        return try {
            val dateString = SimpleDateFormat(
                "dd-MM-yyyy",
                Locale.getDefault()
            ).format(Date())

            val fileName = "Backup Aman-masjid.io-$dateString.TXT"
            val baseDir = Environment.getExternalStorageDirectory()
            val backupDir = File(baseDir, "$FOLDER_APP/$FOLDER_BACKUP")

            if (!backupDir.exists()) {
                val created = backupDir.mkdirs()
                if (!created) {
                    Log.w(TAG, "Folder gagal dibuat di external, coba fallback")
                    return saveFallback(context, content)
                }
            }

            val file = File(backupDir, fileName)
            file.writeText(content, Charsets.UTF_8)

            Log.d(TAG, "Backup tersimpan: ${file.absolutePath}")
            BackupResult(success = true, filePath = file.absolutePath)
        } catch (e: Exception) {
            Log.e(TAG, "Backup gagal: ${e.message}", e)
            saveFallback(context, content)
        }
    }

    /**
     * Fallback: simpan ke folder khusus app (selalu bisa diakses).
     */
    private fun saveFallback(context: Context, content: String): BackupResult {
        return try {
            val dateString = SimpleDateFormat(
                "dd-MM-yyyy",
                Locale.getDefault()
            ).format(Date())

            val fileName = "Backup Aman-masjid.io-$dateString.TXT"
            val appDir = File(context.filesDir, "$FOLDER_APP/$FOLDER_BACKUP")
            if (!appDir.exists()) appDir.mkdirs()

            val file = File(appDir, fileName)
            file.writeText(content, Charsets.UTF_8)

            Log.d(TAG, "Backup (fallback) tersimpan: ${file.absolutePath}")
            BackupResult(success = true, filePath = file.absolutePath)
        } catch (e: Exception) {
            Log.e(TAG, "Fallback juga gagal: ${e.message}", e)
            BackupResult(
                success = false,
                errorMessage = e.message ?: "Gagal menyimpan file backup"
            )
        }
    }
}
