package com.example.ui.slides

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.AppSettings
import com.example.data.model.DailyOfficerItem
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.IslamicGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.time.LocalDate

/**
 * Slide Jadwal Kajian Rutin
 * Menampilkan:
 * - Header "JADWAL KAJIAN"
 * - Daftar kajian dari 7 hari (atau hari tertentu)
 * - Setiap item: foto ustadz, nama, tema, hari
 * - Bisa tampil semua kajian atau rotate
 */
@Composable
fun KajianSlide(
    settings: AppSettings,
    modifier: Modifier = Modifier
) {
    // Ambil semua kajian dari weeklyOfficers yang punya ustadz
    val kajianList = settings.weeklyOfficers
        .filter { it.ustadzKajian.isNotBlank() }
        .take(3) // Tampilkan maks 3 kajian

    // Hari ini
    val todayName = todayIndonesianName()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color(0xFF0D1B3A),
                        Color(0xFF1B2A4A),
                        Color(0xFF0D1B3A)
                    )
                )
            )
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ============ KIRI: HEADER ============
            Column(
                modifier = Modifier
                    .weight(0.32f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                // Icon kalender
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(IslamicGold.copy(alpha = 0.2f))
                        .border(2.dp, IslamicGold, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Event,
                        contentDescription = null,
                        tint = IslamicGold,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "JADWAL",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Light,
                    color = TextSecondary,
                    letterSpacing = 4.sp
                )
                Text(
                    text = "KAJIAN",
                    fontSize = 60.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = IslamicGoldLight,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "RUTIN",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = IslamicGreen,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Garis emas
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(3.dp)
                        .background(IslamicGold)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Ikuti kajian rutin mingguan di masjid kami.\nMenuntut ilmu adalah ibadah.",
                    fontSize = 14.sp,
                    color = TextPrimary,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Hari ini
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(IslamicGold.copy(alpha = 0.2f))
                        .border(1.dp, IslamicGold, RoundedCornerShape(10.dp))
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = IslamicGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Hari ini: $todayName",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = IslamicGoldLight
                    )
                }
            }

            // ============ KANAN: DAFTAR KAJIAN ============
            Column(
                modifier = Modifier
                    .weight(0.68f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (kajianList.isEmpty()) {
                    // Placeholder kalau belum ada data
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0x33000000))
                            .border(1.dp, IslamicGold.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = null,
                                tint = IslamicGold.copy(alpha = 0.6f),
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Belum ada jadwal kajian",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary
                            )
                            Text(
                                text = "Tambahkan ustadz pemateri di Pengaturan → Jadwal Petugas",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                textAlign = TextAlign.Center,
                                lineHeight = 16.sp
                            )
                        }
                    }
                } else {
                    kajianList.forEachIndexed { index, item ->
                        KajianCard(
                            dayName = item.dayName,
                            ustadzName = item.ustadzKajian,
                            theme = item.temaKajian,
                            photoUri = item.fotoUstadzKajian ?: settings.officerPhotoUri,
                            isToday = item.dayName.equals(todayName, ignoreCase = true),
                            index = index
                        )
                    }
                }
            }
        }
    }
}

// ============================================================
// KOMPONEN: KARTU KAJIAN
// ============================================================

@Composable
private fun KajianCard(
    dayName: String,
    ustadzName: String,
    theme: String,
    photoUri: String?,
    isToday: Boolean,
    index: Int
) {
    val accentColor = if (isToday) IslamicGold else IslamicGreen

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isToday) Color(0x44FFD700) else Color(0x33000000)
            )
            .border(
                width = if (isToday) 3.dp else 1.5.dp,
                color = accentColor,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ===== BADGE HARI =====
        Box(
            modifier = Modifier
                .size(width = 90.dp, height = 90.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(accentColor)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = dayName.uppercase(),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF09141D),
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.5.sp,
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // ===== FOTO USTADZ =====
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(IslamicGold.copy(alpha = 0.15f))
                .border(3.dp, accentColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (!photoUri.isNullOrBlank()) {
                AsyncImage(
                    model = photoUri,
                    contentDescription = ustadzName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(42.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // ===== INFO USTADZ =====
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Ustadz",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                if (isToday) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(IslamicGold)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "HARI INI",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF09141D)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = ustadzName,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = IslamicGoldLight,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.MenuBook,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Tema:",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = theme.ifBlank { "(belum diisi)" },
                fontSize = 14.sp,
                color = TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // ===== ICON JAM =====
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Ba'da",
                fontSize = 10.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Maghrib",
                fontSize = 10.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ============================================================
// HELPER: NAMA HARI INI
// ============================================================

private fun todayIndonesianName(): String =
    when (LocalDate.now().dayOfWeek.value) {
        1 -> "Senin"
        2 -> "Selasa"
        3 -> "Rabu"
        4 -> "Kamis"
        5 -> "Jum'at"
        6 -> "Sabtu"
        else -> "Ahad"
    }
    
