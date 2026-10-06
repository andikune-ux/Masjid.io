package dev.andikune.masjidio.ui.settings

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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import dev.andikune.masjidio.data.model.AppSettings
import dev.andikune.masjidio.ui.components.FilePickerMode
import dev.andikune.masjidio.ui.components.TvSlider
import dev.andikune.masjidio.ui.components.VideoFilePickerDialog
import dev.andikune.masjidio.ui.theme.IslamicGold
import dev.andikune.masjidio.ui.theme.IslamicGoldLight
import dev.andikune.masjidio.ui.theme.IslamicGreen
import dev.andikune.masjidio.ui.theme.TextPrimary
import dev.andikune.masjidio.ui.theme.TextSecondary
import dev.andikune.masjidio.ui.theme.UrgentRed
import dev.andikune.masjidio.util.MediaPersistenceHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun QrisSettingsPane(
    settings: AppSettings,
    onUpdate: (AppSettings) -> Unit,
    onTestQrisFocus: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // ============================================================
    // V1.04.425 — State untuk Custom File Picker (FILE)
    // ============================================================
    var showQrisPicker by remember { mutableStateOf(false) }

    // Helper: proses QRIS (copy ke folder permanen)
    fun processQrisUri(uriString: String) {
        scope.launch {
            val localPath = withContext(Dispatchers.IO) {
                MediaPersistenceHelper.copyToPermanent(
                    context = context,
                    sourceUri = uriString,
                    folder = MediaPersistenceHelper.FOLDER_QRIS,
                    fileNamePrefix = "qris"
                )
            }
            if (localPath != null) {
                settings.qrisPhotoUri?.let { old ->
                    if (old != localPath) MediaPersistenceHelper.deleteFile(old)
                }
                onUpdate(settings.copy(qrisPhotoUri = localPath))
            } else {
                onUpdate(settings.copy(qrisPhotoUri = uriString))
            }
        }
    }

    // ============================================================
    // TOMBOL 1: GALERI (Android Native)
    // ============================================================
    val qrisGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) processQrisUri(uri.toString())
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Pengaturan Donasi QRIS & Rekening Kas Masjid",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGoldLight
        )

        // ============================================================
        // PREVIEW & PICKER QRIS — 2 TOMBOL: GALERI + FILE
        // ============================================================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF091620))
                .border(1.dp, Color(0x33FFD700), RoundedCornerShape(14.dp))
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Preview QRIS
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .border(2.dp, IslamicGold, RoundedCornerShape(12.dp))
                    .padding(6.dp),
                contentAlignment = Alignment.Center
            ) {
                if (!settings.qrisPhotoUri.isNullOrBlank()) {
                    AsyncImage(
                        model = settings.qrisPhotoUri,
                        contentDescription = "Preview QRIS",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.QrCode2,
                            contentDescription = null,
                            tint = Color(0xFF0A1822),
                            modifier = Modifier.size(54.dp)
                        )
                        Text(
                            text = "Belum ada QRIS",
                            fontSize = 11.sp,
                            color = Color(0xFF555555)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Foto Kode QRIS Donasi",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Unggah barcode QRIS masjid dari galeri perangkat atau penyimpanan internal.",
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
                        onClick = { qrisGalleryLauncher.launch("image/*") }
                    )
                    // Tombol FILE (buka custom picker)
                    TvActionButton(
                        icon = Icons.Default.FolderOpen,
                        label = "FILE",
                        backgroundColor = IslamicGold,
                        textColor = Color(0xFF09141D),
                        onClick = { showQrisPicker = true }
                    )
                    if (!settings.qrisPhotoUri.isNullOrBlank()) {
                        TvActionButton(
                            icon = Icons.Default.Delete,
                            label = "HAPUS",
                            backgroundColor = Color.Transparent,
                            textColor = UrgentRed,
                            isOutlined = true,
                            onClick = {
                                settings.qrisPhotoUri?.let { old ->
                                    MediaPersistenceHelper.deleteFile(old)
                                }
                                onUpdate(settings.copy(qrisPhotoUri = null))
                            }
                        )
                    }
                }

                // Tombol Uji Tampilan
                TvActionButton(
                    icon = Icons.Default.Visibility,
                    label = "UJI TAMPILAN",
                    backgroundColor = IslamicGreen,
                    textColor = Color(0xFF09141D),
                    onClick = onTestQrisFocus
                )

                Text(
                    text = "💡 GALERI = pakai galeri HP. FILE = pilih dari folder (cocok untuk TV).",
                    fontSize = 10.sp,
                    color = TextSecondary.copy(alpha = 0.8f),
                    lineHeight = 14.sp
                )
            }
        }

        // ============================================================
        // SLIDER INTERVAL & DURASI
        // ============================================================
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF091620))
                .border(1.dp, Color(0x33FFD700), RoundedCornerShape(14.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Pengaturan Interval & Durasi Tayang Layar Penuh QRIS",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = IslamicGoldLight
            )

            TvSlider(
                label = "Interval Penayangan Otomatis",
                value = settings.qrisIntervalMinutes.toFloat(),
                onValueChange = { onUpdate(settings.copy(qrisIntervalMinutes = it.toInt())) },
                valueRange = 0f..30f,
                steps = 29,
                formatter = { v ->
                    if (v.toInt() == 0) "Nonaktif (Manual)" else "${v.toInt()} Menit Sekali"
                }
            )

            TvSlider(
                label = "Lama Tampil Setiap Sesi",
                value = settings.qrisDisplayDurationSeconds.toFloat(),
                onValueChange = { onUpdate(settings.copy(qrisDisplayDurationSeconds = it.toInt())) },
                valueRange = 5f..60f,
                steps = 54,
                unit = " Detik"
            )
        }

        // ============================================================
        // BANK ACCOUNT DETAILS
        // ============================================================
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF091620))
                .border(1.dp, Color(0x33FFD700), RoundedCornerShape(14.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Informasi Nomor Rekening Masjid:",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = IslamicGoldLight
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = settings.bankName,
                    onValueChange = { onUpdate(settings.copy(bankName = it)) },
                    label = { Text("Nama Bank") },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = IslamicGold,
                        unfocusedBorderColor = Color(0x44FFFFFF)
                    )
                )

                OutlinedTextField(
                    value = settings.bankAccountNumber,
                    onValueChange = { onUpdate(settings.copy(bankAccountNumber = it)) },
                    label = { Text("Nomor Rekening") },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = IslamicGold,
                        unfocusedBorderColor = Color(0x44FFFFFF)
                    )
                )
            }

            OutlinedTextField(
                value = settings.bankAccountHolder,
                onValueChange = { onUpdate(settings.copy(bankAccountHolder = it)) },
                label = { Text("Atas Nama Rekening") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = IslamicGold,
                    unfocusedBorderColor = Color(0x44FFFFFF)
                )
            )
        }

        // ============================================================
        // PICKER DIALOG CALLER
        // ============================================================
        if (showQrisPicker) {
            VideoFilePickerDialog(
                mode = FilePickerMode.IMAGE,
                title = "Pilih File QRIS",
                onFileSelected = { file: File ->
                    showQrisPicker = false
                    processQrisUri(file.absolutePath)
                },
                onDismiss = { showQrisPicker = false }
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
