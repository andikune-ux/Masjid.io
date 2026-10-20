package dev.andikune.masjidio.util

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.io.File

/**
 * Helper untuk auto-show log crash setelah force close.
 *
 * Cara pakai di MainActivity:
 *   - Di dalam setContent { }:
 *       var showCrash by remember {
 *           mutableStateOf(CrashAutoShowHelper.hasPendingCrash(this@MainActivity))
 *       }
 *       if (showCrash) {
 *           CrashLogDialog(
 *               log = CrashAutoShowHelper.getLastCrashLog(this@MainActivity),
 *               onDismiss = {
 *                   CrashAutoShowHelper.markAsSeen(this@MainActivity)
 *                   showCrash = false
 *               }
 *           )
 *       } else {
 *           // UI utama
 *       }
 *
 * Tidak perlu modifikasi CrashReporter.kt.
 * Cukup baca file yang sudah disimpan CrashReporter di filesDir/crashes/.
 */
object CrashAutoShowHelper {

    private const val PREF_NAME = "crash_auto_show_prefs"
    private const val KEY_LAST_SEEN = "last_seen_time"
    private const val CRASH_FOLDER = "crashes"

    /**
     * Cek apakah ada crash baru yang belum dilihat user.
     * Cara: bandingkan timestamp file crash terbaru dengan waktu terakhir dilihat.
     */
    fun hasPendingCrash(context: Context): Boolean {
        val latest = getLatestCrashFile(context) ?: return false
        val lastSeen = context
            .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getLong(KEY_LAST_SEEN, 0L)
        return latest.lastModified() > lastSeen
    }

    /**
     * Ambil isi log crash terbaru sebagai String.
     */
    fun getLastCrashLog(context: Context): String {
        val latest = getLatestCrashFile(context) ?: return ""
        return runCatching { latest.readText() }.getOrDefault("")
    }

    /**
     * Tandai semua crash sudah dilihat.
     * Panggil setelah user klik tombol "Kembali".
     */
    fun markAsSeen(context: Context) {
        val latest = getLatestCrashFile(context) ?: return
        context
            .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putLong(KEY_LAST_SEEN, latest.lastModified())
            .apply()
    }

    /**
     * Cari file crash terbaru di filesDir/crashes/.
     */
    private fun getLatestCrashFile(context: Context): File? {
        val dir = File(context.filesDir, CRASH_FOLDER)
        if (!dir.exists() || !dir.isDirectory) return null
        return dir
            .listFiles()
            ?.filter { it.isFile && it.name.endsWith(".txt") }
            ?.maxByOrNull { it.lastModified() }
    }
}

/**
 * Dialog log crash dengan tombol SALIN + KEMBALI.
 * Non-dismissible: user wajib klik tombol (biar tidak kelewat).
 */
@Composable
fun CrashLogDialog(
    log: String,
    onDismiss: () -> Unit
) {
    val clipboard = LocalClipboardManager.current
    var copied by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = { /* wajib klik tombol */ },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.85f),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

                Text(
                    text = "⚠️ Aplikasi Sempat Crash",
                    fontSize = 20.sp,
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Log di bawah bisa Anda salin dan kirim ke AI untuk perbaikan.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(12.dp))

                Surface(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = log.ifEmpty { "(log kosong)" },
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                            .verticalScroll(rememberScrollState())
                    )
                }

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            clipboard.setText(AnnotatedString(log))
                            copied = true
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (copied) "✓ Tersalin" else "📋 Salin")
                    }
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Kembali")
                    }
                }
            }
        }
    }
}
