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
    private const val FOLDER_DOWNLOAD = "masjid.io/update"

    data class DownloadState(
        val isDownloading: Boolean = false,
        val progress: Float = 0f,
        val downloadedBytes: Long = 0L,
        val totalBytes: Long = 0L,
        val savedFilePath: String? = null,
        val errorMessage: String? = null,
        val isFinished: Boolean = false
    )

    /**
     * Download APK dari URL ke folder internal app.
     * Return Flow yang meng-emit DownloadState.
     */
    fun downloadApk(
        context: Context,
        downloadUrl: String,
        fileName: String = "masjid-io-update.apk"
    ): Flow<DownloadState> = flow {
        emit(DownloadState(isDownloading = true, progress = 0f))

        try {
            val targetDir = File(context.getExternalFilesDir(null), FOLDER_DOWNLOAD)
            if (!targetDir.exists()) targetDir.mkdirs()

            val targetFile = File(targetDir, fileName)
            if (targetFile.exists()) targetFile.delete()

            val url = URL(downloadUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 30000
            connection.readTimeout = 60000
            connection.setRequestProperty("User-Agent", "MasjidIO-App")
            connection.instanceFollowRedirects = true

            val responseCode = connection.responseCode
            if (responseCode != HttpURLConnection.HTTP_OK &&
                responseCode != HttpURLConnection.HTTP_MOVED_TEMP &&
                responseCode != HttpURLConnection.HTTP_MOVED_PERM) {
                emit(
                    DownloadState(
                        errorMessage = "Server error: $responseCode",
                        isDownloading = false
                    )
                )
                return@flow
            }

            // Follow redirect kalau ada
            var inputStream = connection.inputStream
            val contentLength = connection.contentLengthLong

            // Kalau ada redirect, ikuti
            if (responseCode == HttpURLConnection.HTTP_MOVED_TEMP ||
                responseCode == HttpURLConnection.HTTP_MOVED_PERM) {
                val redirectUrl = connection.getHeaderField("Location")
                if (redirectUrl != null) {
                    val redirectConn = URL(redirectUrl).openConnection() as HttpURLConnection
                    redirectConn.setRequestProperty("User-Agent", "MasjidIO-App")
                    inputStream = redirectConn.inputStream
                }
            }

            val fileOutput = FileOutputStream(targetFile)
            val buffer = ByteArray(8192)
            var bytesRead: Int
            var totalRead: Long = 0L

            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                fileOutput.write(buffer, 0, bytesRead)
                totalRead += bytesRead

                val progress = if (contentLength > 0) {
                    totalRead.toFloat() / contentLength.toFloat()
                } else {
                    0f
                }

                emit(
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

            emit(
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
            Log.e(TAG, "Download gagal: ${e.message}", e)
            emit(
                DownloadState(
                    isDownloading = false,
                    errorMessage = e.message ?: "Download gagal"
                )
            )
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Cek apakah APK sudah pernah di-download.
     */
    fun hasDownloadedApk(context: Context): Boolean {
        val targetDir = File(context.getExternalFilesDir(null), FOLDER_DOWNLOAD)
        return targetDir.exists() && (targetDir.listFiles()?.any { it.name.endsWith(".apk") } == true)
    }

    /**
     * Ambil path APK yang sudah di-download.
     */
    fun getDownloadedApkPath(context: Context): String? {
        val targetDir = File(context.getExternalFilesDir(null), FOLDER_DOWNLOAD)
        if (!targetDir.exists()) return null
        return targetDir.listFiles()
            ?.firstOrNull { it.name.endsWith(".apk") }
            ?.absolutePath
    }

    /**
     * Buka installer APK.
     * Butuh permission REQUEST_INSTALL_PACKAGES di Manifest.
     */
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

    /**
     * Buka halaman pengaturan "Install unknown apps" untuk app ini.
     */
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

    /**
     * Cek apakah app punya izin install APK.
     */
    fun canInstallApk(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.packageManager.canRequestPackageInstalls()
        } else {
            true
        }
    }
}
