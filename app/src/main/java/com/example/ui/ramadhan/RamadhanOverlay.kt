package com.example.ui.ramadhan

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppSettings
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.IslamicGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

/**
 * Overlay khusus Mode Ramadhan.
 *
 * Menampilkan:
 * - Countdown Imsak & Iftar (besar, di tengah)
 * - Jadwal Tarawih
 * - Jadwal Kultum
 * - Menu Sahur/Iftar (opsional)
 *
 * Dipanggil dari HomeScreen/MainActivity saat settings.ramadhanModeEnabled = true
 * dan waktu mendekati Imsak atau Iftar.
 */
@Composable
fun RamadhanOverlay(
    settings: AppSettings,
    currentTimeString: String,
    imsakTime: String,
    maghribTime: String,
    secondsToImsak: Long,
    secondsToMaghrib: Long,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Tentukan fase: mendekati Imsak atau mendekati Iftar
    val isIftarPhase = secondsToMaghrib in 1..3600
    val isImsakPhase = secondsToImsak in 1..3600

    // Pulse animation untuk countdown
    val infiniteTransition = rememberInfiniteTransition(label = "ramadhan_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ramadhan_pulse_scale"
    )

    // Gradient background
    val bgGradient = if (isIftarPhase) {
        listOf(Color(0xFF2B124C), Color(0xFF5A2E6F), Color(0xFF2B124C))
    } else {
        listOf(Color(0xFF0D1B2A), Color(0xFF1B3A5C), Color(0xFF0D1B2A))
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(bgGradient))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
        ) {
            // ============ HEADER ============
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Mosque,
                        contentDescription = null,
                        tint = IslamicGold,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(IslamicGold.copy(alpha = 0.2f))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "🌙 MODE RAMADHAN",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = IslamicGoldLight,
                            letterSpacing = 1.5.sp
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x33FFFFFF))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = currentTimeString,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 1.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0x33FFFFFF))
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ============ KONTEN UTAMA ============
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // ===== KOLOM KIRI: COUNTDOWN =====
                Column(
                    modifier = Modifier.weight(0.65f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Label fase
                    Text(
                        text = when {
                            isIftarPhase -> "MENJELANG BERBUKA PUASA"
                            isImsakPhase -> "MENJELANG IMSAK"
                            else -> "WAKTU RAMADHAN"
                        },
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isIftarPhase) IslamicGold else Color(0xFF90CAF9),
                        letterSpacing = 3.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Angka countdown besar
                    val targetSeconds = if (isIftarPhase) secondsToMaghrib else secondsToImsak
                    val hours = targetSeconds / 3600
                    val minutes = (targetSeconds % 3600) / 60
                    val seconds = targetSeconds % 60
                    val countdownText = String.format("%02d:%02d:%02d", hours, minutes, seconds)

                    Box(
                        modifier = Modifier
                            .scale(pulseScale)
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                if (isIftarPhase) Color(0x44FFD700) else Color(0x4490CAF9)
                            )
                            .border(
                                width = 4.dp,
                                color = if (isIftarPhase) IslamicGold else Color(0xFF90CAF9),
                                shape = RoundedCornerShape(24.dp)
                            )
                            .padding(horizontal = 48.dp, vertical = 24.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (isIftarPhase) "⏱️ BERBUKA DALAM" else "⏱️ IMSAK DALAM",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isIftarPhase) IslamicGoldLight else Color(0xFFBBDEFB),
                                letterSpacing = 2.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = countdownText,
                                fontSize = 96.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                letterSpacing = 4.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Waktu Imsak & Iftar
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        if (settings.showImsakBesar) {
                            WaktuBesar(
                                label = "IMSAK",
                                time = imsakTime,
                                color = Color(0xFF90CAF9)
                            )
                        }
                        if (settings.showIftarBesar) {
                            WaktuBesar(
                                label = "MAGHRIB / IFTAR",
                                time = maghribTime,
                                color = IslamicGold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Doa Berbuka
                    if (isIftarPhase) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0x33000000))
                                .border(1.dp, IslamicGold.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                                .padding(16.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Doa Berbuka Puasa",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = IslamicGoldLight,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "ذَهَبَ الظَّمَأُ وَابْتَلَّتِ الْعُرُوقُ وَثَبَتَ الْأَجْرُ إِنْ شَاءَ اللَّهُ",
                                    fontSize = 20.sp,
                                    color = IslamicGoldLight,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 32.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "\"Dzahabazh-zhama'u wabtallatil-'uruqu wa tsabatal-ajru insya Allah\"",
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    textAlign = TextAlign.Center,
                                    fontStyle = FontStyle.Italic
                                )
                                Text(
                                    text = "Telah hilang rasa haus, urat-urat telah basah, dan pahala telah tetap, insya Allah",
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }
                }

                // ===== KOLOM KANAN: JADWAL =====
                Column(
                    modifier = Modifier.weight(0.35f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Jadwal Tarawih
                    if (settings.showTarawihSchedule) {
                        JadwalCard(
                            icon = Icons.Default.Mosque,
                            title = "SHOLAT TARAWIH",
                            time = settings.tarawihTime,
                            subtitle = settings.tarawihImam.ifBlank { "Imam: (belum diisi)" },
                            color = IslamicGreen
                        )
                    }

                    // Jadwal Kultum
                    if (settings.showKultumSchedule) {
                        JadwalCard(
                            icon = Icons.Default.Schedule,
                            title = "KULTUM RAMADHAN",
                            time = settings.kultumTime,
                            subtitle = buildString {
                                if (settings.kultumTitle.isNotBlank()) append(settings.kultumTitle)
                                if (settings.kultumUstadz.isNotBlank()) {
                                    if (isNotEmpty()) append(" • ")
                                    append(settings.kultumUstadz)
                                }
                                if (isEmpty()) append("(belum diisi)")
                            },
                            color = IslamicGold
                        )
                    }

                    // Menu Sahur/Iftar
                    if (settings.showMenuSahurIftar) {
                        MenuCard(
                            title = if (isIftarPhase) "🍽️ MENU BERBUKA" else "🍽️ MENU SAHUR",
                            menu = if (isIftarPhase) settings.menuIftarText else settings.menuSahurText
                        )
                    }

                    // Info tambahan
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x33000000))
                            .border(1.dp, IslamicGold.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Text(
                                text = "🌙 Ramadhan Mubarak",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = IslamicGoldLight
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Marhaban ya Ramadhan. Semoga ibadah kita diterima Allah SWT.",
                                fontSize = 11.sp,
                                color = TextPrimary,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ============ FOOTER ============
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Selamat menunaikan ibadah puasa — ${settings.mosqueName}",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    fontStyle = FontStyle.Italic
                )
            }
        }
    }
}

// ============================================================
// KOMPONEN: WAKTU BESAR
// ============================================================

@Composable
private fun WaktuBesar(
    label: String,
    time: String,
    color: Color
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0x33000000))
            .border(2.dp, color, RoundedCornerShape(16.dp))
            .padding(horizontal = 24.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            letterSpacing = 1.5.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = time,
            fontSize = 36.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White,
            letterSpacing = 2.sp
        )
    }
}

// ============================================================
// KOMPONEN: JADWAL CARD
// ============================================================

@Composable
private fun JadwalCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    time: String,
    subtitle: String,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x33000000))
            .border(1.5.dp, color.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(color.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(26.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = time,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = TextSecondary,
                lineHeight = 14.sp,
                maxLines = 2
            )
        }
    }
}

// ============================================================
// KOMPONEN: MENU CARD
// ============================================================

@Composable
private fun MenuCard(
    title: String,
    menu: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x33A5D6A7))
            .border(1.5.dp, IslamicGreen.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Restaurant,
                contentDescription = null,
                tint = IslamicGreen,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = IslamicGreen,
                letterSpacing = 1.sp
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = menu.ifBlank { "(Menu belum diisi)" },
            fontSize = 13.sp,
            color = TextPrimary,
            lineHeight = 18.sp
        )
    }
}
