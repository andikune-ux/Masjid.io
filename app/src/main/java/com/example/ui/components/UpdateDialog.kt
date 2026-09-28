package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.IslamicGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.UrgentRed

/**
 * Dialog update dengan 3 tombol: Update / Nanti / Skip
 *
 * @param currentVersion Versi saat ini (contoh: V1.28.1)
 * @param latestVersion Versi terbaru dari GitHub
 * @param releaseNotes Catatan rilis dari GitHub
 * @param forceUpdate Kalau true → hanya tombol "Update" yang muncul (wajib update)
 * @param downloadProgress Progress download (0f..1f). Null = belum mulai.
 * @param isDownloading Sedang download?
 * @param isInstalling Sedang install?
 * @param onUpdateClick Klik tombol "Update Sekarang"
 * @param onLaterClick Klik tombol "Nanti" (tampil lagi nanti)
 * @param onSkipClick Klik tombol "Skip" (tidak tampil lagi sampai versi lebih tinggi)
 */
@Composable
fun UpdateDialog(
    currentVersion: String,
    latestVersion: String,
    releaseNotes: String?,
    forceUpdate: Boolean = false,
    downloadProgress: Float? = null,
    isDownloading: Boolean = false,
    isInstalling: Boolean = false,
    onUpdateClick: () -> Unit,
    onLaterClick: () -> Unit,
    onSkipClick: () -> Unit
) {
    Dialog(
        onDismissRequest = {
            if (!forceUpdate && !isDownloading) onLaterClick()
        },
        properties = DialogProperties(
            dismissOnBackPress = !forceUpdate && !isDownloading,
            dismissOnClickOutside = false
        )
    ) {
        Column(
            modifier = Modifier
                .width(600.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF0B1720))
                .border(2.dp, IslamicGold, RoundedCornerShape(20.dp))
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // HEADER
            Icon(
                imageVector = Icons.Default.SystemUpdate,
                contentDescription = null,
                tint = IslamicGold,
                modifier = Modifier.size(56.dp)
            )

            Text(
                text = "UPDATE TERSEDIA",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = IslamicGoldLight
            )

            Text(
                text = "Versi baru MASJID.IO sudah tersedia",
                fontSize = 14.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            // VERSI
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x22000000), RoundedCornerShape(10.dp))
                    .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(10.dp))
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Versi Saat Ini", fontSize = 11.sp, color = TextSecondary)
                    Text(currentVersion, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Versi Terbaru", fontSize = 11.sp, color = TextSecondary)
                    Text(latestVersion, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = IslamicGreen)
                }
            }

            // CATATAN RILIS
            if (!releaseNotes.isNullOrBlank()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0x22000000), RoundedCornerShape(10.dp))
                        .border(1.dp, IslamicGold.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .padding(14.dp)
                ) {
                    Text(
                        text = "📋 Yang Baru di Versi $latestVersion:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = IslamicGoldLight
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = releaseNotes,
                            fontSize = 13.sp,
                            color = TextPrimary,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            // PROGRESS (saat download/install)
            if (isDownloading && downloadProgress != null) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Mengunduh update... ${(downloadProgress * 100).toInt()}%",
                        fontSize = 13.sp,
                        color = IslamicGoldLight,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0x33FFFFFF))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(downloadProgress)
                                .height(12.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(IslamicGold)
                        )
                    }
                }
            }

            if (isInstalling) {
                Text(
                    text = "📦 Menginstall update...",
                    fontSize = 14.sp,
                    color = IslamicGreen,
                    fontWeight = FontWeight.Bold
                )
            }

            // TOMBOL
            Spacer(modifier = Modifier.height(8.dp))

            if (forceUpdate) {
                // MODE WAJIB UPDATE — hanya 1 tombol
                DialogButton(
                    icon = Icons.Default.Download,
                    text = "UPDATE SEKARANG",
                    backgroundColor = IslamicGold,
                    textColor = Color(0xFF09141D),
                    enabled = !isDownloading && !isInstalling,
                    onClick = onUpdateClick
                )

                Text(
                    text = "⚠️ Update wajib. Aplikasi tidak dapat digunakan sebelum update selesai.",
                    fontSize = 12.sp,
                    color = UrgentRed,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            } else {
                // MODE BOLEH SKIP — 3 tombol
                DialogButton(
                    icon = Icons.Default.Download,
                    text = "UPDATE SEKARANG",
                    backgroundColor = IslamicGold,
                    textColor = Color(0xFF09141D),
                    enabled = !isDownloading && !isInstalling,
                    onClick = onUpdateClick
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        DialogButton(
                            icon = Icons.Default.Schedule,
                            text = "NANTI",
                            backgroundColor = Color(0xFF142735),
                            textColor = TextPrimary,
                            enabled = !isDownloading && !isInstalling,
                            onClick = onLaterClick
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        DialogButton(
                            icon = Icons.Default.SkipNext,
                            text = "SKIP",
                            backgroundColor = Color(0xFF142735),
                            textColor = UrgentRed,
                            enabled = !isDownloading && !isInstalling,
                            onClick = onSkipClick
                        )
                    }
                }

                Text(
                    text = "Nanti: muncul lagi saat app dibuka. Skip: tidak muncul sampai versi lebih tinggi.",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun DialogButton(
    icon: ImageVector,
    text: String,
    backgroundColor: Color,
    textColor: Color,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    val bg = if (!enabled) backgroundColor.copy(alpha = 0.3f) else backgroundColor
    val borderColor = if (isFocused && enabled) IslamicGoldLight else Color.Transparent

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .border(if (isFocused && enabled) 3.dp else 0.dp, borderColor, RoundedCornerShape(12.dp))
            .onFocusChanged { isFocused = it.isFocused }
            .focusable(enabled)
            .clickable(enabled) { onClick() }
            .padding(vertical = 14.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = textColor,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}
