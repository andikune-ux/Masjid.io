package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

object ApkDownloader {

    private const val TAG = "ApkDownloader"
    private const val FOLDER_APP = "masjid.io"
    private const val FOLDER_UPDATE = "pembaharuan aplikasi"
    private const val MAX_KEEP_APK = 2

    data class DownloadState(
        val isDownloading: Boolean = false,
        val progress: Float = 0f,
        val downloadedBytes: Long = 0L,
        val totalBytes: Long = 0L,
        val savedFilePath: String? = null,
        val errorMessage: String? = null,
        val isFinished: Boolean = false
    )

    // ============================================================
    // FOLDER — /sdcard/masjid.io/pembaharuan aplikasi/
    // ============================================================
    /**
     * Ambil folder target untuk simpan APK.
     * Prioritas: /sdcard/masjid.io/pembaharuan aplikasi/
     * Fallback: getExternalFilesDir()/masjid.io/pembaharuan aplikasi/
     */
    private fun getUpdateDir(context: Context): File {
        // Coba external storage dulu
        return try {
            val externalDir = File(
                Environment.getExternalStorageDirectory(),
                "$FOLDER_APP/$FOLDER_UPDATE"
            )
            if (!externalDir.exists()) externalDir.mkdirs()
            if (externalDir.exists() && externalDir.canWrite()) {
                externalDir
            } else {
                // Fallback ke app external files
                File(context.getExternalFilesDir(null), "$FOLDER_APP/$FOLDER_UPDATE")
                    .also { if (!it.exists()) it.mkdirs() }
            }
        } catch (e: Exception) {
            Log.w(TAG, "External dir gagal, pakai fallback: ${e.message}")
            File(context.getExternalFilesDir(null), "$FOLDER_APP/$FOLDER_UPDATE")
                .also { if (!it.exists()) it.mkdirs() }
        }
    }

    // ============================================================
    // DOWNLOAD
    // ============================================================
    /**
     * Download APK dari URL ke folder update.
     *
     * @param fileName Nama file (misal "masjid-io-V1.30.5.apk")
     */
    fun downloadApk(
        context: Context,
        downloadUrl: String,
        fileName: String = "masjid-io-update.apk"
    ): Flow<DownloadState> = flow {
        emit(DownloadState(isDownloading = true, progress = 0f))

        try {
            val targetDir = getUpdateDir(context)
            val targetFile = File(targetDir, fileName)

            // Kalau sudah ada file versi sama → langsung selesai (tidak download ulang)
            if (targetFile.exists() && targetFile.length() > 0) {
                Log.d(TAG, "File sudah ada: ${targetFile.absolutePath}")
                emit(
                    DownloadState(
                        isDownloading = false,
                        progress = 1f,
                        downloadedBytes = targetFile.length(),
                        totalBytes = targetFile.length(),
                        savedFilePath = targetFile.absolutePath,
                        isFinished = true
                    )
                )
                return@flow
            }

            // Hapus file parsial kalau ada
            if (targetFile.exists()) targetFile.delete()

            val url = URL(downloadUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 30000
            connection.readTimeout = 60000
            connection.setRequestProperty("User-Agent", "MasjidIO-App")
            connection.instanceFollowRedirects = true

            var responseCode = connection.responseCode

            // Follow redirect manual kalau perlu
            if (responseCode == HttpURLConnection.HTTP_MOVED_TEMP ||
                responseCode == HttpURLConnection.HTTP_MOVED_PERM
            ) {
                val redirectUrl = connection.getHeaderField("Location")
                if (redirectUrl != null) {
                    val redirectConn = URL(redirectUrl).openConnection() as HttpURLConnection
                    redirectConn.setRequestProperty("User-Agent", "MasjidIO-App")
                    redirectConn.connectTimeout = 30000
                    redirectConn.readTimeout = 60000
                    responseCode = redirectConn.responseCode

                    if (responseCode != HttpURLConnection.HTTP_OK) {
                        emit(
                            DownloadState(
                                errorMessage = "Redirect error: $responseCode",
                                isDownloading = false
                            )
                        )
                        return@flow
                    }

                    downloadFromConnection(
                        redirectConn, targetFile,
                        onProgress = { state -> emit(state) }
                    )
                    return@flow
                }
            }

            if (responseCode != HttpURLConnection.HTTP_OK) {
                emit(
                    DownloadState(
                        errorMessage = "Server error: $responseCode",
                        isDownloading = false
                    )
                )
                return@flow
            }

            downloadFromConnection(
                connection, targetFile,
                onProgress = { state -> emit(state) }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Download gagal: ${e.message}", e)
            emit(
                DownloadState(
                    isDownloading = false,
                    errorMessage = e.message ?: "Download gagal"
                )
            )
        }
    }.flowOn(Dispatchers.IO)

    private suspend fun kotlinx.coroutines.flow.FlowCollector<DownloadState>.downloadFromConnection(
        connection: HttpURLConnection,
        targetFile: File,
        onProgress: suspend (DownloadState) -> Unit
    ) {
        try {
            val inputStream = connection.inputStream
            val contentLength = connection.contentLengthLong

            val fileOutput = FileOutputStream(targetFile)
            val buffer = ByteArray(8192)
            var bytesRead: Int
            var totalRead: Long = 0L

            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                fileOutput.write(buffer, 0, bytesRead)
                totalRead += bytesRead

                val progress = if (contentLength > 0) {
                    totalRead.toFloat() / contentLength.toFloat()
                } else 0f

                onProgress(
                    DownloadState(
                        isDownloading = true,
                        progress = progress.coerceIn(0f, 1f),
                        downloadedBytes = totalRead,
                        totalBytes = contentLength
                    )
                )
            }

            fileOutput.flush()
            fileOutput.close()
            inputStream.close()
            connection.disconnect()

            onProgress(
                DownloadState(
                    isDownloading = false,
                    progress = 1f,
                    downloadedBytes = totalRead,
                    totalBytes = contentLength,
                    savedFilePath = targetFile.absolutePath,
                    isFinished = true
                )
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error download connection: ${e.message}", e)
            onProgress(
                DownloadState(
                    isDownloading = false,
                    errorMessage = e.message ?: "Download gagal"
                )
            )
        }
    }
    
    // ============================================================
    // LIST & CLEANUP APK
    // ============================================================
    /**
     * Ambil daftar semua APK yang tersimpan (untuk Info Aplikasi).
     * Diurutkan dari terbaru (lastModified desc).
     */
    fun getDownloadedApkList(context: Context): List<File> {
        return try {
            val dir = getUpdateDir(context)
            if (!dir.exists()) return emptyList()
            dir.listFiles()
                ?.filter { it.isFile && it.name.endsWith(".apk", ignoreCase = true) }
                ?.sortedByDescending { it.lastModified() }
                ?: emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "Gagal list APK: ${e.message}")
            emptyList()
        }
    }

    /**
     * Hapus APK lama — sisakan hanya N terbaru (default 2).
     * Panggil setelah install selesai.
     */
    fun deleteOldApks(context: Context, keepCount: Int = MAX_KEEP_APK) {
        try {
            val list = getDownloadedApkList(context)
            if (list.size <= keepCount) return

            val toDelete = list.drop(keepCount)
            toDelete.forEach { file ->
                if (file.delete()) {
                    Log.d(TAG, "🗑️ Hapus APK lama: ${file.name}")
                } else {
                    Log.w(TAG, "Gagal hapus: ${file.name}")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Gagal hapus APK lama: ${e.message}")
        }
    }

    /**
     * Hapus 1 file APK spesifik (dari UI Info Aplikasi).
     */
    fun deleteApk(filePath: String): Boolean {
        return try {
            val file = File(filePath)
            if (file.exists()) file.delete() else false
        } catch (e: Exception) {
            Log.e(TAG, "Gagal hapus APK: ${e.message}")
            false
        }
    }

    // ============================================================
    // BACKWARD COMPAT — method lama
    // ============================================================
    fun hasDownloadedApk(context: Context): Boolean {
        return getDownloadedApkList(context).isNotEmpty()
    }

    fun getDownloadedApkPath(context: Context): String? {
        return getDownloadedApkList(context).firstOrNull()?.absolutePath
    }

    // ============================================================
    // INSTALL APK
    // ============================================================
    fun installApk(context: Context, apkPath: String): Boolean {
        return try {
            val apkFile = File(apkPath)
            if (!apkFile.exists()) {
                Log.e(TAG, "File APK tidak ada: $apkPath")
                return false
            }

            val apkUri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    apkFile
                )
            } else {
                Uri.fromFile(apkFile)
            }

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(apkUri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(intent)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Install gagal: ${e.message}", e)
            false
        }
    }

    fun openInstallPermissionSettings(context: Context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val intent = Intent(android.provider.Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                    data = Uri.parse("package:${context.packageName}")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Gagal buka install permission settings: ${e.message}")
        }
    }

    fun canInstallApk(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.packageManager.canRequestPackageInstalls()
        } else {
            true
        }
    }

    // ============================================================
    // HELPER — Format ukuran file (untuk UI)
    // ============================================================
    fun formatSize(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "${bytes / 1024} KB"
            else -> String.format("%.1f MB", bytes / (1024.0 * 1024.0))
        }
    }
}
