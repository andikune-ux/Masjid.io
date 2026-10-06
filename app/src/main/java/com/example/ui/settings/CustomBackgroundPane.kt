package com.example.ui.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.AppSettings
import com.example.data.model.BackgroundMode
import com.example.ui.components.FilePickerMode
import com.example.ui.components.VideoFilePickerDialog
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.IslamicGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.UrgentRed
import com.example.util.MediaPersistenceHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun CustomBackgroundPane(
    settings: AppSettings,
    onUpdate: (AppSettings) -> Unit,
    onRestart: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // ============================================================
    // V1.04.425 — State untuk Custom File Picker (FILE)
    // ============================================================
    var showBackgroundPicker by remember { mutableStateOf(false) }

    // Helper: proses URI background
    fun processBgUri(uriString: String) {
        scope.launch {
            val localPath = withContext(Dispatchers.IO) {
                MediaPersistenceHelper.copyToPermanent(
                    context = context,
                    sourceUri = uriString,
                    folder = MediaPersistenceHelper.FOLDER_BACKGROUND,
                    fileNamePrefix = "background"
                )
            }
            if (localPath != null) {
                settings.customBackgroundUri?.let { old ->
                    if (old != localPath) MediaPersistenceHelper.deleteFile(old)
                }
                onUpdate(
                    settings.copy(
                        customBackgroundUri = localPath,
                        backgroundMode = BackgroundMode.CUSTOM
                    )
                )
            } else {
                onUpdate(
                    settings.copy(
                        customBackgroundUri = uriString,
                        backgroundMode = BackgroundMode.CUSTOM
                    )
                )
            }
        }
    }

    // ============================================================
    // TOMBOL 1: GALERI (Android Native)
    // ============================================================
    val bgGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) processBgUri(uri.toString())
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Pengaturan Latar Belakang (Background) Layar TV",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGoldLight
        )

        // ============================================================
        // CUSTOM BACKGROUND PICKER — 2 TOMBOL: GALERI + FILE
        // ============================================================
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF091620))
                .border(1.dp, Color(0x33FFD700), RoundedCornerShape(14.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Gambar Background Sendiri (Dari Galeri / Penyimpanan Internal):",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 160.dp, height = 90.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.Black)
                        .border(1.5.dp, IslamicGold, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (!settings.customBackgroundUri.isNullOrBlank()) {
                        AsyncImage(
                            model = settings.customBackgroundUri,
                            contentDescription = "Background Kustom",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text(text = "Belum Ada Gambar", fontSize = 11.sp, color = TextSecondary)
                    }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Unggah foto resolusi tinggi (1920x1080 Full HD atau 4K) interior masjid, arsitektur kubah, atau motif kaligrafi pilihan Anda.",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )

                    // ============================================================
                    // V1.04.425 — 2 TOMBOL: GALERI + FILE
                    // ============================================================
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // Tombol GALERI (buka galeri bawaan HP)
                        TvActionButton(
                            icon = Icons.Default.PhotoLibrary,
                            label = "GALERI",
                            backgroundColor = IslamicGold,
                            textColor = Color(0xFF09141D),
                            onClick = { bgGalleryLauncher.launch("image/*") }
                        )
                        // Tombol FILE (buka custom picker)
                        TvActionButton(
                            icon = Icons.Default.FolderOpen,
                            label = "FILE",
                            backgroundColor = IslamicGold,
                            textColor = Color(0xFF09141D),
                            onClick = { showBackgroundPicker = true }
                        )
                        if (!settings.customBackgroundUri.isNullOrBlank()) {
                            TvActionButton(
                                icon = Icons.Default.Delete,
                                label = "HAPUS",
                                backgroundColor = Color.Transparent,
                                textColor = UrgentRed,
                                isOutlined = true,
                                onClick = {
                                    settings.customBackgroundUri?.let { old ->
                                        MediaPersistenceHelper.deleteFile(old)
                                    }
                                    onUpdate(
                                        settings.copy(
                                            customBackgroundUri = null,
                                            backgroundMode = BackgroundMode.MAKKAH_DYNAMIC
                                        )
                                    )
                                }
                            )
                        }
                    }

                    Text(
                        text = "💡 GALERI = pakai galeri HP. FILE = pilih dari folder (cocok untuk TV).",
                        fontSize = 10.sp,
                        color = TextSecondary.copy(alpha = 0.8f),
                        lineHeight = 14.sp
                    )
                }
            }
        }

        // ============================================================
        // PRESET BACKGROUND THEMES
        // ============================================================
        Text(
            text = "Pilihan Tema Latar Bawaan:",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGoldLight
        )

        val presetList = listOf(
            Triple(
                BackgroundMode.MAKKAH_DYNAMIC,
                "🕋 Makkah Dinamis (Default — Baru)",
                "Langit Makkah bergerak real-time: matahari melengkung dari timur ke barat, bulan bergeser dengan fase asli (sabit/purnama), awan bergerak, burung berterbangan, bintang berkelip saat malam. Cuaca otomatis mengikuti lokasi — hujan, petir, kabut, semua tampil live!"
            ),
            Triple(
                BackgroundMode.NATURE,
                "Langit Dinamis Real-Time (Cerdas)",
                "Warna langit berubah sesuai jam: pagi biru cerah, siang terang, sore keemasan, maghrib senja, malam gelap berbintang."
            ),
            Triple(
                BackgroundMode.KABAH,
                "Ka'bah Al-Mukarramah (Statis)",
                "Gradien malam Ka'bah dengan kilau ornamen Kiswah emas yang megah (statis)."
            ),
            Triple(
                BackgroundMode.EMERALD_GEOMETRIC,
                "Emerald Geometris Arabesque",
                "Pola arabesque islami mewah bernuansa hijau zamrud tua dan garis emas."
            ),
            Triple(
                BackgroundMode.CUSTOM,
                "Gambar Kustom Pengguna",
                if (!settings.customBackgroundUri.isNullOrBlank())
                    "Menggunakan foto dari galeri yang telah Anda pilih."
                else
                    "Pilih foto dari galeri pada menu di atas terlebih dahulu."
            )
        )

        for ((mode, title, desc) in presetList) {
            PresetBackgroundItem(
                title = title,
                description = desc,
                isSelected = settings.backgroundMode == mode,
                onClick = { onUpdate(settings.copy(backgroundMode = mode)) }
            )
        }

        // ============================================================
        // DAFTAR FILE TEMPLATE .iO
        // ============================================================
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "📁 FILE TEMPLATE .iO YANG PERNAH DITERIMA",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGoldLight
        )
        Text(
            text = "File template berisi pengaturan + foto + video dari pengirim. " +
                    "Tekan GUNAKAN untuk apply template, INFO untuk lihat log, " +
                    "HAPUS untuk buang file.",
            fontSize = 12.sp,
            color = TextSecondary,
            lineHeight = 16.sp
        )

        IoBundleListSection(
            currentSettings = settings,
            onApplySettings = { newSettings ->
                onUpdate(newSettings)
            },
            onRestart = onRestart
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ============================================================
        // PICKER DIALOG CALLER
        // ============================================================
        if (showBackgroundPicker) {
            VideoFilePickerDialog(
                mode = FilePickerMode.IMAGE,
                title = "Pilih Background",
                onFileSelected = { file: File ->
                    showBackgroundPicker = false
                    processBgUri(file.absolutePath)
                },
                onDismiss = { showBackgroundPicker = false }
            )
        }
    }
}
// ============================================================
// KOMPONEN: TOMBOL DENGAN FOKUS LEBIH TEBAL
// ============================================================

@Composable
private fun TvActionButton(
    icon: ImageVector,
    label: String,
    backgroundColor: Color,
    textColor: Color,
    onClick: () -> Unit,
    isOutlined: Boolean = false
) {
    var isFocused by remember { mutableStateOf(false) }

    val borderWidth by animateDpAsState(
        targetValue = if (isFocused) 4.dp else if (isOutlined) 1.5.dp else 0.dp,
        animationSpec = tween(200),
        label = "btn_border_width"
    )

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.05f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 800f),
        label = "btn_scale"
    )

    val shadowElevation by animateDpAsState(
        targetValue = if (isFocused) 12.dp else 0.dp,
        animationSpec = tween(200),
        label = "btn_shadow"
    )

    Row(
        modifier = Modifier
            .scale(scale)
            .shadow(
                elevation = shadowElevation,
                shape = RoundedCornerShape(10.dp),
                ambientColor = Color(0x88FFD700),
                spotColor = Color(0x88FFD700)
            )
            .clip(RoundedCornerShape(10.dp))
            .background(if (isOutlined) Color.Transparent else backgroundColor)
            .border(
                width = borderWidth,
                color = when {
                    isFocused -> Color(0xFFFFE44D)
                    isOutlined -> textColor.copy(alpha = 0.6f)
                    else -> Color.Transparent
                },
                shape = RoundedCornerShape(10.dp)
            )
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = textColor,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

// ============================================================
// KOMPONEN: PRESET BACKGROUND ITEM
// ============================================================

@Composable
private fun PresetBackgroundItem(
    title: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    val borderWidth by animateDpAsState(
        targetValue = if (isFocused || isSelected) 2.dp else 1.dp,
        animationSpec = tween(200),
        label = "preset_border_width"
    )

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.02f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 800f),
        label = "preset_scale"
    )

    val shadowElevation by animateDpAsState(
        targetValue = if (isFocused) 10.dp else 0.dp,
        animationSpec = tween(200),
        label = "preset_shadow"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .shadow(
                elevation = shadowElevation,
                shape = RoundedCornerShape(12.dp),
                ambientColor = Color(0x66FFD700),
                spotColor = Color(0x66FFD700)
            )
            .clip(RoundedCornerShape(12.dp))
            .background(
                when {
                    isSelected -> Color(0x33FFD700)
                    isFocused -> Color(0x22FFD700)
                    else -> Color(0xFF091620)
                }
            )
            .border(
                width = borderWidth,
                color = when {
                    isFocused -> Color(0xFFFFE44D)
                    isSelected -> IslamicGold
                    else -> Color(0x22FFFFFF)
                },
                shape = RoundedCornerShape(12.dp)
            )
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        isSelected -> IslamicGoldLight
                        isFocused -> Color(0xFFFFE44D)
                        else -> TextPrimary
                    }
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Terpilih",
                    tint = IslamicGold,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
