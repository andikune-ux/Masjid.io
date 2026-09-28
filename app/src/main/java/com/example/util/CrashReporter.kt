package com.example.util

import android.content.Context
import android.os.Build
import android.util.Log
import com.example.BuildConfig
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CrashReporter {

    private const val TAG = "CrashReporter"
    private const val CRASH_FOLDER = "crashes"
    private const val MAX_CRASH_FILES = 20

    private var appContext: Context? = null
    private var previousHandler: Thread.UncaughtExceptionHandler? = null

    /**
     * Inisialisasi CrashReporter. Panggil di MainActivity.onCreate().
     */
    fun init(context: Context) {
        appContext = context.applicationContext

        previousHandler = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                saveCrashLog(throwable, thread)
            } catch (e: Exception) {
                Log.e(TAG, "Gagal menyimpan crash: ${e.message}")
            }

            // Teruskan ke handler sebelumnya (agar sistem tetap proses crash)
            previousHandler?.uncaughtException(thread, throwable)
        }

        Log.d(TAG, "CrashReporter aktif")
    }

    private fun saveCrashLog(throwable: Throwable, thread: Thread) {
        val ctx = appContext ?: return

        val timestamp = SimpleDateFormat(
            "dd-MM-yyyy HH:mm:ss",
            Locale.getDefault()
        ).format(Date())

        val sw = StringWriter()
        throwable.printStackTrace(PrintWriter(sw))
        val stackTrace = sw.toString()

        val appVersion = try {
            BuildConfig.VERSION_NAME
        } catch (e: Exception) {
            "Unknown"
        }

        val content = """
============================================================
CRASH LOG - MASJID.IO
============================================================
Waktu       : $timestamp
Versi App   : $appVersion
Device      : ${Build.MANUFACTURER} ${Build.MODEL}
Android     : ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})
Thread      : ${thread.name}
Error       : ${throwable.javaClass.simpleName}
Pesan       : ${throwable.message ?: "(tidak ada pesan)"}

STACK TRACE:
$stackTrace
============================================================
        """.trimIndent()

        val crashDir = File(ctx.filesDir, CRASH_FOLDER)
        if (!crashDir.exists()) crashDir.mkdirs()

        val fileName = "crash_${System.currentTimeMillis()}.txt"
        val file = File(crashDir, fileName)
        file.writeText(content, Charsets.UTF_8)

        Log.d(TAG, "Crash tersimpan: ${file.absolutePath}")

        cleanupOldCrashes(crashDir)
    }

    private fun cleanupOldCrashes(dir: File) {
        try {
            val files = dir.listFiles()?.sortedByDescending { it.lastModified() } ?: return
            if (files.size > MAX_CRASH_FILES) {
                files.drop(MAX_CRASH_FILES).forEach { it.delete() }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Gagal cleanup crash lama: ${e.message}")
        }
    }

    /**
     * Ambil daftar semua crash yang tersimpan.
     */
    fun getCrashHistory(context: Context): List<CrashEntry> {
        val crashDir = File(context.filesDir, CRASH_FOLDER)
        if (!crashDir.exists()) return emptyList()

        return crashDir.listFiles()
            ?.sortedByDescending { it.lastModified() }
            ?.mapNotNull { file ->
                try {
                    CrashEntry(
                        fileName = file.name,
                        timestamp = file.lastModified(),
                        content = file.readText(Charsets.UTF_8)
                    )
                } catch (e: Exception) {
                    null
                }
            } ?: emptyList()
    }

    /**
     * Hapus 1 file crash.
     */
    fun deleteCrash(context: Context, fileName: String) {
        try {
            val file = File(File(context.filesDir, CRASH_FOLDER), fileName)
            if (file.exists()) file.delete()
        } catch (e: Exception) {
            Log.e(TAG, "Gagal hapus crash: ${e.message}")
        }
    }

    /**
     * Hapus semua file crash.
     */
    fun deleteAllCrashes(context: Context) {
        try {
            val crashDir = File(context.filesDir, CRASH_FOLDER)
            crashDir.listFiles()?.forEach { it.delete() }
        } catch (e: Exception) {
            Log.e(TAG, "Gagal hapus semua crash: ${e.message}")
        }
    }

    /**
     * Cek apakah ada crash yang tersimpan.
     */
    fun hasAnyCrash(context: Context): Boolean {
        val crashDir = File(context.filesDir, CRASH_FOLDER)
        return crashDir.exists() && (crashDir.listFiles()?.isNotEmpty() == true)
    }
}

data class CrashEntry(
    val fileName: String,
    val timestamp: Long,
    val content: String
)
