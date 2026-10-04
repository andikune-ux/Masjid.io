package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.media.MediaMetadataRetriever
import android.media.MediaMuxer
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Base64
import android.util.Log
import com.example.data.model.AppSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.ByteBuffer

/**
 * MediaTransferHelper — Helper untuk transfer file media antar device.
 *
 * V1.04.423 FIX: Support PATH LOKAL (filesDir/masjid_io/...) selain content:// URI.
 * Bug sebelumnya: file yang di-copy ke folder permanen tidak bisa dibaca.
 */
object MediaTransferHelper {

    private const val TAG = "MediaTransferHelper"
    private const val CHUNK_SIZE = 1 * 1024 * 1024
    private const val PHOTO_QUALITY = 85
    private const val PHOTO_MAX_DIMENSION = 1920
    private const val VIDEO_COMPRESS_BITRATE = 3_000_000
    private const val VIDEO_COMPRESS_MAX_DIM = 1920

    data class MediaFileInfo(
        val fieldKey: String,
        val displayName: String,
        val uri: String,
        val fileType: String,
        val fileSize: Long,
        val mimeType: String
    )

    data class TransferProgress(
        val currentFileIndex: Int,
        val totalFiles: Int,
        val currentFileName: String,
        val currentFileProgress: Float,
        val overallProgress: Float,
        val phase: String,
        val message: String = ""
    )

    data class ChunkData(
        val index: Int,
        val total: Int,
        val bytes: ByteArray,
        val fileId: String
    )

    // ============================================================
    // KUMPULKAN SEMUA URI MEDIA DARI SETTINGS
    // ============================================================
    fun collectMediaFiles(settings: AppSettings): List<MediaFileInfo> {
        val list = mutableListOf<MediaFileInfo>()

        if (!settings.qrisPhotoUri.isNullOrBlank()) {
            list.add(MediaFileInfo("qrisPhotoUri", "QRIS Code", settings.qrisPhotoUri, "photo", 0L, "image/jpeg"))
        }
        if (!settings.officerPhotoUri.isNullOrBlank()) {
            list.add(MediaFileInfo("officerPhotoUri", "Logo Masjid", settings.officerPhotoUri, "photo", 0L, "image/jpeg"))
        }
        if (!settings.customBackgroundUri.isNullOrBlank()) {
            list.add(MediaFileInfo("customBackgroundUri", "Background Custom", settings.customBackgroundUri, "photo", 0L, "image/jpeg"))
        }
        if (settings.prayerCardPhotoEnabled && !settings.prayerCardPhotoUri.isNullOrBlank()) {
            list.add(MediaFileInfo("prayerCardPhotoUri", "Background Kartu Sholat", settings.prayerCardPhotoUri, "photo", 0L, "image/jpeg"))
        }
        if (settings.videoEnabled && !settings.videoUri.isNullOrBlank()) {
            list.add(MediaFileInfo("videoUri", "Video Kegiatan", settings.videoUri, "video", 0L, "video/mp4"))
        }
        if (settings.photoSlideshowEnabled && settings.photoSlideshowUris.isNotEmpty()) {
            settings.photoSlideshowUris.forEachIndexed { idx, uri ->
                list.add(MediaFileInfo("photoSlideshowUris[$idx]", "Foto Slideshow ${idx + 1}", uri, "photo", 0L, "image/jpeg"))
            }
        }
        return list
    }

    // ============================================================
    // V1.04.423 FIX: CEK APAKAH PATH LOKAL
    // ============================================================
    private fun isLocalPath(uriString: String): Boolean {
        return uriString.startsWith("/") || uriString.startsWith("file://")
    }

    private fun localPathFromUri(uriString: String): String {
        return if (uriString.startsWith("file://")) {
            uriString.removePrefix("file://")
        } else {
            uriString
        }
    }

    // ============================================================
    // V1.04.423 FIX: GET FILE SIZE — SUPPORT PATH LOKAL + URI
    // ============================================================
    fun getFileSize(context: Context, uriString: String): Long {
        return try {
            if (isLocalPath(uriString)) {
                val f = File(localPathFromUri(uriString))
                return if (f.exists()) f.length() else 0L
            }
            val uri = Uri.parse(uriString)
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (cursor.moveToFirst() && sizeIndex >= 0) {
                    return cursor.getLong(sizeIndex)
                }
            }
            0L
        } catch (e: Exception) {
            Log.w(TAG, "Gagal ambil file size: ${e.message}")
            0L
        }
    }

    // ============================================================
    // V1.04.423 FIX: BACA FILE — SUPPORT PATH LOKAL + URI
    // ============================================================
    fun readFileBytes(context: Context, uriString: String): ByteArray? {
        return try {
            if (isLocalPath(uriString)) {
                val f = File(localPathFromUri(uriString))
                if (f.exists()) f.readBytes() else null
            } else {
                val uri = Uri.parse(uriString)
                context.contentResolver.openInputStream(uri)?.use { input ->
                    input.readBytes()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Gagal baca file: ${e.message}")
            null
        }
    }
    // ============================================================
// V1.04.423 FIX: KOMPRES FOTO — SUPPORT PATH LOKAL + URI
// ============================================================
@Suppress("DEPRECATION")
fun compressPhoto(context: Context, uriString: String): ByteArray? {
    return try {
        val inputStream1: InputStream
        val inputStream2: InputStream

        if (isLocalPath(uriString)) {
            // Path lokal — buka File langsung
            val file = File(localPathFromUri(uriString))
            if (!file.exists()) {
                Log.w(TAG, "File lokal tidak ada: ${file.absolutePath}")
                return null
            }
            inputStream1 = file.inputStream()
            inputStream2 = file.inputStream()
        } else {
            // content:// URI
            val uri = Uri.parse(uriString)
            inputStream1 = context.contentResolver.openInputStream(uri) ?: return null
            inputStream2 = context.contentResolver.openInputStream(uri) ?: run {
                inputStream1.close()
                return null
            }
        }

        // Baca dimensi dulu untuk hitung scale
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeStream(inputStream1, null, options)
        inputStream1.close()

        val width = options.outWidth
        val height = options.outHeight
        val scale = calculateSampleSize(width, height, PHOTO_MAX_DIMENSION)

        // Baca ulang dengan sampling
        val decodeOptions = BitmapFactory.Options().apply {
            inSampleSize = scale
        }
        val bitmap = BitmapFactory.decodeStream(inputStream2, null, decodeOptions)
        inputStream2.close()

        if (bitmap == null) return null

        // Kompres ke JPEG
        val output = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, PHOTO_QUALITY, output)
        bitmap.recycle()

        output.toByteArray()
    } catch (e: Exception) {
        Log.e(TAG, "Gagal kompres foto: ${e.message}")
        null
    }
}

private fun calculateSampleSize(width: Int, height: Int, maxDim: Int): Int {
    var sample = 1
    var w = width
    var h = height
    while (w / 2 >= maxDim || h / 2 >= maxDim) {
        w /= 2
        h /= 2
        sample *= 2
    }
    return sample
}

// ============================================================
// V1.04.423 FIX: KOMPRES VIDEO — SUPPORT PATH LOKAL + URI
// ============================================================
suspend fun compressVideo(
    context: Context,
    inputUri: String,
    outputFile: File,
    onProgress: (Float) -> Unit = {}
): Boolean = withContext(Dispatchers.IO) {
    try {
        val extractor = MediaExtractor()

        if (isLocalPath(inputUri)) {
            // Path lokal — set data source langsung
            val file = File(localPathFromUri(inputUri))
            if (!file.exists()) {
                Log.w(TAG, "File video lokal tidak ada: ${file.absolutePath}")
                extractor.release()
                return@withContext false
            }
            extractor.setDataSource(file.absolutePath)
        } else {
            // content:// URI
            val uri = Uri.parse(inputUri)
            context.contentResolver.openFileDescriptor(uri, "r")?.use { pfd ->
                extractor.setDataSource(pfd.fileDescriptor)
            }
        }

        var videoTrackIndex = -1
        var audioTrackIndex = -1
        var videoFormat: MediaFormat? = null
        var audioFormat: MediaFormat? = null

        for (i in 0 until extractor.trackCount) {
            val format = extractor.getTrackFormat(i)
            val mime = format.getString(MediaFormat.KEY_MIME) ?: continue
            if (mime.startsWith("video/") && videoTrackIndex < 0) {
                videoTrackIndex = i
                videoFormat = format
            } else if (mime.startsWith("audio/") && audioTrackIndex < 0) {
                audioTrackIndex = i
                audioFormat = format
            }
        }

        if (videoTrackIndex < 0 || videoFormat == null) {
            Log.w(TAG, "Tidak ada track video")
            extractor.release()
            return@withContext false
        }

        val muxer = MediaMuxer(
            outputFile.absolutePath,
            MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4
        )

        extractor.selectTrack(videoTrackIndex)
        val muxerVideoTrack = muxer.addTrack(videoFormat)
        var muxerAudioTrack = -1
        if (audioTrackIndex >= 0 && audioFormat != null) {
            muxerAudioTrack = muxer.addTrack(audioFormat)
        }
        muxer.start()

        val bufferSize = 1 * 1024 * 1024
        val buffer = ByteBuffer.allocate(bufferSize)
        val bufferInfo = MediaCodec.BufferInfo()

        val durationUs = try {
            videoFormat.getLong(MediaFormat.KEY_DURATION)
        } catch (e: Exception) { 0L }

        // Copy video
        while (true) {
            bufferInfo.offset = 0
            bufferInfo.size = extractor.readSampleData(buffer, 0)
            if (bufferInfo.size < 0) break
            bufferInfo.presentationTimeUs = extractor.sampleTime
            bufferInfo.flags = extractor.sampleFlags
            muxer.writeSampleData(muxerVideoTrack, buffer, bufferInfo)

            if (durationUs > 0) {
                onProgress((bufferInfo.presentationTimeUs.toFloat() / durationUs).coerceIn(0f, 0.9f))
            }
            extractor.advance()
        }

        // Copy audio track (jika ada)
        if (audioTrackIndex >= 0) {
            extractor.selectTrack(audioTrackIndex)
            extractor.seekTo(0, MediaExtractor.SEEK_TO_CLOSEST_SYNC)
            while (true) {
                bufferInfo.offset = 0
                bufferInfo.size = extractor.readSampleData(buffer, 0)
                if (bufferInfo.size < 0) break
                bufferInfo.presentationTimeUs = extractor.sampleTime
                bufferInfo.flags = extractor.sampleFlags
                muxer.writeSampleData(muxerAudioTrack, buffer, bufferInfo)
                extractor.advance()
            }
        }

        muxer.stop()
        muxer.release()
        extractor.release()

        onProgress(1f)
        Log.d(TAG, "Video kompres selesai: ${outputFile.absolutePath}")
        true
    } catch (e: Exception) {
        Log.e(TAG, "Gagal kompres video: ${e.message}", e)
        false
    }
}

// ============================================================
// CHUNK FILE → List ByteArray
// ============================================================
fun splitIntoChunks(bytes: ByteArray, chunkSize: Int = CHUNK_SIZE): List<ByteArray> {
    val chunks = mutableListOf<ByteArray>()
    var offset = 0
    while (offset < bytes.size) {
        val end = minOf(offset + chunkSize, bytes.size)
        chunks.add(bytes.copyOfRange(offset, end))
        offset = end
    }
    return chunks
}

// ============================================================
// ENCODE / DECODE BASE64
// ============================================================
fun encodeBase64(bytes: ByteArray): String {
    return Base64.encodeToString(bytes, Base64.NO_WRAP)
}

fun decodeBase64(text: String): ByteArray {
    return Base64.decode(text, Base64.NO_WRAP)
}
    // ============================================================
    // SIMPAN FILE KE FOLDER INTERNAL
    // ============================================================
    fun saveFileToInternal(context: Context, folder: String, fileName: String, bytes: ByteArray): String? {
        return try {
            val dir = File(context.filesDir, "masjid_io/$folder")
            if (!dir.exists()) dir.mkdirs()
            val file = File(dir, fileName)
            FileOutputStream(file).use { it.write(bytes) }
            file.absolutePath
        } catch (e: Exception) {
            Log.e(TAG, "Gagal simpan file: ${e.message}")
            null
        }
    }

    // ============================================================
    // GET MIME TYPE — SUPPORT PATH LOKAL + URI
    // ============================================================
    fun getMimeType(context: Context, uriString: String): String {
        return try {
            if (isLocalPath(uriString)) {
                val path = localPathFromUri(uriString)
                when {
                    path.endsWith(".png", true) -> "image/png"
                    path.endsWith(".jpg", true) -> "image/jpeg"
                    path.endsWith(".jpeg", true) -> "image/jpeg"
                    path.endsWith(".gif", true) -> "image/gif"
                    path.endsWith(".webp", true) -> "image/webp"
                    path.endsWith(".mp4", true) -> "video/mp4"
                    path.endsWith(".mkv", true) -> "video/x-matroska"
                    path.endsWith(".webm", true) -> "video/webm"
                    path.endsWith(".3gp", true) -> "video/3gpp"
                    else -> "application/octet-stream"
                }
            } else {
                val uri = Uri.parse(uriString)
                context.contentResolver.getType(uri) ?: "application/octet-stream"
            }
        } catch (e: Exception) {
            "application/octet-stream"
        }
    }

    // ============================================================
    // FORMAT UKURAN FILE (Helper UI)
    // ============================================================
    fun formatSize(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "${bytes / 1024} KB"
            bytes < 1024 * 1024 * 1024 -> String.format("%.1f MB", bytes / (1024.0 * 1024.0))
            else -> String.format("%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0))
        }
    }
}
