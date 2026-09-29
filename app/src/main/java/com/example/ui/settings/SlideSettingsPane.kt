package com.example.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Slideshow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppSettings
import com.example.ui.components.TvSlider
import com.example.ui.components.TvToggle
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.IslamicGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SlideSettingsPane(
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
        // HEADER
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Slideshow,
                contentDescription = null,
                tint = IslamicGold,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "SLIDE FULLSCREEN",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = IslamicGoldLight
                )
                Text(
                    text = "Slide otomatis di antara waktu sholat",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }
        }

        // MASTER TOGGLE
        TvToggle(
            label = "Aktifkan Slide Otomatis",
            description = "Slide akan tampil bergantian saat tidak ada waktu sholat",
            isChecked = settings.slideEnabled,
            onToggle = { onUpdate(settings.copy(slideEnabled = it)) }
        )

        // PILIH SLIDE
        Text(
            text = "PILIH SLIDE YANG DITAMPILKAN",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGoldLight
        )

        TvToggle(
            label = "Slide QRIS Infaq",
            description = "Tampilkan QRIS + ajakan berinfaq",
            isChecked = settings.qrisSlideEnabled,
            onToggle = { onUpdate(settings.copy(qrisSlideEnabled = it)) },
            enabled = settings.slideEnabled
        )

        TvToggle(
            label = "Slide Laporan Keuangan",
            description = "Tampilkan laporan pemasukan & pengeluaran",
            isChecked = settings.laporanSlideEnabled,
            onToggle = { onUpdate(settings.copy(laporanSlideEnabled = it)) },
            enabled = settings.slideEnabled
        )

        TvToggle(
            label = "Slide Jadwal Kajian",
            description = "Tampilkan jadwal kajian rutin + foto ustadz",
            isChecked = settings.kajianSlideEnabled,
            onToggle = { onUpdate(settings.copy(kajianSlideEnabled = it)) },
            enabled = settings.slideEnabled
        )

        // INTERVAL
        if (settings.slideEnabled) {
            Text(
                text = "PENGATURAN ROTASI",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = IslamicGoldLight
            )

            TvSlider(
                label = "Interval Ganti Slide",
                value = settings.slideIntervalSeconds.toFloat(),
                onValueChange = { onUpdate(settings.copy(slideIntervalSeconds = it.toInt())) },
                valueRange = 5f..120f,
                steps = 22,
                unit = " detik"
            )

            TvToggle(
                label = "Hanya Tampil Saat Idle",
                description = "Slide muncul hanya saat tidak ada waktu sholat dekat",
                isChecked = settings.slideShowOnlyWhenIdle,
                onToggle = { onUpdate(settings.copy(slideShowOnlyWhenIdle = it)) }
            )
        }

        // DATA LAPORAN KEUANGAN
        if (settings.slideEnabled && settings.laporanSlideEnabled) {
            Text(
                text = "DATA LAPORAN KEUANGAN",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = IslamicGoldLight
            )

            Text(
                text = "Masukkan angka dalam Rupiah. Contoh: 5100000 untuk Rp 5.100.000",
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 16.sp
            )

            LaporanInputField(
                label = "Saldo Sebelumnya",
                value = settings.laporanKeuangan.saldoSebelumnya,
                onValueChange = {
                    onUpdate(settings.copy(laporanKeuangan = settings.laporanKeuangan.copy(saldoSebelumnya = it)))
                }
            )

            LaporanInputField(
                label = "Infaq Jum'at",
                value = settings.laporanKeuangan.pemasukanJumat,
                onValueChange = {
                    onUpdate(settings.copy(laporanKeuangan = settings.laporanKeuangan.copy(pemasukanJumat = it)))
                }
            )

            LaporanInputField(
                label = "Infaq Umum",
                value = settings.laporanKeuangan.pemasukanUmum,
                onValueChange = {
                    onUpdate(settings.copy(laporanKeuangan = settings.laporanKeuangan.copy(pemasukanUmum = it)))
                }
            )

            LaporanInputField(
                label = "Pengeluaran Dakwah",
                value = settings.laporanKeuangan.pengeluaranDakwah,
                onValueChange = {
                    onUpdate(settings.copy(laporanKeuangan = settings.laporanKeuangan.copy(pengeluaranDakwah = it)))
                }
            )

            LaporanInputField(
                label = "Pengeluaran Sosial",
                value = settings.laporanKeuangan.pengeluaranSosial,
                onValueChange = {
                    onUpdate(settings.copy(laporanKeuangan = settings.laporanKeuangan.copy(pengeluaranSosial = it)))
                }
            )

            LaporanInputField(
                label = "Pengeluaran Operasional",
                value = settings.laporanKeuangan.pengeluaranOperasional,
                onValueChange = {
                    onUpdate(settings.copy(laporanKeuangan = settings.laporanKeuangan.copy(pengeluaranOperasional = it)))
                }
            )

            TextInputField(
                label = "Periode Mulai",
                value = settings.laporanKeuangan.periodeMulai,
                placeholder = "Contoh: 11 September 2026",
                onValueChange = {
                    onUpdate(settings.copy(laporanKeuangan = settings.laporanKeuangan.copy(periodeMulai = it)))
                }
            )

            TextInputField(
                label = "Periode Selesai",
                value = settings.laporanKeuangan.periodeSelesai,
                placeholder = "Contoh: 17 September 2026",
                onValueChange = {
                    onUpdate(settings.copy(laporanKeuangan = settings.laporanKeuangan.copy(periodeSelesai = it)))
                }
            )
        }

        // INFO
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0x22FFD700), RoundedCornerShape(10.dp))
                .border(1.dp, IslamicGold.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                .padding(14.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = IslamicGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Info",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = IslamicGoldLight
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Slide akan otomatis muncul di HomeScreen ketika tidak ada waktu sholat dekat. " +
                            "Slide juga bisa di-dismiss dengan tombol X di pojok kanan atas.",
                    fontSize = 12.sp,
                    color = TextPrimary,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

// ============================================================
// KOMPONEN: INPUT ANGKA RUPIAH
// ============================================================

@Composable
private fun LaporanInputField(
    label: String,
    value: Long,
    onValueChange: (Long) -> Unit
) {
    var textValue by remember(value) { mutableStateOf(value.toString()) }
    var isFocused by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Text(
                text = "Rp ${formatRupiah(value)}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = IslamicGreen,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0x22000000))
                .border(
                    width = if (isFocused) 4.dp else 1.dp,
                    color = if (isFocused) Color(0xFFFFE44D) else Color(0x44FFFFFF),
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(14.dp)
        ) {
            BasicTextField(
                value = textValue,
                onValueChange = { newText ->
                    val filtered = newText.filter { it.isDigit() }
                    textValue = filtered
                    filtered.toLongOrNull()?.let { onValueChange(it) }
                },
                textStyle = TextStyle(
                    fontSize = 15.sp,
                    color = TextPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { isFocused = it.isFocused }
            )
        }
    }
}

// ============================================================
// KOMPONEN: INPUT TEKS
// ============================================================

@Composable
private fun TextInputField(
    label: String,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0x22000000))
                .border(
                    width = if (isFocused) 4.dp else 1.dp,
                    color = if (isFocused) Color(0xFFFFE44D) else Color(0x44FFFFFF),
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(14.dp)
        ) {
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    fontSize = 14.sp,
                    color = TextSecondary.copy(alpha = 0.5f)
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = TextStyle(
                    fontSize = 14.sp,
                    color = TextPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { isFocused = it.isFocused }
            )
        }
    }
}

// ============================================================
// HELPER: FORMAT RUPIAH
// ============================================================

private fun formatRupiah(amount: Long): String {
    return "%,d".format(amount).replace(',', '.')
}
