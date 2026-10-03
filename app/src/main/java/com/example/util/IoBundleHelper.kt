package com.example.util

import android.content.Context
import android.os.Environment
import android.util.Log
import com.example.data.model.AppSettings
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

/**
 * IoBundleHelper — Bikin & baca file template .iO
 *
 * File .iO sebenarnya adalah file ZIP yang di-rename.
 * Isinya:
 *   ├── settings.json    → semua pengaturan
 *   ├── metadata.json    → info pengirim + riwayat transfer
 *   └── media/
 *       ├── qris.jpg
 *       ├── logo.png
 *       ├── background.jpg
 *       ├── video.mp4
 *       └── slideshow/
 *           ├── foto_1.jpg
 *           └── foto_2.jpg
 *
 * Lokasi simpan: /sdcard/masjid.io/Terima/{Merk HP}-{dd-MM-yyyy HH.mm}.iO
 */
object IoBundleHelper {

    private const val TAG = "IoBundleHelper"
    private const val FOLDER_APP = "masjid.io"
    private const val FOLDER_TERIMA = "Terima"
    private const val EXT = ".iO"

    // ============================================================
    // DATA CLASS
    // ============================================================
    data class FailedFileInfo(
        val displayName: String,
        val fieldKey: String,
        val reason: String,
        val exceptionClass: String,
        val stackTrace: String
    )

    data class BundleMetadata(
        val senderDevice: String,
        val senderRole: String,
        val senderVersion: String,
        val receivedAt: String,
        val settingsSuccess: Boolean,
        val mediaTotal: Int,
        val mediaSuccess: Int,
        val mediaFailed: Int,
        val photoCount: Int,
        val videoCount: Int,
        val failedFiles: List<FailedFileInfo>
    )

    /**
     * Info file .iO untuk ditampilkan di daftar UI.
     */
    data class BundleInfo(
        val file: File,
        val fileName: String,
        val filePath: String,
        val fileSizeBytes: Long,
        val fileSizeText: String,
        val lastModified: Long,
        val metadata: BundleMetadata?
    ) {
        val summaryLine: String
            get() = metadata?.let { m ->
                "${m.mediaSuccess} sukses · ${m.mediaFailed} gagal · " +
                        "📷 ${m.photoCount} foto · 🎬 ${m.videoCount} video"
            } ?: "(metadata tidak terbaca)"
    }

    data class BundleResult(
        val success: Boolean,
        val filePath: String? = null,
        val errorMessage: String? = null,
        val needPermission: Boolean = false
    )

    data class RestoreResult(
        val success: Boolean,
        val settings: AppSettings? = null,
        val restoredMediaCount: Int = 0,
        val errorMessage: String? = null
    )

    // ============================================================
    // PERMISSION CHECK
    // ============================================================
    fun needsStoragePermission(): Boolean {
        return BackupManager.needsStoragePermission()
    }

    fun openPermissionSettings(context: Context) {
        BackupManager.openPermissionSettings(context)
    }

    // ============================================================
    // FOLDER /sdcard/masjid.io/Terima/
    // ============================================================
    fun getTerimaDir(context: Context): File {
        return try {
            val externalDir = File(
                Environment.getExternalStorageDirectory(),
                "$FOLDER_APP/$FOLDER_TERIMA"
            )
            if (!externalDir.exists()) externalDir.mkdirs()
            if (externalDir.exists() && externalDir.canWrite()) {
                externalDir
            } else {
                fallbackDir(context)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Eksternal gagal, pakai fallback: ${e.message}")
            fallbackDir(context)
        }
    }

    private fun fallbackDir(context: Context): File {
        val dir = File(context.filesDir, "$FOLDER_APP/$FOLDER_TERIMA")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    // ============================================================
    // NAMA FILE .iO
    // ============================================================
    /**
     * Format: {Merk HP}-{dd-MM-yyyy HH.mm}.iO
     * Contoh: Infinix X6827-03-10-2026 14.20.iO
     */
    fun buildBundleFileName(senderDevice: String): String {
        val timestamp = SimpleDateFormat("dd-MM-yyyy HH.mm", Locale.getDefault()).format(Date())
        val cleanSender = sanitizeFileName(senderDevice.ifBlank { "Masjid.io" })
        return "$cleanSender-$timestamp$EXT"
    }

    private fun sanitizeFileName(name: String): String {
        return name.replace(Regex("[^a-zA-Z0-9 ._-]"), "_").trim()
    }

    // ============================================================
    // BIKIN BUNDLE .iO
    // ============================================================
    /**
     * Bikin file .iO (ZIP) berisi settings.json + metadata.json + media/.
     *
     * @param context Context
     * @param settings AppSettings saat ini
     * @param metadata BundleMetadata (info pengirim + transfer summary)
     * @param mediaFiles Map<fieldKey, File>  — file media yang sudah tersimpan di TV
     * @return BundleResult
     */
    fun createBundle(
        context: Context,
        settings: AppSettings,
        metadata: BundleMetadata,
        mediaFiles: Map<String, File>
    ): BundleResult {
        if (needsStoragePermission()) {
            return BundleResult(
                success = false,
                errorMessage = "Izin storage diperlukan",
                needPermission = true
            )
        }

        return try {
            val dir = getTerimaDir(context)
            val fileName = buildBundleFileName(metadata.senderDevice)
            val targetFile = File(dir, fileName)

            // Tulis ZIP
            FileOutputStream(targetFile).use { fos ->
                ZipOutputStream(fos).use { zos ->
                    // === 1. settings.json ===
                    writeEntry(zos, "settings.json", SettingsTransferHelper.serializeSettings(settings))

                    // === 2. metadata.json ===
                    writeEntry(zos, "metadata.json", buildMetadataJson(metadata))

                    // === 3. media files ===
                    mediaFiles.forEach { (fieldKey, file) ->
                        if (file.exists() && file.length() > 0) {
                            val zipPath = "media/${sanitizeMediaName(fieldKey, file)}"
                            writeFileEntry(zos, zipPath, file)
                        }
                    }
                }
            }

            Log.d(TAG, "✅ Bundle tersimpan: ${targetFile.absolutePath} (${targetFile.length()} bytes)")

            BundleResult(
                success = true,
                filePath = targetFile.absolutePath
            )
        } catch (e: Exception) {
            Log.e(TAG, "Gagal bikin bundle: ${e.message}", e)
            BundleResult(
                success = false,
                errorMessage = e.message ?: "Gagal bikin bundle"
            )
        }
    }

    private fun sanitizeMediaName(fieldKey: String, file: File): String {
        val safeKey = fieldKey.replace(Regex("[^a-zA-Z0-9._-]"), "_")
        val ext = file.extension.ifBlank { "bin" }
        return "${safeKey}.$ext"
    }

    private fun writeEntry(zos: ZipOutputStream, path: String, content: String) {
        val entry = ZipEntry(path)
        zos.putNextEntry(entry)
        zos.write(content.toByteArray(Charsets.UTF_8))
        zos.closeEntry()
    }

    private fun writeFileEntry(zos: ZipOutputStream, path: String, file: File) {
        try {
            val entry = ZipEntry(path)
            zos.putNextEntry(entry)
            FileInputStream(file).use { fis ->
                val buffer = ByteArray(8192)
                var len: Int
                while (fis.read(buffer).also { len = it } > 0) {
                    zos.write(buffer, 0, len)
                }
            }
            zos.closeEntry()
        } catch (e: Exception) {
            Log.w(TAG, "Skip media $path: ${e.message}")
        }
    }

    private fun buildMetadataJson(m: BundleMetadata): String {
        val root = JSONObject()
        root.put("senderDevice", m.senderDevice)
        root.put("senderRole", m.senderRole)
        root.put("senderVersion", m.senderVersion)
        root.put("receivedAt", m.receivedAt)
        root.put("settingsSuccess", m.settingsSuccess)
        root.put("mediaTotal", m.mediaTotal)
        root.put("mediaSuccess", m.mediaSuccess)
        root.put("mediaFailed", m.mediaFailed)
        root.put("photoCount", m.photoCount)
        root.put("videoCount", m.videoCount)

        val failedArr = JSONArray()
        m.failedFiles.forEach { f ->
            failedArr.put(JSONObject().apply {
                put("displayName", f.displayName)
                put("fieldKey", f.fieldKey)
                put("reason", f.reason)
                put("exceptionClass", f.exceptionClass)
                put("stackTrace", f.stackTrace)
            })
        }
        root.put("failedFiles", failedArr)

        return root.toString(2)
    }
    // ============================================================
    // BACA BUNDLE .iO
    // ============================================================
    /**
     * Baca isi file .iO. Return Map<zipPath, ByteArray>.
     * Contoh key: "settings.json", "metadata.json", "media/qris.jpg"
     */
    fun readBundle(bundleFile: File): Map<String, ByteArray> {
        val result = mutableMapOf<String, ByteArray>()
        try {
            ZipInputStream(FileInputStream(bundleFile)).use { zis ->
                var entry: ZipEntry? = zis.nextEntry
                while (entry != null) {
                    val path = entry.name
                    if (!entry.isDirectory) {
                        val bytes = zis.readBytes()
                        result[path] = bytes
                    }
                    zis.closeEntry()
                    entry = zis.nextEntry
                }
            }
            Log.d(TAG, "Bundle dibaca: ${result.size} entry")
        } catch (e: Exception) {
            Log.e(TAG, "Gagal baca bundle: ${e.message}", e)
        }
        return result
    }

    // ============================================================
    // PARSE METADATA.JSON
    // ============================================================
    fun parseMetadata(bundleFile: File): BundleMetadata? {
        return try {
            val content = readBundle(bundleFile)
            val metaBytes = content["metadata.json"] ?: return null
            val json = JSONObject(String(metaBytes, Charsets.UTF_8))

            val failedArr = json.optJSONArray("failedFiles") ?: JSONArray()
            val failures = mutableListOf<FailedFileInfo>()
            for (i in 0 until failedArr.length()) {
                val obj = failedArr.optJSONObject(i) ?: continue
                failures.add(FailedFileInfo(
                    displayName = obj.optString("displayName", ""),
                    fieldKey = obj.optString("fieldKey", ""),
                    reason = obj.optString("reason", ""),
                    exceptionClass = obj.optString("exceptionClass", ""),
                    stackTrace = obj.optString("stackTrace", "")
                ))
            }

            BundleMetadata(
                senderDevice = json.optString("senderDevice", "Unknown"),
                senderRole = json.optString("senderRole", "HP"),
                senderVersion = json.optString("senderVersion", "V?"),
                receivedAt = json.optString("receivedAt", ""),
                settingsSuccess = json.optBoolean("settingsSuccess", false),
                mediaTotal = json.optInt("mediaTotal", 0),
                mediaSuccess = json.optInt("mediaSuccess", 0),
                mediaFailed = json.optInt("mediaFailed", 0),
                photoCount = json.optInt("photoCount", 0),
                videoCount = json.optInt("videoCount", 0),
                failedFiles = failures
            )
        } catch (e: Exception) {
            Log.w(TAG, "Metadata tidak terbaca: ${e.message}")
            null
        }
    }

    // ============================================================
    // DAFTAR SEMUA BUNDLE .iO
    // ============================================================
    /**
     * Ambil semua file .iO di folder Terima.
     * Diurutkan dari terbaru (lastModified desc).
     */
    fun listBundles(context: Context): List<BundleInfo> {
        return try {
            val dir = getTerimaDir(context)
            if (!dir.exists()) return emptyList()

            dir.listFiles()
                ?.filter { it.isFile && it.name.endsWith(EXT, ignoreCase = true) }
                ?.sortedByDescending { it.lastModified() }
                ?.map { file ->
                    BundleInfo(
                        file = file,
                        fileName = file.name,
                        filePath = file.absolutePath,
                        fileSizeBytes = file.length(),
                        fileSizeText = formatSize(file.length()),
                        lastModified = file.lastModified(),
                        metadata = parseMetadata(file)
                    )
                } ?: emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Gagal list bundles: ${e.message}")
            emptyList()
        }
    }

    // ============================================================
    // HAPUS BUNDLE
    // ============================================================
    fun deleteBundle(bundleFile: File): Boolean {
        return try {
            if (bundleFile.exists()) {
                val ok = bundleFile.delete()
                Log.d(TAG, "Hapus bundle: ${bundleFile.name} → $ok")
                ok
            } else false
        } catch (e: Exception) {
            Log.e(TAG, "Gagal hapus bundle: ${e.message}")
            false
        }
    }

    fun deleteBundleByPath(path: String): Boolean {
        return try {
            deleteBundle(File(path))
        } catch (e: Exception) {
            false
        }
    }

    // ============================================================
    // AMBIL NAMA PENGIRIM DARI FILE .iO
    // ============================================================
    fun getSenderName(bundleFile: File): String {
        return try {
            parseMetadata(bundleFile)?.senderDevice ?: "Unknown"
        } catch (e: Exception) {
            "Unknown"
        }
    }

    // ============================================================
    // HELPER: FORMAT UKURAN FILE
    // ============================================================
    fun formatSize(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "${bytes / 1024} KB"
            bytes < 1024 * 1024 * 1024 -> String.format("%.1f MB", bytes / (1024.0 * 1024.0))
            else -> String.format("%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0))
        }
    }

    // ============================================================
    // RESTORE BUNDLE → APPLY KE SETTINGS + MEDIA
    // ============================================================
    /**
     * Baca bundle dan apply ke Settings + extract media.
     *
     * @param mode "TAMBAH" (gabung) atau "TIMPA" (replace)
     *             Default: TAMBAH (sesuai keputusan Andi)
     */
    fun restoreBundle(
        context: Context,
        bundleFile: File,
        currentSettings: AppSettings,
        mode: String = "TAMBAH"
    ): RestoreResult {
        return try {
            val content = readBundle(bundleFile)
            if (content.isEmpty()) {
                return RestoreResult(
                    success = false,
                    errorMessage = "Bundle kosong atau rusak"
                )
            }

            // ===== 1. PARSE SETTINGS =====
            val settingsBytes = content["settings.json"]
                ?: return RestoreResult(
                    success = false,
                    errorMessage = "settings.json tidak ada di bundle"
                )

            val settingsJson = String(settingsBytes, Charsets.UTF_8)
            val newSettings = SettingsTransferHelper.deserializeSettings(
                settingsJson,
                currentSettings
            ) ?: return RestoreResult(
                success = false,
                errorMessage = "Settings tidak bisa dibaca"
            )

            // ===== 2. EXTRACT MEDIA KE filesDir/masjid_io/ =====
            var restoredCount = 0
            content.forEach { (zipPath, bytes) ->
                if (zipPath.startsWith("media/") && bytes.isNotEmpty()) {
                    val fileName = zipPath.removePrefix("media/")
                    val folder = determineFolderFromName(fileName)
                    val savedPath = saveMediaToInternal(context, folder, fileName, bytes)
                    if (savedPath != null) {
                        restoredCount++
                        Log.d(TAG, "Restore media: $fileName → $savedPath")
                    }
                }
            }

            Log.d(TAG, "✅ Restore selesai: $restoredCount file media, mode=$mode")

            RestoreResult(
                success = true,
                settings = newSettings,
                restoredMediaCount = restoredCount
            )
        } catch (e: Exception) {
            Log.e(TAG, "Gagal restore bundle: ${e.message}", e)
            RestoreResult(
                success = false,
                errorMessage = e.message ?: "Gagal restore bundle"
            )
        }
    }

    private fun determineFolderFromName(fileName: String): String {
        val lower = fileName.lowercase()
        return when {
            lower.contains("video") -> "video"
            lower.contains("qris") -> "qris"
            lower.contains("officer") || lower.contains("logo") -> "logo"
            lower.contains("background") -> "background"
            lower.contains("prayer") || lower.contains("card") -> "prayer_card"
            lower.contains("slideshow") || lower.contains("photo") -> "slideshow"
            else -> "media"
        }
    }

    private fun saveMediaToInternal(
        context: Context,
        folder: String,
        fileName: String,
        bytes: ByteArray
    ): String? {
        return try {
            val dir = File(context.filesDir, "masjid_io/$folder")
            if (!dir.exists()) dir.mkdirs()
            val safeName = fileName.replace(Regex("[^a-zA-Z0-9._-]"), "_")
            val targetFile = File(dir, "${System.currentTimeMillis()}_$safeName")
            FileOutputStream(targetFile).use { it.write(bytes) }
            targetFile.absolutePath
        } catch (e: Exception) {
            Log.e(TAG, "Gagal simpan media: ${e.message}")
            null
        }
    }

    // ============================================================
    // V1.30.7 BARU — RESTORE DENGAN PATH LOKAL
    // Sama seperti restoreBundle, tapi settings yang dikembalikan
    // sudah di-patch dengan path lokal TV (bukan path HP lama).
    // ============================================================
    fun restoreBundleWithLocalPaths(
        context: Context,
        bundleFile: File,
        currentSettings: AppSettings
    ): RestoreResult {
        // 1. Panggil restoreBundle untuk extract media
        val raw = restoreBundle(context, bundleFile, currentSettings)
        val initialSettings = raw.settings
        if (!raw.success || initialSettings == null) return raw

        // 2. Scan folder masjid_io untuk cari file terbaru
        val filesDir = context.filesDir
        val root = File(filesDir, "masjid_io")

        var patched: AppSettings = initialSettings

        // Patch QRIS
        findLatestFile(File(root, "qris"))?.let { f ->
            patched = patched.copy(qrisPhotoUri = f.absolutePath)
        }
        // Patch logo (officerPhotoUri)
        findLatestFile(File(root, "logo"))?.let { f ->
            patched = patched.copy(officerPhotoUri = f.absolutePath)
        }
        // Patch background
        findLatestFile(File(root, "background"))?.let { f ->
            patched = patched.copy(customBackgroundUri = f.absolutePath)
        }
        // Patch video
        findLatestFile(File(root, "video"))?.let { f ->
            patched = patched.copy(videoUri = f.absolutePath)
        }
        // Patch prayer card
        findLatestFile(File(root, "prayer_card"))?.let { f ->
            patched = patched.copy(prayerCardPhotoUri = f.absolutePath)
        }
        // Patch slideshow — DITAMBAH, bukan ditimpa
        val slideshowDir = File(root, "slideshow")
        if (slideshowDir.exists()) {
            val newSlides = slideshowDir.listFiles()
                ?.filter { it.isFile }
                ?.sortedBy { it.lastModified() }
                ?.map { it.absolutePath }
                ?: emptyList()
            if (newSlides.isNotEmpty()) {
                patched = patched.copy(
                    photoSlideshowUris = patched.photoSlideshowUris + newSlides
                )
            }
        }

        Log.d(TAG, "✅ Restore dengan path lokal selesai")
        return raw.copy(settings = patched)
    }

    private fun findLatestFile(dir: File?): File? {
        if (dir == null || !dir.exists()) return null
        return dir.listFiles()
            ?.filter { it.isFile }
            ?.maxByOrNull { it.lastModified() }
    }
}
