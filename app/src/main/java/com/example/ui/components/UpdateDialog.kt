package dev.andikune.masjidio.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusGroup
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.andikune.masjidio.ui.theme.IslamicGold
import dev.andikune.masjidio.ui.theme.IslamicGoldLight
import dev.andikune.masjidio.ui.theme.IslamicGreen
import dev.andikune.masjidio.ui.theme.TextPrimary
import dev.andikune.masjidio.ui.theme.TextSecondary
import dev.andikune.masjidio.ui.theme.UrgentRed
import kotlinx.coroutines.delay

/**
 * UpdateDialog — V1.04.430
 * Fix D-pad + Compact HP
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
    onSkipClick: () -> Unit,
    onTidakClick: () -> Unit = onSkipClick
) {
    val isLocked = isDownloading || isInstalling

    val updateFocusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        delay(500L)
        runCatching { updateFocusRequester.requestFocus() }
        delay(300L)
        runCatching { updateFocusRequester.requestFocus() }
    }

    Dialog(
        onDismissRequest = {
            if (!forceUpdate && !isLocked) onLaterClick()
        },
        properties = DialogProperties(
            dismissOnBackPress = !forceUpdate && !isLocked,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.94f)
                    .widthIn(max = 680.dp)
                    .fillMaxHeight(0.94f)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFF0B1720))
                    .border(2.dp, IslamicGold, RoundedCornerShape(18.dp))
                    .padding(horizontal = 18.dp, vertical = 16.dp)
                    .verticalScroll(rememberScrollState())
                    .focusGroup(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.SystemUpdate,
                    contentDescription = null,
                    tint = IslamicGold,
                    modifier = Modifier.size(40.dp)
                )

                Text(
                    text = "UPDATE TERSEDIA",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = IslamicGoldLight
                )

                Text(
                    text = "Versi baru MASJID.IO sudah tersedia",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )

                // INFO VERSI
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0x22000000), RoundedCornerShape(10.dp))
                        .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(10.dp))
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Versi Saat Ini", fontSize = 10.sp, color = TextSecondary)
                        Text(
                            currentVersion,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Versi Terbaru", fontSize = 10.sp, color = TextSecondary)
                        Text(
                            latestVersion,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = IslamicGreen
                        )
                    }
                }

                // CHANGELOG
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0x22000000), RoundedCornerShape(10.dp))
                        .border(1.dp, IslamicGold.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "📋 Yang Baru di Versi $latestVersion:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = IslamicGoldLight
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    val notesText = if (releaseNotes.isNullOrBlank()) {
                        "(Tidak ada catatan rilis)"
                    } else {
                        releaseNotes
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = notesText,
                            fontSize = 12.sp,
                            color = TextPrimary,
                            lineHeight = 18.sp
                        )
                    }
                }

                // PROGRESS BAR
                if (isDownloading && downloadProgress != null) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "⬇️ Mengunduh update... ${(downloadProgress * 100).toInt()}%",
                            fontSize = 11.sp,
                            color = IslamicGoldLight,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0x33FFFFFF))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(downloadProgress)
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(IslamicGold)
                            )
                        }
                    }
                }

                if (isInstalling) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(IslamicGreen.copy(alpha = 0.15f))
                            .border(1.dp, IslamicGreen.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "📦 Menginstall update...",
                            fontSize = 12.sp,
                            color = IslamicGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // TOMBOL
                if (forceUpdate) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            DialogButton(
                                icon = Icons.Default.Cancel,
                                text = "TIDAK",
                                backgroundColor = Color(0xFF142735),
                                textColor = UrgentRed,
                                enabled = !isLocked,
                                onClick = onTidakClick
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            DialogButton(
                                icon = Icons.Default.Download,
                                text = "UPDATE",
                                backgroundColor = IslamicGold,
                                textColor = Color(0xFF09141D),
                                enabled = !isLocked,
                                onClick = onUpdateClick,
                                focusRequester = updateFocusRequester
                            )
                        }
                    }

                    Text(
                        text = "⚠️ Update ini WAJIB. Aplikasi tidak dapat digunakan sebelum update selesai.",
                        fontSize = 10.sp,
                        color = UrgentRed,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            DialogButton(
                                icon = Icons.Default.SkipNext,
                                text = "SKIP",
                                backgroundColor = Color(0xFF142735),
                                textColor = UrgentRed,
                                enabled = !isLocked,
                                onClick = onSkipClick
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            DialogButton(
                                icon = Icons.Default.Schedule,
                                text = "NANTI",
                                backgroundColor = Color(0xFF142735),
                                textColor = TextPrimary,
                                enabled = !isLocked,
                                onClick = onLaterClick
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            DialogButton(
                                icon = Icons.Default.Download,
                                text = "UPDATE",
                                backgroundColor = IslamicGold,
                                textColor = Color(0xFF09141D),
                                enabled = !isLocked,
                                onClick = onUpdateClick,
                                focusRequester = updateFocusRequester
                            )
                        }
                    }

                    Text(
                        text = "Nanti: muncul lagi saat app dibuka.\nSkip: tidak muncul sampai versi lebih tinggi.",
                        fontSize = 10.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }
}
// ============================================================
// DIALOG BUTTON
// ============================================================
@Composable
private fun DialogButton(
    icon: ImageVector,
    text: String,
    backgroundColor: Color,
    textColor: Color,
    enabled: Boolean = true,
    onClick: () -> Unit,
    focusRequester: FocusRequester? = null
) {
    var isFocused by remember { mutableStateOf(false) }
    val bg = if (!enabled) backgroundColor.copy(alpha = 0.3f) else backgroundColor
    val borderColor = if (isFocused && enabled) IslamicGoldLight else Color.Transparent

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .border(
                if (isFocused && enabled) 3.dp else 0.dp,
                borderColor,
                RoundedCornerShape(12.dp)
            )
            .let { base ->
                if (focusRequester != null) base.focusRequester(focusRequester) else base
            }
            .focusable(enabled)
            .onFocusChanged { isFocused = it.isFocused }
            .clickable(enabled) { onClick() }
            .padding(vertical = 10.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = textColor,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}
