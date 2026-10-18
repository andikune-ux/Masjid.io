package dev.andikune.masjidio.ui.components

import android.net.Uri
import android.os.Environment
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.request.videoFrameMillis
import dev.andikune.masjidio.ui.theme.IslamicGold
import dev.andikune.masjidio.ui.theme.IslamicGoldLight
import dev.andikune.masjidio.ui.theme.TextPrimary
import dev.andikune.masjidio.ui.theme.TextSecondary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Locale

enum class FilePickerMode { VIDEO, IMAGE, ALL }

@Composable
fun VideoFilePickerDialog(
    mode: FilePickerMode,
    title: String,
    onFileSelected: (File) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var fileList by remember { mutableStateOf<List<File>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Scan file di background
    LaunchedEffect(mode) {
        isLoading = true
        val files = withContext(Dispatchers.IO) {
            scanFiles(mode)
        }
        fileList = files
        isLoading = false
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF0B1720))
                .border(2.dp, IslamicGold, RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = IslamicGoldLight
                )
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0x33FF5252))
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Close, "Tutup", tint = Color.White)
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = IslamicGold)
                }
            } else if (fileList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Tidak ada file ditemukan.", color = TextSecondary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(fileList) { file ->
                        FileItemRow(
                            file = file,
                            mode = mode,
                            onSelect = { onFileSelected(file) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FileItemRow(
    file: File,
    mode: FilePickerMode,
    onSelect: () -> Unit
) {
    val context = LocalContext.current
    var isFocused by remember { mutableStateOf(false) }
    var isPreviewing by remember { mutableStateOf(false) } // Untuk touchscreen (tap 1x)
    
    // State untuk ExoPlayer (video hover)
    var exoPlayer by remember { mutableStateOf<ExoPlayer?>(null) }
    var isPlayerReady by remember { mutableStateOf(false) }

    val isVideo = file.extension.lowercase(Locale.getDefault()) in listOf("mp4", "mkv", "webm", "avi")

    // LOGIKA FOKUS D-PAD (MULAI PRATINJAU)
    LaunchedEffect(isFocused, isPreviewing) {
        val shouldPreview = isFocused || isPreviewing
        if (shouldPreview && isVideo) {
            // Inisialisasi ExoPlayer hanya jika video
            if (exoPlayer == null) {
                exoPlayer = ExoPlayer.Builder(context).build().apply {
                    setMediaItem(MediaItem.fromUri(Uri.fromFile(file)))
                    prepare()
                    playWhenReady = true
                    volume = 0f // Mute saat pratinjau
                }
                // Tunggu frame pertama siap
                delay(300) 
                isPlayerReady = true
            }
        } else {
            // Hentikan & lepas player jika fokus keluar (Optimasi RAM)
            exoPlayer?.release()
            exoPlayer = null
            isPlayerReady = false
        }
    }

    // Bersihkan player saat komponen dihancurkan
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer?.release()
        }
    }

    Box {
        // ============================================================
        // KARTU POPUP HOVER (Video Pratinjau)
        // ============================================================
        AnimatedVisibility(
            visible = (isFocused || isPreviewing) && isVideo && isPlayerReady,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(bottom = 80.dp) // Muncul di atas kartu
        ) {
            Box(
                modifier = Modifier
                    .width(280.dp)
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black)
                    .border(2.dp, IslamicGold, RoundedCornerShape(12.dp))
            ) {
                exoPlayer?.let { player ->
                    AndroidView(
                        factory = { ctx ->
                            PlayerView(ctx).apply {
                                useController = false // Sembunyikan kontrol
                                this.player = player
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        // ============================================================
        // KARTU FILE UTAMA (Thumbnail + Info)
        // ============================================================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(if (isFocused || isPreviewing) Color(0x33FFD700) else Color(0xFF142735))
                .border(
                    width = if (isFocused || isPreviewing) 2.dp else 1.dp,
                    color = if (isFocused || isPreviewing) IslamicGold else Color.Transparent,
                    shape = RoundedCornerShape(12.dp)
                )
                .onFocusChanged { isFocused = it.isFocused }
                .focusable()
                .clickable {
                    // Logika HP: Tap 1 Preview, Tap 2 Pilih
                    if (isVideo) {
                        if (isPreviewing) {
                            onSelect()
                        } else {
                            isPreviewing = true
                        }
                    } else {
                        // Foto langsung pilih
                        onSelect()
                    }
                }
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail
            Box(
                modifier = Modifier
                    .size(width = 120.dp, height = 68.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                val model = ImageRequest.Builder(context)
                    .data(file)
                    .apply { if (isVideo) videoFrameMillis(1000L) } // Ambil frame detik ke-1
                    .crossfade(true)
                    .build()

                AsyncImage(
                    model = model,
                    contentDescription = "Thumbnail",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                if (isVideo) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Info File
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = file.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "📁 ${formatFileSize(file.length())}",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }
    }
}

// ============================================================
// HELPER: SCAN FILE DI PENYIMPANAN
// ============================================================
private fun scanFiles(mode: FilePickerMode): List<File> {
    val result = mutableListOf<File>()
    val root = Environment.getExternalStorageDirectory()
    
    val videoExts = listOf("mp4", "mkv", "webm", "avi", "3gp")
    val imageExts = listOf("jpg", "jpeg", "png", "webp")

    fun scan(dir: File) {
        val files = dir.listFiles() ?: return
        for (file in files) {
            if (file.isDirectory && !file.name.startsWith(".") && file.name != "Android") {
                scan(file)
            } else if (file.isFile) {
                val ext = file.extension.lowercase(Locale.getDefault())
                val isVid = ext in videoExts
                val isImg = ext in imageExts

                when (mode) {
                    FilePickerMode.VIDEO -> if (isVid) result.add(file)
                    FilePickerMode.IMAGE -> if (isImg) result.add(file)
                    FilePickerMode.ALL -> if (isVid || isImg) result.add(file)
                }
            }
        }
    }
    
    // Scan folder umum (DCIM, Pictures, Movies, Download, masjid.io)
    scan(File(root, "DCIM"))
    scan(File(root, "Pictures"))
    scan(File(root, "Movies"))
    scan(File(root, "Download"))
    scan(File(root, "masjid.io"))

    return result.sortedByDescending { it.lastModified() }
}

private fun formatFileSize(size: Long): String {
    if (size <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()
    return String.format(Locale.US, "%.1f %s", size / Math.pow(1024.0, digitGroups.toDouble()), units[digitGroups])
}
