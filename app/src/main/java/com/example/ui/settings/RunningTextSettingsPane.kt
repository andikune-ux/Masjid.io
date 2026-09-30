package com.example.ui.settings

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.TextFields
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
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun RunningTextSettingsPane(
    settings: AppSettings,
    onUpdate: (AppSettings) -> Unit,
    modifier: Modifier = Modifier
) {
    var textInput by remember { mutableStateOf(settings.runningText) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ===== HEADER =====
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.TextFields,
                contentDescription = null,
                tint = IslamicGold,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "RUNNING TEXT",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = IslamicGoldLight
                )
                Text(
                    text = "Atur teks berjalan di bawah layar utama",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }
        }

        // ===== TOGGLE AKTIF =====
        TvToggle(
            label = "Aktifkan Running Text",
            description = if (settings.runningText.isNotBlank())
                "Teks berjalan akan tampil di bagian bawah layar"
            else
                "Isi teks di bawah untuk mengaktifkan",
            isChecked = settings.runningText.isNotBlank(),
            onToggle = {
                if (!it) {
                    onUpdate(settings.copy(runningText = ""))
                    textInput = ""
                }
            }
        )

        // ===== INPUT TEKS =====
        Text(
            text = "ISI TEKS BERJALAN",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGoldLight
        )

        MultiLineInput(
            value = textInput,
            placeholder = "Contoh: Selamat datang di Masjid Al-Ikhlas ═══ Luruskan dan rapatkan shaf ═══ Harap nonaktifkan nada dering ponsel",
            onValueChange = {
                textInput = it
                onUpdate(settings.copy(runningText = it))
            }
        )

        // ===== KECEPATAN SCROLL =====
        Text(
            text = "PENGATURAN TAMPILAN",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGoldLight
        )

        TvSlider(
            label = "Kecepatan Scroll",
            value = settings.runningTextSpeed.toFloat(),
            onValueChange = { onUpdate(settings.copy(runningTextSpeed = it.toInt())) },
            valueRange = 1f..10f,
            steps = 8,
            formatter = { v ->
                when (v.toInt()) {
                    1 -> "Paling Lambat"
                    2 -> "Sangat Lambat"
                    3 -> "Lambat"
                    4 -> "Agak Lambat"
                    5 -> "Sedang"
                    6 -> "Normal"
                    7 -> "Agak Cepat"
                    8 -> "Cepat"
                    9 -> "Sangat Cepat"
                    else -> "Paling Cepat"
                }
            }
        )

        TvSlider(
            label = "Ukuran Huruf",
            value = settings.runningTextFontSize.toFloat(),
            onValueChange = { onUpdate(settings.copy(runningTextFontSize = it.toInt())) },
            valueRange = 14f..32f,
            steps = 17,
            unit = " sp"
        )

        // ===== PREVIEW =====
        Text(
            text = "PREVIEW",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGoldLight
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xCC000000))
                .border(1.dp, IslamicGold.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Text(
                text = if (textInput.isBlank()) "(teks kosong)" else textInput,
                fontSize = settings.runningTextFontSize.sp,
                color = Color.White,
                maxLines = 1,
                fontWeight = FontWeight.Normal
            )
        }

        // ===== INFO BOX =====
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
                        text = "Tips",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = IslamicGoldLight
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "• Gunakan pemisah ═══ untuk memisahkan antar pengumuman\n" +
                            "• Teks akan berjalan dari kanan ke kiri\n" +
                            "• Perubahan langsung tampil di HomeScreen setelah SIMPAN",
                    fontSize = 12.sp,
                    color = TextPrimary,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

// ============================================================
// KOMPONEN: INPUT MULTILINE
// ============================================================

@Composable
private fun MultiLineInput(
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    val borderWidth by animateDpAsState(
        targetValue = if (isFocused) 4.dp else 1.dp,
        animationSpec = tween(200),
        label = "input_border_width"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0x22000000))
            .border(
                width = borderWidth,
                color = if (isFocused) Color(0xFFFFE44D) else Color(0x44FFFFFF),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(14.dp)
    ) {
        if (value.isEmpty()) {
            Text(
                text = placeholder,
                fontSize = 14.sp,
                color = TextSecondary.copy(alpha = 0.5f),
                lineHeight = 20.sp
            )
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                fontSize = 14.sp,
                color = TextPrimary,
                lineHeight = 20.sp
            ),
            modifier = Modifier
                .fillMaxSize()
                .onFocusChanged { isFocused = it.isFocused }
        )
    }
}
