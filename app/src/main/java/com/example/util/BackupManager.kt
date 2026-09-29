package com.example.util

import android.content.Context
import android.os.Build
import android.os.Environment
import com.example.data.AppKnowledge
import com.example.data.UpdateHistory
import com.example.data.local.SettingsRepository
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Backup Aman — Ekspor SEMUA data + ISI KODE SEMUA FILE.
 *
 * Output: /sdcard/masjid.io/backup aman/Backup Aman-masjid.io-DD-MM-YYYY.TXT
 *
 * Cara C (folder lokal dulu, fallback GitHub API):
 *   1. Kalau /sdcard/masjid.io/source/ ada -> baca dari situ
 *   2. Kalau tidak ada -> fetch dari GitHub API (repo public)
 *   3. Kalau dua-duanya gagal -> kasih pesan jelas di TXT
 *
 * PENTING: fungsi `backup()` sekarang SUSPEND.
 * Pemanggil WAJIB di dalam Coroutine Scope.
 */
object BackupManager {

    private val DATE_FMT = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    private val TIME_FMT = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())

    private val SOURCE_EXTENSIONS = setOf(
        "kt", "java", "xml", "gradle", "kts",
        "toml", "yml", "yaml", "properties", "pro"
    )

    /**
     * Jalankan backup lengkap.
     */
    suspend fun backup(
        context: Context,
        onProgress: (tahap: String, current: Int, total: Int, info: String) -> Unit = { _, _, _, _ -> }
    ): File? {
        onProgress("Siapkan data", 0, 1, "")

        val sourceMap = collectSourceCode(context, onProgress)

        onProgress("Susun TXT", 0, 1, "")
        val text = buildBackupText(context, sourceMap)
        val fileName = "Backup Aman-masjid.io-${DATE_FMT.format(Date())}.TXT"

        // Prioritas 1: /sdcard/masjid.io/backup aman/
        runCatching {
            val extDir = File(
                Environment.getExternalStorageDirectory(),
                "masjid.io/backup aman"
            )
            if (!extDir.exists()) extDir.mkdirs()
            val f = File(extDir, fileName)
            f.writeText(text)
            return f
        }

        // Prioritas 2: app external files dir
        return runCatching {
            val fallback = File(context.getExternalFilesDir(null), fileName)
            fallback.writeText(text)
            fallback
        }.getOrNull()
    }

    /**
     * Kumpulkan source code (Cara C):
     *   1. Coba folder lokal
     *   2. Fallback GitHub API
     */
    private suspend fun collectSourceCode(
        context: Context,
        onProgress: (tahap: String, current: Int, total: Int, info: String) -> Unit
    ): Map<String, String> {

        // === 1. Coba folder lokal ===
        val localRoot = findLocalSourceRoot(context)
        if (localRoot != null) {
            val files = localRoot.walkTopDown()
                .filter { it.isFile }
                .filter { it.extension.lowercase() in SOURCE_EXTENSIONS }
                .filter { !it.absolutePath.contains("/build/") }
                .sortedBy { it.absolutePath }
                .toList()

            if (files.isNotEmpty()) {
                onProgress("Baca folder lokal", 0, files.size, localRoot.absolutePath)
                val map = LinkedHashMap<String, String>()
                files.forEachIndexed { idx, f ->
                    val rel = f.absolutePath
                        .removePrefix(localRoot.absolutePath)
                        .trimStart(File.separatorChar)
                    onProgress("Baca folder lokal", idx + 1, files.size, rel)
                    map[rel] = runCatching { f.readText() }.getOrDefault("[GAGAL BACA]")
                }
                return map
            }
        }

        // === 2. Fallback GitHub API ===
        onProgress("GitHub API", 0, 1, "Menghubungi github.com…")
        val fromApi = runCatching {
            GithubSourceFetcher.fetchAllFiles { cur, total, path ->
                onProgress("GitHub API", cur, total, path)
            }
        }.getOrNull() ?: emptyMap()

        return fromApi
    }

    /** Cari folder sumber kode lokal. */
    private fun findLocalSourceRoot(context: Context): File? {
        val candidates = listOf(
            File(Environment.getExternalStorageDirectory(), "masjid.io/source"),
            File(context.filesDir, "source")
        )
        return candidates.firstOrNull { it.exists() && it.isDirectory }
    }

    /** Bangun isi backup lengkap. */
    private fun buildBackupText(
        context: Context,
        sourceMap: Map<String, String>
    ): String = buildString {

        // ==================== HEADER ====================
        appendLine("=".repeat(60))
        appendLine("BACKUP AMAN - MASJID.IO")
        appendLine("=".repeat(60))
        appendLine("Tanggal Export  : ${TIME_FMT.format(Date())}")
        appendLine("Versi Aplikasi  : ${getAppVersion(context)}")
        appendLine("Device          : ${Build.MANUFACTURER} ${Build.MODEL}")
        appendLine("Android Version : ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})")
        appendLine("=".repeat(60))
        appendLine()

        // ==================== PENGATURAN USER ====================
        appendLine("=".repeat(60))
        appendLine("PENGATURAN USER SAAT INI")
        appendLine("=".repeat(60))
        appendLine(runCatching { SettingsRepository.exportSummary(context) }
            .getOrDefault("(tidak bisa baca pengaturan)"))
        appendLine()

        // ==================== MEMORY KNOWLEDGE ====================
        appendLine("=".repeat(60))
        appendLine("MEMORY KNOWLEDGE & INSTRUCTION")
        appendLine("=".repeat(60))
        appendLine(AppKnowledge.MEMORY_KNOWLEDGE)
        appendLine()

        // ==================== STRUKTUR APLIKASI ====================
        appendLine("=".repeat(60))
        appendLine("STRUKTUR APLIKASI MASJID.IO")
        appendLine("=".repeat(60))
        appendLine(AppKnowledge.STRUCTURE)
        appendLine()

        // ==================== FITUR ====================
        appendLine("=".repeat(60))
        appendLine("FITUR APLIKASI MASJID.IO")
        appendLine("=".repeat(60))
        appendLine(AppKnowledge.FEATURES)
        appendLine()

        // ==================== RIWAYAT UPDATE ====================
        appendLine("=".repeat(60))
        appendLine("RIWAYAT UPDATE MASJID.IO")
        appendLine("=".repeat(60))
        appendLine(runCatching { UpdateHistory.getFullText() }
            .getOrDefault("(tidak bisa baca riwayat update)"))
        appendLine()

        // ==================== BUILD ERROR HISTORY ====================
        appendLine("=".repeat(60))
        appendLine("RIWAYAT BUILD ERROR & SOLUSI")
        appendLine("=".repeat(60))
        appendLine(AppKnowledge.BUILD_ERROR_HISTORY)
        appendLine()

        // ==================== BUILD GITHUB ====================
        appendLine("=".repeat(60))
        appendLine("RIWAYAT BUILD (GitHub Actions)")
        appendLine("=".repeat(60))
        appendLine(runCatching { BuildHistoryFetcher.getCachedSummary() }
            .getOrDefault("(belum ada data build history)"))
        appendLine()

        // ==================== FULL SOURCE CODE ====================
        appendLine("=".repeat(60))
        appendLine("FULL SOURCE CODE — SEMUA FILE")
        appendLine("=".repeat(60))
        appendLine("File di bawah ini adalah SOURCE CODE LENGKAP aplikasi.")
        appendLine("Setiap file dipisahkan marker ---BEGIN--- dan ---END---.")
        appendLine("AI pembaca: baca setiap file untuk paham struktur.")
        appendLine("=".repeat(60))
        appendLine()

        if (sourceMap.isEmpty()) {
            appendLine("⚠️ SOURCE CODE TIDAK BISA DIAMBIL.")
            appendLine()
            appendLine("Kemungkinan penyebab:")
            appendLine("- Tidak ada folder /sdcard/masjid.io/source/")
            appendLine("- Tidak ada koneksi internet ke GitHub API")
            appendLine("- Repo private (butuh token)")
            appendLine()
            appendLine("Cara aktifkan:")
            appendLine("1. Download repo dari GitHub (menu → Download ZIP)")
            appendLine("2. Extract ZIP")
            appendLine("3. Copy folder 'app/src/main' ke:")
            appendLine("   /sdcard/masjid.io/source/")
            appendLine("4. Jalankan Backup Aman lagi.")
        } else {
            appendLine("📊 Total file: ${sourceMap.size}")
            appendLine()
            sourceMap.forEach { (path, content) ->
                appendLine("---BEGIN--- $path")
                appendLine(content)
                appendLine("---END--- $path")
                appendLine()
            }
        }

        // ==================== FOOTER ====================
        appendLine()
        appendLine("=".repeat(60))
        appendLine("END OF BACKUP")
        appendLine("=".repeat(60))
    }

    private fun getAppVersion(context: Context): String = runCatching {
        context.packageManager
            .getPackageInfo(context.packageName, 0)
            .versionName ?: "unknown"
    }.getOrDefault("unknown")
}
