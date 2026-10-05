package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.font.FontFamily
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
import kotlinx.coroutines.delay

/**
 * AutoOffDialog — Popup konfirmasi saat user tekan remote di jam OFF.
 *
 * V1.04.423 BARU.
 *
 * Logika:
 *   - Muncul saat user tekan remote (di jam OFF)
 *   - 2 tombol: YA (kiri) / TIDAK (kanan)
 *   - Default = TIDAK (auto-dismiss 120 detik → TIDAK)
 *   - YA → callback onConfirmTrue (matikan jadwal)
 *   - TIDAK / auto-dismiss → callback onConfirmFalse
 *
 * @param offStartTime Waktu mulai OFF (misal "19:32")
 * @param offEndTime Waktu selesai OFF (misal "04:05")
 * @param autoDismissSeconds Berapa detik sebelum auto-dismiss (default 120)
 */
@Composable
fun AutoOffDialog(
    offStartTime: String,
    offEndTime: String,
    autoDismissSeconds: Int = 120,
    onConfirmTrue: () -> Unit,
    onConfirmFalse: () -> Unit
) {
    // Countdown auto-dismiss
    var secondsRemaining by remember { mutableIntStateOf(autoDismissSeconds) }

    // Focus requesters
    val yesFocusRequester = remember { FocusRequester() }
    val noFocusRequester = remember { FocusRequester() }

    // Fokus awal ke tombol TIDAK (default)
    LaunchedEffect(Unit) {
        delay(300)
        runCatching { noFocusRequester.requestFocus() }
    }

    // Auto-dismiss timer
    LaunchedEffect(Unit) {
        while (secondsRemaining > 0) {
            delay(1000L)
            secondsRemaining -= 1
        }
        onConfirmFalse()
    }

    // Pulse animation untuk icon
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Dialog(
        onDismissRequest = { /* Wajib pilih tombol */ },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.65f)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF0B1720))
                .border(3.dp, IslamicGold, RoundedCornerShape(24.dp))
                .padding(28.dp)
                .onKeyEvent { event ->
                    if (event.type == KeyEventType.KeyDown) {
                        when (event.key) {
                            // BACK = TIDAK (default)
                            Key.Back, Key.Escape -> {
                                onConfirmFalse()
                                true
                            }
                            // ENTER / OK diarahkan ke tombol yang sedang fokus
                            else -> false
                        }
                    } else false
                }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ============================================================
                // ICON PULSE
                // ============================================================
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(Color(0x33FFD700))
                        .border(3.dp, IslamicGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🌙",
                        fontSize = 48.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // ============================================================
                // JUDUL
                // ============================================================
                Text(
                    text = "JADWAL OFF AKTIF",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = IslamicGoldLight,
                    letterSpacing = 2.sp,
                    textAlign = TextAlign.Center
                )

                // ============================================================
                // GARIS PEMISAH
                // ============================================================
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(2.dp)
                        .background(IslamicGold.copy(alpha = 0.5f))
                )

                Spacer(modifier = Modifier.height(2.dp))

                // ============================================================
                // INFO JAM OFF
                // ============================================================
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0x33FFFFFF))
                        .border(1.dp, IslamicGold.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = IslamicGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Layar redup sejak $offStartTime",
                        fontSize = 12.sp,
                        color = TextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // ============================================================
                // PERTANYAAN
                // ============================================================
                Text(
                    text = "Apakah Anda bersedia\nMEMATIKAN JADWAL ON/OFF?",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center,
                    lineHeight = 26.sp
                )

                Spacer(modifier = Modifier.height(2.dp))

                // ============================================================
                // WARNING BOX
                // ============================================================
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0x33FF5252))
                        .border(1.dp, UrgentRed.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = UrgentRed,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Kalau memilih YA, layar akan tetap nyala sampai TV dimatikan manual.",
                        fontSize = 11.sp,
                        color = Color(0xFFFFB0B0),
                        lineHeight = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // ============================================================
                // TOMBOL YA / TIDAK
                // ============================================================
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // ===== TOMBOL YA (KIRI) =====
                    DialogButton(
                        text = "YA",
                        icon = Icons.Default.Check,
                        backgroundColor = IslamicGreen,
                        textColor = Color.White,
                        modifier = Modifier.weight(1f),
                        focusRequester = yesFocusRequester,
                        onClick = onConfirmTrue
                    )

                    // ===== TOMBOL TIDAK (KANAN, DEFAULT) =====
                    DialogButton(
                        text = "TIDAK",
                        icon = Icons.Default.Close,
                        backgroundColor = Color(0xFF142735),
                        textColor = TextPrimary,
                        modifier = Modifier.weight(1f),
                        focusRequester = noFocusRequester,
                        onClick = onConfirmFalse
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // ============================================================
                // COUNTDOWN AUTO-DISMISS
                // ============================================================
                Text(
                    text = "Auto-dismiss dalam ${formatCountdown(secondsRemaining)} → dianggap TIDAK",
                    fontSize = 10.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

// ============================================================
// TOMBOL DIALOG
// ============================================================
@Composable
private fun DialogButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    backgroundColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester? = null,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(
                width = if (isFocused) 4.dp else 0.dp,
                color = if (isFocused) IslamicGoldLight else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .clickable { onClick() }
            .padding(vertical = 18.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = textColor,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            color = textColor,
            letterSpacing = 1.sp
        )
    }
}

// ============================================================
// FORMAT COUNTDOWN
// ============================================================
private fun formatCountdown(seconds: Int): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return if (minutes > 0) {
        String.format("%d:%02d", minutes, secs)
    } else {
        String.format("%d detik", secs)
    }
}
