package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
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
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.time.LocalDate

// ============================================================
// DATA UNTUK SATU KARTU PETUGAS
// ============================================================
private data class OfficerCardData(
    val role: String,
    val name: String,
    val subtitle: String,
    val photoUri: String?,
    val icon: ImageVector
)

// ============================================================
// KOMPONEN UTAMA — signature SAMA dengan HomeScreen.kt
// ============================================================
@Composable
fun OfficerCarousel(
    officers: OfficerSchedule,
    weeklyOfficers: List<DailyOfficerItem>,
    officerPhotoUri: String?,
    nextPrayerId: PrayerId,
    modifier: Modifier = Modifier
) {
    val todayName = remember { todayIndonesianName() }
    val isFriday = remember { LocalDate.now().dayOfWeek.value == 5 }
    val todayOfficer = remember(weeklyOfficers, todayName) {
        weeklyOfficers.firstOrNull { it.dayName.equals(todayName, ignoreCase = true) }
    }

    val leftCard: OfficerCardData
    val rightCard: OfficerCardData

    if (isFriday) {
        // Hari Jum'at: tampilkan Khatib & Kajian
        leftCard = OfficerCardData(
            role = "KHATIB JUM'AT",
            name = officers.khatibJumat,
            subtitle = "Tema: ${officers.temaJumat}",
            photoUri = todayOfficer?.fotoKhatibJumat ?: officerPhotoUri,
            icon = Icons.Filled.EventNote
        )
        rightCard = OfficerCardData(
            role = "KAJIAN RUTIN PEKANAN",
            name = officers.ustadzKajian,
            subtitle = "Tema: ${officers.temaKajian}",
            photoUri = todayOfficer?.fotoUstadzKajian ?: officerPhotoUri,
            icon = Icons.AutoMirrored.Filled.MenuBook
        )
    } else {
        // Hari biasa: tampilkan Imam & Muadzin sholat berikutnya
        leftCard = OfficerCardData(
            role = "IMAM ${nextPrayerId.displayName.uppercase()}",
            name = imamName(todayOfficer, officers, nextPrayerId),
            subtitle = "Petugas hari ini: $todayName",
            photoUri = imamPhoto(todayOfficer, nextPrayerId) ?: officerPhotoUri,
            icon = Icons.Filled.Person
        )
        rightCard = OfficerCardData(
            role = "MUADZIN ${nextPrayerId.displayName.uppercase()}",
            name = muadzinName(todayOfficer, officers, nextPrayerId),
            subtitle = "Petugas hari ini: $todayName",
            photoUri = muadzinPhoto(todayOfficer, nextPrayerId) ?: officerPhotoUri,
            icon = Icons.Filled.Person
        )
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OfficerCard(
            data = leftCard,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        )
        OfficerCard(
            data = rightCard,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        )
    }
}

// ============================================================
// KARTU PETUGAS (satu kartu)
// ============================================================
@Composable
private fun OfficerCard(
    data: OfficerCardData,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }
    val borderColor by animateColorAsState(
        targetValue = if (isFocused) IslamicGoldLight else Color(0x55FFD700),
        label = "officer_border"
    )
    val shape = RoundedCornerShape(20.dp)

    Row(
        modifier = modifier
            .clip(shape)
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xCC0A1A2F), Color(0x99040E1A))
                )
            )
            .border(
                width = if (isFocused) 3.dp else 2.dp,
                color = borderColor,
                shape = shape
            )
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .padding(horizontal = 24.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OfficerAvatar(data = data)

        Spacer(modifier = Modifier.width(20.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = data.role.uppercase(),
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = IslamicGold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = data.name,
                fontSize = 26.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (data.subtitle.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = data.subtitle,
                    fontSize = 18.sp,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// ============================================================
// FOTO BULAT BESAR (dengan fallback ikon)
// ============================================================
@Composable
private fun OfficerAvatar(data: OfficerCardData) {
    val avatarSize = 120.dp

    Box(
        modifier = Modifier
            .size(avatarSize)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    listOf(
                        IslamicGold.copy(alpha = 0.28f),
                        Color(0x22FFFFFF)
                    )
                )
            )
            .border(3.dp, IslamicGold, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (!data.photoUri.isNullOrBlank()) {
            AsyncImage(
                model = data.photoUri,
                contentDescription = data.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(avatarSize)
                    .clip(CircleShape)
            )
        } else {
            Icon(
                imageVector = data.icon,
                contentDescription = data.role,
                tint = IslamicGoldLight,
                modifier = Modifier.size(64.dp)
            )
        }
    }
}

// ============================================================
// HELPER — nama hari & data petugas per waktu sholat
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

private fun imamName(
    item: DailyOfficerItem?,
    fallback: OfficerSchedule,
    id: PrayerId
): String = when (id) {
    PrayerId.SUBUH, PrayerId.SYURUQ -> item?.imamSubuh ?: fallback.imamSubuh
    PrayerId.DZUHUR -> item?.imamDzuhur ?: fallback.imamDzuhur
    PrayerId.ASHAR -> item?.imamAshar ?: fallback.imamAshar
    PrayerId.MAGHRIB -> item?.imamMaghrib ?: fallback.imamMaghrib
    PrayerId.ISYA -> item?.imamIsya ?: fallback.imamIsya
}

private fun muadzinName(
    item: DailyOfficerItem?,
    fallback: OfficerSchedule,
    id: PrayerId
): String = when (id) {
    PrayerId.SUBUH, PrayerId.SYURUQ -> item?.muadzinSubuh ?: fallback.muadzinSubuh
    PrayerId.DZUHUR -> item?.muadzinDzuhur ?: fallback.muadzinDzuhur
    PrayerId.ASHAR -> item?.muadzinAshar ?: fallback.muadzinAshar
    PrayerId.MAGHRIB -> item?.muadzinMaghrib ?: fallback.muadzinMaghrib
    PrayerId.ISYA -> item?.muadzinIsya ?: fallback.muadzinIsya
}

private fun imamPhoto(item: DailyOfficerItem?, id: PrayerId): String? = when (id) {
    PrayerId.SUBUH, PrayerId.SYURUQ -> item?.fotoImamSubuh
    PrayerId.DZUHUR -> item?.fotoImamDzuhur
    PrayerId.ASHAR -> item?.fotoImamAshar
    PrayerId.MAGHRIB -> item?.fotoImamMaghrib
    PrayerId.ISYA -> item?.fotoImamIsya
}

private fun muadzinPhoto(item: DailyOfficerItem?, id: PrayerId): String? = when (id) {
    PrayerId.SUBUH, PrayerId.SYURUQ -> item?.fotoMuadzinSubuh
    PrayerId.DZUHUR -> item?.fotoMuadzinDzuhur
    PrayerId.ASHAR -> item?.fotoMuadzinAshar
    PrayerId.MAGHRIB -> item?.fotoMuadzinMaghrib
    PrayerId.ISYA -> item?.fotoMuadzinIsya
}