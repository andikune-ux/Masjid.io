package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.DailyOfficerItem
import com.example.data.model.OfficerSchedule
import com.example.data.model.PrayerId
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.IslamicGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import java.time.LocalDate

@Composable
fun OfficerCarousel(
    officers: OfficerSchedule,
    weeklyOfficers: List<DailyOfficerItem> = emptyList(),
    officerPhotoUri: String? = null,
    nextPrayerId: PrayerId = PrayerId.MAGHRIB,
    modifier: Modifier = Modifier
) {
    var activeSlide by remember { mutableStateOf(0) }

    // Auto-slide every 10 seconds
    LaunchedEffect(Unit) {
        while (true) {
            delay(10000)
            activeSlide = (activeSlide + 1) % 3
        }
    }

    // Determine day of week: 0=Senin, ..., 6=Ahad
    val dayOfWeekIndex = (LocalDate.now().dayOfWeek.value - 1).coerceIn(0, 6)
    val todaySchedule = weeklyOfficers.getOrNull(dayOfWeekIndex)

    val prayerName = nextPrayerId.displayName

    // Prioritize weekly schedule if present, else fallback to standard schedule
    val imamName = when (nextPrayerId) {
        PrayerId.SUBUH -> todaySchedule?.imamSubuh?.ifBlank { null } ?: officers.imamSubuh
        PrayerId.SYURUQ, PrayerId.DZUHUR -> todaySchedule?.imamDzuhur?.ifBlank { null } ?: officers.imamDzuhur
        PrayerId.ASHAR -> todaySchedule?.imamAshar?.ifBlank { null } ?: officers.imamAshar
        PrayerId.MAGHRIB -> todaySchedule?.imamMaghrib?.ifBlank { null } ?: officers.imamMaghrib
        PrayerId.ISYA -> todaySchedule?.imamIsya?.ifBlank { null } ?: officers.imamIsya
    }

    val muadzinName = when (nextPrayerId) {
        PrayerId.SUBUH -> todaySchedule?.muadzinSubuh?.ifBlank { null } ?: officers.muadzinSubuh
        PrayerId.SYURUQ, PrayerId.DZUHUR -> todaySchedule?.muadzinDzuhur?.ifBlank { null } ?: officers.muadzinDzuhur
        PrayerId.ASHAR -> todaySchedule?.muadzinAshar?.ifBlank { null } ?: officers.muadzinAshar
        PrayerId.MAGHRIB -> todaySchedule?.muadzinMaghrib?.ifBlank { null } ?: officers.muadzinMaghrib
        PrayerId.ISYA -> todaySchedule?.muadzinIsya?.ifBlank { null } ?: officers.muadzinIsya
    }

    val khatibJumat = todaySchedule?.khatibJumat?.ifBlank { null } ?: officers.khatibJumat
    val temaJumat = todaySchedule?.temaJumat?.ifBlank { null } ?: officers.temaJumat
    val ustadzKajian = todaySchedule?.ustadzKajian?.ifBlank { null } ?: officers.ustadzKajian
    val temaKajian = todaySchedule?.temaKajian?.ifBlank { null } ?: officers.temaKajian
    val dayLabel = todaySchedule?.dayName ?: "Hari Ini"

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = activeSlide,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            label = "officer_slide"
        ) { slide ->
            when (slide) {
                0 -> {
                    // Slide 1: Imam & Muadzin for upcoming prayer
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OfficerCard(
                            roleTitle = "IMAM SHOLAT $prayerName ($dayLabel)".uppercase(),
                            officerName = imamName,
                            icon = Icons.Default.Person,
                            badgeColor = IslamicGold,
                            photoUri = officerPhotoUri,
                            modifier = Modifier.weight(1f)
                        )
                        OfficerCard(
                            roleTitle = "MUADZIN SHOLAT $prayerName".uppercase(),
                            officerName = muadzinName,
                            icon = Icons.Default.Campaign,
                            badgeColor = IslamicGreen,
                            photoUri = null,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                1 -> {
                    // Slide 2: Khutbah Jum'at & Kajian
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OfficerCard(
                            roleTitle = "KHATIB JUM'AT",
                            officerName = khatibJumat,
                            subtitle = "Tema: \"$temaJumat\"",
                            icon = Icons.Default.MenuBook,
                            badgeColor = IslamicGold,
                            photoUri = officerPhotoUri,
                            modifier = Modifier.weight(1f)
                        )
                        OfficerCard(
                            roleTitle = "KAJIAN RUTIN PEKANAN",
                            officerName = ustadzKajian,
                            subtitle = "Tema: $temaKajian",
                            icon = Icons.Default.EventNote,
                            badgeColor = Color(0xFF64B5F6),
                            photoUri = null,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                else -> {
                    // Slide 3: Info & Mutiara Nasihat
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OfficerCard(
                            roleTitle = "USTADZ & PEMBINA MASJID",
                            officerName = officers.ustadzKajian,
                            subtitle = "Kajian & Konsultasi Syariah Jamaah",
                            icon = Icons.Default.Person,
                            badgeColor = IslamicGoldLight,
                            photoUri = officerPhotoUri,
                            modifier = Modifier.weight(1f)
                        )
                        OfficerCard(
                            roleTitle = "LAYANAN ZISWAF & KAS",
                            officerName = "Sekretariat DKM",
                            subtitle = "Laporan infaq transparan & penyaluran mustahik.",
                            icon = Icons.Default.EventNote,
                            badgeColor = IslamicGreen,
                            photoUri = null,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OfficerCard(
    roleTitle: String,
    officerName: String,
    subtitle: String? = null,
    icon: ImageVector,
    badgeColor: Color,
    photoUri: String? = null,
    modifier: Modifier = Modifier
) {
    val cardShape = RoundedCornerShape(14.dp)

    Row(
        modifier = modifier
            .fillMaxHeight()
            .clip(cardShape)
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xBB0C1A24), Color(0x9908131C))
                )
            )
            .border(1.dp, Color(0x33FFD700), cardShape)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar circle / Ustadz Photo
        if (!photoUri.isNullOrBlank()) {
            AsyncImage(
                model = photoUri,
                contentDescription = officerName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .border(1.5.dp, badgeColor, CircleShape)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(badgeColor.copy(alpha = 0.2f))
                    .border(1.5.dp, badgeColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = roleTitle,
                    tint = badgeColor,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Titles and Names
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = roleTitle,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = badgeColor
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = officerName,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (!subtitle.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
