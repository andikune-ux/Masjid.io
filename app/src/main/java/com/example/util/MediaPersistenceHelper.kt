package com.example.util

import android.content.Context
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.FileOutputStream

/**
 * MediaPersistenceHelper — Menyalin file media dari galeri ke folder permanen app.
 *
 * MASALAH:
 *   Pola lama menyimpan `content://` URI langsung dari galeri. URI ini temporary —
 *   izin akses dicabut saat app ditutup/update/reboot → file jadi blank.
 *
 * SOLUSI:
 *   Copy file fisik ke `filesDir/masjid_io/` (folder permanen milik app)
 *   lalu simpan PATH LOKAL (bukan URI).
 *
 * Folder:
 *   - filesDir/masjid_io/qris/        -> QRIS
 *   - filesDir/masjid_io/logo/        -> Logo masjid
 *   - filesDir/masjid_io/background/  -> Background custom
 *   - filesDir/masjid_io/video/       -> Video kegiatan
 *   - filesDir/masjid_io/slideshow/   -> Foto slideshow
 *   - filesDir/masjid_io/prayer_card/ -> Background kartu sholat
 *   - filesDir/masjid_io/officer/     -> Foto ustadz/petugas
 */
object MediaPersistenceHelper {

    private const val TAG = "MediaPersistenceHelper"
    private const val BASE_FOLDER = "masjid_io"

    const val FOLDER_QRIS = "qris"
    const val FOLDER_LOGO = "logo"
    const val FOLDER_BACKGROUND = "background"
    const val FOLDER_VIDEO = "video"
    const val FOLDER_SLIDESHOW = "slideshow"
    const val FOLDER_PRAYER_CARD = "prayer_card"
    const val FOLDER_OFFICER = "officer"

    /**
     * Copy file dari URI ke folder permanen.
     *
     * @param context Context
     * @param sourceUri URI asli (content://... atau file://...)
     * @param folder Folder tujuan (pakai konstanta FOLDER_*)
     * @param fileNamePrefix Prefix nama file (misal "qris", "video", "foto")
     * @return Path lokal (String) atau null kalau gagal
     */
    fun copyToPermanent(
        context: Context,
        sourceUri: String,
        folder: String,
        fileNamePrefix: String = "media"
    ): String? {
        if (sourceUri.isBlank()) return null

        return try {
            val uri = Uri.parse(sourceUri)
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: run {
                    Log.e(TAG, "Input stream null untuk: $sourceUri")
                    return null
                }

            val dir = File(context.filesDir, "$BASE_FOLDER/$folder")
            if (!dir.exists()) dir.mkdirs()

            val ext = detectExtension(context, uri)
            val fileName = "${fileNamePrefix}_${System.currentTimeMillis()}.$ext"
            val targetFile = File(dir, fileName)

            FileOutputStream(targetFile).use { output ->
                inputStream.use { input ->
                    input.copyTo(output)
                }
            }

            Log.d(TAG, "Copied: $sourceUri -> ${targetFile.absolutePath}")
            targetFile.absolutePath
        } catch (e: Exception) {
            Log.e(TAG, "Gagal copy file: ${e.message}", e)
            null
        }
    }

    /**
     * Copy file video (khusus — extension lebih variatif).
     */
    fun copyVideoToPermanent(
        context: Context,
        sourceUri: String
    ): String? {
        return copyToPermanent(
            context = context,
            sourceUri = sourceUri,
            folder = FOLDER_VIDEO,
            fileNamePrefix = "video"
        )
    }

    private fun detectExtension(context: Context, uri: Uri): String {
        return try {
            val mime = context.contentResolver.getType(uri) ?: "image/jpeg"
            when {
                mime.contains("png") -> "png"
                mime.contains("gif") -> "gif"
                mime.contains("webp") -> "webp"
                mime.contains("bmp") -> "bmp"
                mime.contains("mp4") -> "mp4"
                mime.contains("mkv") -> "mkv"
                mime.contains("webm") -> "webm"
                mime.contains("3gp") -> "3gp"
                else -> "jpg"
            }
        } catch (e: Exception) {
            "jpg"
        }
    }

    /**
     * Hapus file lama di folder (cleanup — sisakan N terbaru).
     */
    fun cleanupFolder(
        context: Context,
        folder: String,
        keepLatest: Int = 5
    ) {
        try {
            val dir = File(context.filesDir, "$BASE_FOLDER/$folder")
            if (!dir.exists()) return
            val files = dir.listFiles()?.sortedByDescending { it.lastModified() } ?: return
            files.drop(keepLatest).forEach { old ->
                if (old.delete()) Log.d(TAG, "Cleanup: ${old.name}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Cleanup error: ${e.message}")
        }
    }

    /**
     * Hapus 1 file spesifik.
     */
    fun deleteFile(path: String): Boolean {
        return try {
            val f = File(path)
            if (f.exists()) f.delete() else false
        } catch (e: Exception) {
            false
        }
    }
}
