package dev.andikune.masjidio.ui.components

import android.os.Environment
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.andikune.masjidio.ui.theme.IslamicGold
import dev.andikune.masjidio.ui.theme.IslamicGoldLight
import dev.andikune.masjidio.ui.theme.IslamicGreen
import dev.andikune.masjidio.ui.theme.TextPrimary
import dev.andikune.masjidio.ui.theme.TextSecondary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * VideoFilePickerDialog — Custom File Picker dalam aplikasi.
 *
 * V1.04.423 BARU.
 *
 * Fitur:
 *   - Rekursif scan /sdcard/ + semua subfolder
 *   - Mode: VIDEO (hanya video) / IMAGE (hanya foto) / ALL (semua file)
 *   - Ramah D-pad remote TV (auto-focus, clickable semua item)
 *   - Navigasi masuk/keluar folder dengan tombol OK
 *   - Tampilkan ukuran file + tipe
 *
 * Dipakai di VideoSettingsPane (mode VIDEO), dan bisa dipakai
 * di tempat lain untuk IMAGE atau ALL (fitur masa depan).
 */
enum class FilePickerMode {
    VIDEO,   // hanya video (mp4, mkv, webm, avi, mov, 3gp, dll)
    IMAGE,   // hanya foto (jpg, jpeg, png, gif, webp, bmp, dll)
    ALL      // semua file (untuk fitur masa depan)
}

// ============================================================
// KONSTANTA EKSTENSI
// ============================================================
private val VIDEO_EXTENSIONS = setOf(
    "mp4", "mkv", "webm", "avi", "mov", "3gp", "flv", "ts", "m4v", "wmv"
)

private val IMAGE_EXTENSIONS = setOf(
    "jpg", "jpeg", "png", "gif", "webp", "bmp", "heic", "heif"
)

private val AUDIO_EXTENSIONS = setOf(
    "mp3", "wav", "ogg", "m4a", "aac", "flac"
)

private val DOC_EXTENSIONS = setOf(
    "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt"
)

// ============================================================
// DATA CLASS
// ============================================================
data class PickerItem(
    val file: File,
    val isDirectory: Boolean
) {
    val name: String get() = file.name
    val path: String get() = file.absolutePath
    val extension: String get() = file.extension.lowercase()

    val sizeText: String get() = if (isDirectory) "" else formatSize(file.length())

    val isVideo: Boolean get() = extension in VIDEO_EXTENSIONS
    val isImage: Boolean get() = extension in IMAGE_EXTENSIONS
    val isAudio: Boolean get() = extension in AUDIO_EXTENSIONS
    val isDoc: Boolean get() = extension in DOC_EXTENSIONS
}

// ============================================================
// HELPER FUNCTIONS
// ============================================================

/**
 * Cek apakah file cocok dengan mode yang dipilih.
 */
private fun isFileMatchingMode(file: File, mode: FilePickerMode): Boolean {
    if (file.isDirectory) return true
    val ext = file.extension.lowercase()
    return when (mode) {
        FilePickerMode.VIDEO -> ext in VIDEO_EXTENSIONS
        FilePickerMode.IMAGE -> ext in IMAGE_EXTENSIONS
        FilePickerMode.ALL -> true
    }
}

/**
 * Cek apakah folder mengandung file yang cocok (rekursif).
 * Skip folder sistem yang tidak perlu.
 */
private fun folderContainsMatchingFiles(folder: File, mode: FilePickerMode): Boolean {
    if (!folder.isDirectory || !folder.canRead()) return false

    val skipFolders = setOf(
        "Android", ".thumbnails", ".cache", ".trash",
        "LOST.DIR", ".nomedia", "cache"
    )
    if (folder.name in skipFolders) return false

    return try {
        val files = folder.listFiles() ?: return false
        for (f in files) {
            if (f.isDirectory) {
                if (folderContainsMatchingFiles(f, mode)) return true
            } else if (isFileMatchingMode(f, mode)) {
                return true
            }
        }
        false
    } catch (e: Exception) {
        false
    }
}

/**
 * Scan isi folder (langsung, tidak rekursif).
 * Tampilkan semua file yang cocok + semua subfolder.
 */
private fun scanFolder(folder: File, mode: FilePickerMode): List<PickerItem> {
    if (!folder.exists() || !folder.isDirectory || !folder.canRead()) {
        return emptyList()
    }

    return try {
        val files = folder.listFiles() ?: return emptyList()
        val result = mutableListOf<PickerItem>()

        // 1. Folder dulu — hanya folder yang mengandung file cocok
        files.filter { it.isDirectory && !it.name.startsWith(".") }
            .forEach { dir ->
                if (folderContainsMatchingFiles(dir, mode)) {
                    result.add(PickerItem(dir, isDirectory = true))
                }
            }

        // 2. File yang cocok
        files.filter { it.isFile && isFileMatchingMode(it, mode) }
            .forEach { file ->
                result.add(PickerItem(file, isDirectory = false))
            }

        // 3. Sort: folder dulu, lalu file (alfabet)
        result.sortedWith(
            compareByDescending<PickerItem> { it.isDirectory }
                .thenBy { it.name.lowercase() }
        )
    } catch (e: Exception) {
        Log.e("VideoFilePicker", "Scan error: ${e.message}", e)
        emptyList()
    }
}

/**
 * Ambil list folder root yang akan discan pertama kali.
 */
private fun getRootFolders(): List<File> {
    val result = mutableListOf<File>()
    val sd = Environment.getExternalStorageDirectory()
    if (sd != null && sd.exists()) {
        result.add(sd)
    }
    return result
}

/**
 * Format ukuran file jadi string human-readable.
 */
private fun formatSize(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${bytes / 1024} KB"
        bytes < 1024 * 1024 * 1024 -> String.format("%.1f MB", bytes / (1024.0 * 1024.0))
        else -> String.format("%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0))
    }
}

/**
 * Pilih icon sesuai tipe file.
 */
private fun getFileIcon(item: PickerItem): ImageVector {
    return when {
        item.isDirectory -> Icons.Default.Folder
        item.isVideo -> Icons.Default.VideoFile
        item.isImage -> Icons.Default.Image
        item.isAudio -> Icons.Default.MusicNote
        item.isDoc -> Icons.Default.Description
        else -> Icons.Default.InsertDriveFile
    }
}

/**
 * Warna icon sesuai tipe.
 */
private fun getFileColor(item: PickerItem): Color {
    return when {
        item.isDirectory -> IslamicGold
        item.isVideo -> Color(0xFF64B5F6)
        item.isImage -> Color(0xFF81C784)
        item.isAudio -> Color(0xFFBA68C8)
        item.isDoc -> Color(0xFFFFB74D)
        else -> TextSecondary
    }
}
// ============================================================
// DIALOG UTAMA — VideoFilePickerDialog
// ============================================================
@Composable
fun VideoFilePickerDialog(
    mode: FilePickerMode,
    title: String = "Pilih File",
    onFileSelected: (File) -> Unit,
    onDismiss: () -> Unit
) {
    var currentFolder by remember { mutableStateOf<File?>(null) }
    var folderHistory by remember { mutableStateOf<List<File>>(emptyList()) }
    var items by remember { mutableStateOf<List<PickerItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedItem by remember { mutableStateOf<PickerItem?>(null) }

    // ============================================================
    // INITIAL LOAD — scan root /sdcard/
    // ============================================================
    LaunchedEffect(mode) {
        isLoading = true
        val roots = getRootFolders()
        currentFolder = roots.firstOrNull()
        currentFolder?.let { folder ->
            items = withContext(Dispatchers.IO) {
                scanFolder(folder, mode)
            }
        }
        isLoading = false
    }

    // ============================================================
    // LOAD saat folder berubah
    // ============================================================
    LaunchedEffect(currentFolder, mode) {
        val folder = currentFolder ?: return@LaunchedEffect
        isLoading = true
        items = withContext(Dispatchers.IO) {
            scanFolder(folder, mode)
        }
        isLoading = false
    }

    // ============================================================
    // NAVIGASI
    // ============================================================
    fun navigateIntoFolder(folder: File) {
        currentFolder?.let { old ->
            folderHistory = folderHistory + old
        }
        currentFolder = folder
        selectedItem = null
    }

    fun navigateBack() {
        if (folderHistory.isNotEmpty()) {
            val parent = folderHistory.last()
            folderHistory = folderHistory.dropLast(1)
            currentFolder = parent
            selectedItem = null
        } else {
            onDismiss()
        }
    }

    fun handleItemClick(item: PickerItem) {
        if (item.isDirectory) {
            navigateIntoFolder(item.file)
        } else {
            selectedItem = item
        }
    }

    fun handleItemDoubleClick(item: PickerItem) {
        if (!item.isDirectory) {
            onFileSelected(item.file)
        }
    }

    // ============================================================
    // DIALOG UI
    // ============================================================
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFF0B1720))
                .border(2.dp, IslamicGold, RoundedCornerShape(18.dp))
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ============================================================
            // HEADER
            // ============================================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = when (mode) {
                            FilePickerMode.VIDEO -> Icons.Default.VideoFile
                            FilePickerMode.IMAGE -> Icons.Default.Image
                            FilePickerMode.ALL -> Icons.Default.Folder
                        },
                        contentDescription = null,
                        tint = IslamicGold,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = IslamicGoldLight
                        )
                        Text(
                            text = when (mode) {
                                FilePickerMode.VIDEO -> "Hanya file video ditampilkan"
                                FilePickerMode.IMAGE -> "Hanya file foto ditampilkan"
                                FilePickerMode.ALL -> "Semua jenis file ditampilkan"
                            },
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                // Tombol TUTUP
                CloseHeaderButton(onClick = onDismiss)
            }

            // ============================================================
            // CURRENT FOLDER PATH
            // ============================================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF07121A))
                    .border(1.dp, IslamicGold.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Folder,
                        contentDescription = null,
                        tint = IslamicGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = currentFolder?.absolutePath ?: "(loading...)",
                        fontSize = 12.sp,
                        color = TextPrimary,
                        fontFamily = FontFamily.Monospace,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // ============================================================
            // KONTEN UTAMA — File List / Loading / Empty
            // ============================================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF07121A))
                    .border(1.dp, IslamicGold.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
            ) {
                when {
                    isLoading -> LoadingView()
                    items.isEmpty() -> EmptyView(mode)
                    else -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(6.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            items(items, key = { it.path }) { item ->
                                FileItemRow(
                                    item = item,
                                    isSelected = selectedItem?.path == item.path,
                                    onClick = { handleItemClick(item) },
                                    onDoubleClick = { handleItemDoubleClick(item) }
                                )
                            }
                        }
                    }
                }
            }

            // ============================================================
            // FOOTER BUTTONS
            // ============================================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // KEMBALI / BATAL
                PickerFooterButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    label = if (folderHistory.isNotEmpty()) "KEMBALI" else "BATAL",
                    backgroundColor = Color(0xFF142735),
                    textColor = TextPrimary,
                    modifier = Modifier.weight(1f),
                    onClick = { navigateBack() }
                )

                // PILIH
                PickerFooterButton(
                    icon = Icons.Default.Check,
                    label = "PILIH FILE INI",
                    backgroundColor = if (selectedItem != null && !selectedItem!!.isDirectory)
                        IslamicGreen else Color(0x44333333),
                    textColor = if (selectedItem != null && !selectedItem!!.isDirectory)
                        Color.White else TextSecondary.copy(alpha = 0.5f),
                    modifier = Modifier.weight(1.5f),
                    enabled = selectedItem != null && !selectedItem!!.isDirectory,
                    onClick = {
                        selectedItem?.let { sel ->
                            if (!sel.isDirectory) {
                                onFileSelected(sel.file)
                            }
                        }
                    }
                )
            }
        }
    }
}

// ============================================================
// FILE ITEM ROW
// ============================================================
@Composable
private fun FileItemRow(
    item: PickerItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    onDoubleClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    val bgColor = when {
        isSelected -> Color(0x4481C784)
        isFocused -> Color(0x33FFD700)
        else -> Color(0x33000000)
    }

    val borderColor = when {
        isSelected -> IslamicGreen
        isFocused -> IslamicGoldLight
        else -> Color(0x22FFFFFF)
    }

    val borderWidth = when {
        isSelected -> 2.dp
        isFocused -> 2.5.dp
        else -> 1.dp
    }

    // Long-press untuk double-click (pilih langsung)
    var longPressHandled by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(borderWidth, borderColor, RoundedCornerShape(8.dp))
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .clickable {
                if (longPressHandled) {
                    longPressHandled = false
                    return@clickable
                }
                onClick()
            }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ICON
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(getFileColor(item).copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = getFileIcon(item),
                contentDescription = null,
                tint = getFileColor(item),
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // NAMA + DETAIL
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.name,
                fontSize = 13.sp,
                fontWeight = if (item.isDirectory) FontWeight.Bold else FontWeight.Medium,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (!item.isDirectory) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${item.extension.uppercase()} • ${item.sizeText}",
                    fontSize = 10.sp,
                    color = TextSecondary,
                    fontFamily = FontFamily.Monospace
                )
            } else {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Folder",
                    fontSize = 10.sp,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // CHECKMARK kalau selected
        if (isSelected && !item.isDirectory) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(IslamicGreen),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

// ============================================================
// LOADING VIEW
// ============================================================
@Composable
private fun LoadingView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = IslamicGoldLight)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Memindai folder...",
                fontSize = 13.sp,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Mungkin butuh waktu beberapa detik",
                fontSize = 11.sp,
                color = TextSecondary.copy(alpha = 0.7f)
            )
        }
    }
}

// ============================================================
// EMPTY VIEW
// ============================================================
@Composable
private fun EmptyView(mode: FilePickerMode) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Folder,
                contentDescription = null,
                tint = IslamicGold.copy(alpha = 0.4f),
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Tidak ada file di folder ini",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = when (mode) {
                    FilePickerMode.VIDEO -> "Belum ada file video di folder ini atau subfolder"
                    FilePickerMode.IMAGE -> "Belum ada file foto di folder ini atau subfolder"
                    FilePickerMode.ALL -> "Folder ini kosong atau tidak dapat diakses"
                },
                fontSize = 12.sp,
                color = TextSecondary.copy(alpha = 0.7f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 16.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "💡 Coba navigasi ke folder lain, atau transfer file via iO Control dari HP",
                fontSize = 11.sp,
                color = IslamicGold.copy(alpha = 0.7f),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 14.sp
            )
        }
    }
}

// ============================================================
// CLOSE HEADER BUTTON
// ============================================================
@Composable
private fun CloseHeaderButton(onClick: () -> Unit) {
    var isFocused by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(if (isFocused) Color(0x44FFD700) else Color(0xFF142735))
            .border(
                width = if (isFocused) 2.dp else 1.dp,
                color = if (isFocused) IslamicGoldLight else IslamicGold.copy(alpha = 0.5f),
                shape = RoundedCornerShape(10.dp)
            )
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Tutup",
            tint = if (isFocused) IslamicGoldLight else IslamicGold,
            modifier = Modifier.size(22.dp)
        )
    }
}

// ============================================================
// PICKER FOOTER BUTTON
// ============================================================
@Composable
private fun PickerFooterButton(
    icon: ImageVector,
    label: String,
    backgroundColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(backgroundColor)
            .border(
                width = if (isFocused && enabled) 3.dp else 0.dp,
                color = if (isFocused && enabled) IslamicGoldLight else Color.Transparent,
                shape = RoundedCornerShape(10.dp)
            )
            .onFocusChanged { isFocused = it.isFocused }
            .focusable(enabled)
            .clickable(enabled) { onClick() }
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = textColor,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}
