package com.example.ui.settings

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
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Timer
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.IslamicWisdomStore
import com.example.data.model.AppSettings
import com.example.ui.components.TvSlider
import com.example.ui.components.TvToggle
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.IslamicGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun WisdomSettingsPane(
    settings: AppSettings,
    onUpdate: (AppSettings) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Pengaturan Kartu Ucapan & Mutiara Nasihat",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGoldLight
        )

        // ============================================================
        // ANIMASI SELECTOR
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Animation,
                    contentDescription = null,
                    tint = IslamicGold,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Efek Animasi Pergantian Kartu:",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            val animations = listOf(
                Pair("Fade", "Pudar Halus (Fade In / Fade Out — Elegan & Tenang)"),
                Pair("Slide", "Geser Horizontal (Slide In / Slide Out — Modern)"),
                Pair("Scale", "Skala Zoom (Scale In / Out — Dinamis)")
            )

            for ((animKey, animDesc) in animations) {
                val isSelected = settings.wisdomCardAnimation == animKey
                AnimationOptionItem(
                    title = animKey,
                    description = animDesc,
                    isSelected = isSelected,
                    onClick = { onUpdate(settings.copy(wisdomCardAnimation = animKey)) }
                )
            }
        }

        // ============================================================
        // DURASI SLIDER
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        tint = IslamicGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Durasi Tampil Setiap Kartu",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }

            TvSlider(
                label = "Durasi",
                value = settings.wisdomCardIntervalSeconds.toFloat(),
                onValueChange = { onUpdate(settings.copy(wisdomCardIntervalSeconds = it.toInt())) },
                valueRange = 4f..60f,
                steps = 55,
                unit = " Detik"
            )
        }

        // ============================================================
        // FILTER KATEGORI KARTU MUTIARA
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
                text = "Pilih Kategori Konten yang Ditampilkan:",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Aktifkan atau nonaktifkan jenis mutiara nasihat pada karusel layar utama.",
                fontSize = 12.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(4.dp))

            TvToggle(
                label = "Asmaul Husna",
                description = "Nama-nama agung Allah beserta arti dan fadhilahnya",
                isChecked = settings.showAsmaulHusna,
                onToggle = { onUpdate(settings.copy(showAsmaulHusna = it)) }
            )

            TvToggle(
                label = "Hadits Shahih Harian",
                description = "Hadits-hadits pilihan seputar sholat, adab, dan keutamaan ibadah",
                isChecked = settings.showHaditsHarian,
                onToggle = { onUpdate(settings.copy(showHaditsHarian = it)) }
            )

            TvToggle(
                label = "Ayat Al-Qur'an Pilihan",
                description = "Kutipan ayat suci Al-Qur'an penyejuk hati dan pengingat ketaatan",
                isChecked = settings.showAyatQuran,
                onToggle = { onUpdate(settings.copy(showAyatQuran = it)) }
            )

            TvToggle(
                label = "Doa Harian & Masjid",
                description = "Doa setelah adzan, doa masuk/keluar masjid, dan doa harian",
                isChecked = settings.showDoaHarian,
                onToggle = { onUpdate(settings.copy(showDoaHarian = it)) }
            )

            TvToggle(
                label = "Pengingat Sholat Sunnah",
                description = "Keutamaan sholat sunnah rawatib, dhuha, dan tahajjud",
                isChecked = settings.showSunnahReminder,
                onToggle = { onUpdate(settings.copy(showSunnahReminder = it)) }
            )
        }

        // ============================================================
        // PREVIEW KOLEKSI
        // ============================================================
        Text(
            text = "Daftar Koleksi Mutiara Nasihat (${IslamicWisdomStore.wisdomCards.size} Hadits & Ayat Tersedia):",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGoldLight
        )

        for (card in IslamicWisdomStore.wisdomCards.take(4)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF0C1B26))
                    .border(1.dp, Color(0x22FFD700), RoundedCornerShape(10.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${card.category}: ${card.title}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = IslamicGoldLight
                    )
                    Text(
                        text = card.source,
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "\"${card.translation}\"",
                    fontSize = 12.sp,
                    color = TextPrimary,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

// ============================================================
// KOMPONEN: ANIMATION OPTION (dengan fokus lebih tebal)
// ============================================================

@Composable
private fun AnimationOptionItem(
    title: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    val borderWidth by animateDpAsState(
        targetValue = if (isFocused) 4.dp else if (isSelected) 2.dp else 1.dp,
        animationSpec = tween(200),
        label = "anim_border_width"
    )

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.02f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 800f),
        label = "anim_scale"
    )

    val shadowElevation by animateDpAsState(
        targetValue = if (isFocused) 10.dp else 0.dp,
        animationSpec = tween(200),
        label = "anim_shadow"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .shadow(
                elevation = shadowElevation,
                shape = RoundedCornerShape(10.dp),
                ambientColor = Color(0x66FFD700),
                spotColor = Color(0x66FFD700)
            )
            .clip(RoundedCornerShape(10.dp))
            .background(
                when {
                    isSelected -> Color(0x33FFD700)
                    isFocused -> Color(0x22FFD700)
                    else -> Color(0x22000000)
                }
            )
            .border(
                width = borderWidth,
                color = when {
                    isFocused -> Color(0xFFFFE44D)
                    isSelected -> IslamicGold
                    else -> Color(0x22FFFFFF)
                },
                shape = RoundedCornerShape(10.dp)
            )
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        isSelected -> IslamicGoldLight
                        isFocused -> Color(0xFFFFE44D)
                        else -> TextPrimary
                    }
                )
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
                    contentDescription = null,
                    tint = IslamicGold,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
