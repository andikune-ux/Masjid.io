package dev.andikune.masjidio.kiosk

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.content.ContextCompat
import dev.andikune.masjidio.util.ApkDownloader
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Download update dipisahkan dari Activity supaya tetap hidup saat pengguna
 * berpindah menu atau membuka aplikasi lain.
 */
class UpdateDownloadService : Service() {

    companion object {
        private const val TAG = "UpdateDownloadService"
        private const val CHANNEL_ID = "masjid_io_update_download"
        private const val NOTIFICATION_ID = 8890

        const val ACTION_START = "dev.andikune.masjidio.START_UPDATE_DOWNLOAD"
        const val ACTION_PROGRESS = "dev.andikune.masjidio.UPDATE_DOWNLOAD_PROGRESS"

        const val EXTRA_URL = "download_url"
        const val EXTRA_FILE_NAME = "file_name"

        const val PREFS = "update_download_state"
        const val KEY_STATUS = "status"
        const val KEY_PROGRESS = "progress"
        const val KEY_DOWNLOADED_BYTES = "downloaded_bytes"
        const val KEY_TOTAL_BYTES = "total_bytes"
        const val KEY_FILE_PATH = "file_path"
        const val KEY_ERROR = "error"

        const val STATUS_IDLE = "IDLE"
        const val STATUS_DOWNLOADING = "DOWNLOADING"
        const val STATUS_COMPLETE = "COMPLETE"
        const val STATUS_ERROR = "ERROR"

        fun start(context: Context, downloadUrl: String, fileName: String) {
            val intent = Intent(context, UpdateDownloadService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_URL, downloadUrl)
                putExtra(EXTRA_FILE_NAME, fileName)
            }
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    ContextCompat.startForegroundService(context, intent)
                } else {
                    context.startService(intent)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Gagal memulai service download: ${e.message}", e)
                writeState(context, STATUS_ERROR, error = e.message ?: "Service download gagal")
            }
        }

        fun readState(context: Context): State {
            val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            return State(
                status = prefs.getString(KEY_STATUS, STATUS_IDLE) ?: STATUS_IDLE,
                progress = prefs.getFloat(KEY_PROGRESS, 0f),
                downloadedBytes = prefs.getLong(KEY_DOWNLOADED_BYTES, 0L),
                totalBytes = prefs.getLong(KEY_TOTAL_BYTES, 0L),
                filePath = prefs.getString(KEY_FILE_PATH, null),
                error = prefs.getString(KEY_ERROR, null)
            )
        }

        private fun writeState(
            context: Context,
            status: String,
            progress: Float = 0f,
            downloadedBytes: Long = 0L,
            totalBytes: Long = 0L,
            filePath: String? = null,
            error: String? = null
        ) {
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_STATUS, status)
                .putFloat(KEY_PROGRESS, progress.coerceIn(0f, 1f))
                .putLong(KEY_DOWNLOADED_BYTES, downloadedBytes)
                .putLong(KEY_TOTAL_BYTES, totalBytes)
                .putString(KEY_FILE_PATH, filePath)
                .putString(KEY_ERROR, error)
                .commit()
        }

        data class State(
            val status: String,
            val progress: Float,
            val downloadedBytes: Long,
            val totalBytes: Long,
            val filePath: String?,
            val error: String?
        )
    }

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var activeJob = false

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action != ACTION_START) return START_REDELIVER_INTENT

        startForeground(NOTIFICATION_ID, buildNotification("Menyiapkan download update..."))
        if (activeJob) return START_REDELIVER_INTENT

        val url = intent.getStringExtra(EXTRA_URL)
        val fileName = intent.getStringExtra(EXTRA_FILE_NAME)
        if (url.isNullOrBlank() || fileName.isNullOrBlank()) {
            writeState(this, STATUS_ERROR, error = "URL atau nama file update kosong")
            stopSelf(startId)
            return START_NOT_STICKY
        }

        activeJob = true
        writeState(this, STATUS_DOWNLOADING)
        broadcastState()

        serviceScope.launch {
            var lastPersistedBytes = 0L
            var lastPersistedAt = 0L
            try {
                ApkDownloader.downloadApk(
                    context = applicationContext,
                    downloadUrl = url,
                    fileName = fileName
                ).collect { state ->
                    when {
                        state.errorMessage != null -> {
                            writeState(this@UpdateDownloadService, STATUS_ERROR, error = state.errorMessage)
                            broadcastState()
                        }
                        state.isFinished && state.savedFilePath != null -> {
                            val apkFile = java.io.File(state.savedFilePath)
                            if (ApkDownloader.isValidApk(applicationContext, apkFile)) {
                                writeState(
                                    this@UpdateDownloadService,
                                    STATUS_COMPLETE,
                                    progress = 1f,
                                    downloadedBytes = state.downloadedBytes,
                                    totalBytes = state.totalBytes,
                                    filePath = state.savedFilePath
                                )
                                updateNotification("Download update selesai")
                            } else {
                                apkFile.delete()
                                writeState(
                                    this@UpdateDownloadService,
                                    STATUS_ERROR,
                                    error = "APK belum lengkap atau paket tidak valid"
                                )
                                updateNotification("Download update gagal")
                            }
                            broadcastState()
                        }
                        else -> {
                            val now = System.currentTimeMillis()
                            val shouldPublish =
                                state.downloadedBytes - lastPersistedBytes >= 256 * 1024L ||
                                    now - lastPersistedAt >= 500L
                            if (shouldPublish) {
                                lastPersistedBytes = state.downloadedBytes
                                lastPersistedAt = now
                                writeState(
                                    this@UpdateDownloadService,
                                    STATUS_DOWNLOADING,
                                    progress = state.progress,
                                    downloadedBytes = state.downloadedBytes,
                                    totalBytes = state.totalBytes
                                )
                                updateNotification(
                                    "Mengunduh update... ${(state.progress * 100).toInt()}%"
                                )
                                broadcastState()
                            }
                        }
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Download service gagal: ${e.message}", e)
                writeState(this@UpdateDownloadService, STATUS_ERROR, error = e.message ?: "Download gagal")
                broadcastState()
            } finally {
                activeJob = false
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf(startId)
            }
        }

        return START_REDELIVER_INTENT
    }

    private fun broadcastState() {
        sendBroadcast(Intent(ACTION_PROGRESS).setPackage(packageName))
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getSystemService(NotificationManager::class.java).createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID,
                    "Download Update MASJID.IO",
                    NotificationManager.IMPORTANCE_LOW
                ).apply { setShowBadge(false) }
            )
        }
    }

    private fun buildNotification(message: String): Notification {
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, CHANNEL_ID)
        } else {
            @Suppress("DEPRECATION")
            Notification.Builder(this)
        }
        return builder
            .setContentTitle("MASJID.IO — Update")
            .setContentText(message)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setOngoing(true)
            .setPriority(Notification.PRIORITY_LOW)
            .build()
    }

    private fun updateNotification(message: String) {
        getSystemService(NotificationManager::class.java)
            .notify(NOTIFICATION_ID, buildNotification(message))
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }
}
