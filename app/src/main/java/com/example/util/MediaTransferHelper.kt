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
 * Fungsi utama:
 *   1. Kumpulkan semua URI media dari AppSettings
 *   2. Kompres video via MediaCodec (opsional)
 *   3. Kompres foto via BitmapFactory (opsional)
 *   4. Chunk file jadi potongan kecil untuk transfer HTTP
 *   5. Encode/decode base64
 */
object MediaTransferHelper {

    private const val TAG = "MediaTransferHelper"
    private const val CHUNK_SIZE = 1 * 1024 * 1024 // 1 MB per chunk
    private const val PHOTO_QUALITY = 85 // JPEG quality 0-100
    private const val PHOTO_MAX_DIMENSION = 1920 // max 1920px sisi terpanjang
    private const val VIDEO_COMPRESS_BITRATE = 3_000_000 // 3 Mbps
    private const val VIDEO_COMPRESS_MAX_DIM = 1920 // max 1080p

    // ============================================================
    // DATA CLASS
    // ============================================================
    data class MediaFileInfo(
        val fieldKey: String,       // "qrisPhotoUri", "videoUri", dll
        val displayName: String,    // Nama file
        val uri: String,            // URI asli
        val fileType: String,       // "photo", "video", "unknown"
        val fileSize: Long,         // Ukuran byte
        val mimeType: String        // "image/jpeg", "video/mp4", dll
    )

    data class TransferProgress(
        val currentFileIndex: Int,
        val totalFiles: Int,
        val currentFileName: String,
        val currentFileProgress: Float, // 0..1
        val overallProgress: Float,     // 0..1
        val phase: String,              // "collecting", "compressing", "transferring", "done", "error"
        val message: String = ""
    )

    data class ChunkData(
        val index: Int,
        val total: Int,
        val bytes: ByteArray,
        val fileId: String
    )

    // ============================================================
    // 1. KUMPULKAN SEMUA URI MEDIA DARI SETTINGS
    // ============================================================
    fun collectMediaFiles(settings: AppSettings): List<MediaFileInfo> {
        val list = mutableListOf<MediaFileInfo>()

        // QRIS
        if (!settings.qrisPhotoUri.isNullOrBlank()) {
            list.add(MediaFileInfo(
                fieldKey = "qrisPhotoUri",
                displayName = "QRIS Code",
                uri = settings.qrisPhotoUri,
                fileType = "photo",
                fileSize = 0L,
                mimeType = "image/jpeg"
            ))
        }

        // Logo Masjid (officerPhotoUri)
        if (!settings.officerPhotoUri.isNullOrBlank()) {
            list.add(MediaFileInfo(
                fieldKey = "officerPhotoUri",
                displayName = "Logo Masjid",
                uri = settings.officerPhotoUri,
                fileType = "photo",
                fileSize = 0L,
                mimeType = "image/jpeg"
            ))
        }

        // Background Custom
        if (!settings.customBackgroundUri.isNullOrBlank()) {
            list.add(MediaFileInfo(
                fieldKey = "customBackgroundUri",
                displayName = "Background Custom",
                uri = settings.customBackgroundUri,
                fileType = "photo",
                fileSize = 0L,
                mimeType = "image/jpeg"
            ))
        }

        // Prayer Card Photo
        if (settings.prayerCardPhotoEnabled && !settings.prayerCardPhotoUri.isNullOrBlank()) {
            list.add(MediaFileInfo(
                fieldKey = "prayerCardPhotoUri",
                displayName = "Background Kartu Sholat",
                uri = settings.prayerCardPhotoUri,
                fileType = "photo",
                fileSize = 0L,
                mimeType = "image/jpeg"
            ))
        }

        // Video Kegiatan
        if (settings.videoEnabled && !settings.videoUri.isNullOrBlank()) {
            list.add(MediaFileInfo(
                fieldKey = "videoUri",
                displayName = "Video Kegiatan",
                uri = settings.videoUri,
                fileType = "video",
                fileSize = 0L,
                mimeType = "video/mp4"
            ))
        }

        // Foto Slideshow (multiple)
        if (settings.photoSlideshowEnabled && settings.photoSlideshowUris.isNotEmpty()) {
            settings.photoSlideshowUris.forEachIndexed { idx, uri ->
                list.add(MediaFileInfo(
                    fieldKey = "photoSlideshowUris[$idx]",
                    displayName = "Foto Slideshow ${idx + 1}",
                    uri = uri,
                    fileType = "photo",
                    fileSize = 0L,
                    mimeType = "image/jpeg"
                ))
            }
        }

        return list
    }

    // ============================================================
    // 2. AMBIL UKURAN FILE DARI URI
    // ============================================================
    fun getFileSize(context: Context, uriString: String): Long {
        return try {
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
    // 3. BACA FILE DARI URI → ByteArray
    // ============================================================
    fun readFileBytes(context: Context, uriString: String): ByteArray? {
        return try {
            val uri = Uri.parse(uriString)
            context.contentResolver.openInputStream(uri)?.use { input ->
                input.readBytes()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Gagal baca file: ${e.message}")
            null
        }
    }

    // ============================================================
    // 4. KOMPRES FOTO (Bitmap → JPEG)
    // ============================================================
    @Suppress("DEPRECATION")
    fun compressPhoto(context: Context, uriString: String): ByteArray? {
        return try {
            val uri = Uri.parse(uriString)
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null

            // Baca dimensi dulu untuk hitung scale
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream.close()

            val width = options.outWidth
            val height = options.outHeight
            val scale = calculateSampleSize(width, height, PHOTO_MAX_DIMENSION)

            // Baca ulang dengan sampling
            val decodeOptions = BitmapFactory.Options().apply {
                inSampleSize = scale
            }
            val stream2 = context.contentResolver.openInputStream(uri) ?: return null
            val bitmap = BitmapFactory.decodeStream(stream2, null, decodeOptions)
            stream2.close()

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
    // 5. KOMPRES VIDEO (MediaCodec transcode)
    // ============================================================
    suspend fun compressVideo(
        context: Context,
        inputUri: String,
        outputFile: File,
        onProgress: (Float) -> Unit = {}
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val uri = Uri.parse(inputUri)
            val extractor = MediaExtractor()
            context.contentResolver.openFileDescriptor(uri, "r")?.use { pfd ->
                extractor.setDataSource(pfd.fileDescriptor)
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

            // Copy video track
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
    // 6. CHUNK FILE → List ByteArray
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
    // 7. ENCODE / DECODE BASE64
    // ============================================================
    fun encodeBase64(bytes: ByteArray): String {
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    fun decodeBase64(text: String): ByteArray {
        return Base64.decode(text, Base64.NO_WRAP)
    }

    // ============================================================
    // 8. SIMPAN FILE KE FOLDER INTERNAL
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
    // 9. GET MIME TYPE DARI URI
    // ============================================================
    fun getMimeType(context: Context, uriString: String): String {
        return try {
            val uri = Uri.parse(uriString)
            context.contentResolver.getType(uri) ?: "application/octet-stream"
        } catch (e: Exception) {
            "application/octet-stream"
        }
    }

    // ============================================================
    // 10. FORMAT UKURAN FILE (Helper UI)
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
