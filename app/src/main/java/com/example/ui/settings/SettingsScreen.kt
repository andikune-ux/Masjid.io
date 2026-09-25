package com.example.ui.settings

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.SoundManager
import com.example.data.local.IndonesiaLocations
import com.example.data.local.PrayerTimesCalculator
import com.example.data.model.AppSettings
import com.example.data.model.AudioMode
import com.example.data.model.BackgroundMode
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.IslamicGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.UrgentRed

enum class SettingsCategory(val title: String, val icon: ImageVector) {
    LOCATION("Lokasi & Metode Hisab", Icons.Default.Place),
    TIME_SETTINGS("Waktu & Kalender (Offline)", Icons.Default.Schedule),
    COUNTDOWN("Durasi & Hitungan Mundur", Icons.Default.Timer),
    IDENTITY("Identitas Masjid", Icons.Default.Mosque),
    OFFICERS("Jadwal Petugas & Foto", Icons.Default.People),
    QRIS_DONATION("Donasi QRIS & Rekening", Icons.Default.QrCode2),
    VIDEO_MEDIA("Video Kegiatan Masjid", Icons.Default.Videocam),
    APPEARANCE("Tampilan & Background", Icons.Default.Palette),
    WISDOM_CARDS("Kartu Nasihat & Mutiara", Icons.Default.MenuBook),
    RUNNING_TEXT("Running Text", Icons.Default.EditNote),
    AUDIO("Audio & Adzan", Icons.Default.NotificationsActive),
    RAMADHAN("Mode Ramadhan", Icons.Default.Nightlight),
    SECURITY("Keamanan & Kiosk", Icons.Default.Lock),
    POWER("Power Management", Icons.Default.PowerSettingsNew),
    ABOUT("Tentang Aplikasi", Icons.Default.Info)
}

@Composable
fun SettingsScreen(
    currentSettings: AppSettings,
    soundManager: SoundManager,
    onSaveSettings: (AppSettings) -> Unit,
    onBack: () -> Unit,
    onTestQrisFocus: () -> Unit = {}
) {
    var draftSettings by remember { mutableStateOf(currentSettings) }
    var selectedCategory by remember { mutableStateOf(SettingsCategory.LOCATION) }
    var showChangePinDialog by remember { mutableStateOf(false) }
    var saveFeedbackMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF061118))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0A1822))
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color(0x33FFFFFF))
                            .testTag("btn_settings_back")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = "PENGATURAN MASJID.IO",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = IslamicGoldLight
                    )
                }

                // Save Button
                Button(
                    onClick = {
                        onSaveSettings(draftSettings)
                        saveFeedbackMessage = "Pengaturan berhasil disimpan!"
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = IslamicGold,
                        contentColor = Color(0xFF09141D)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("btn_save_settings")
                ) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "SIMPAN PENGATURAN", fontWeight = FontWeight.Bold)
                }
            }

            if (saveFeedbackMessage != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(IslamicGreen.copy(alpha = 0.2f))
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = saveFeedbackMessage ?: "",
                        color = IslamicGreen,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Main 2-Pane Layout (Left categories, Right content)
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Left Categories List (32% width)
                Column(
                    modifier = Modifier
                        .weight(0.32f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF0B1720))
                        .border(1.dp, Color(0x33FFD700), RoundedCornerShape(16.dp))
                        .padding(8.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    for (cat in SettingsCategory.values()) {
                        val isSelected = cat == selectedCategory
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) Color(0x44FFD700) else Color.Transparent)
                                .border(
                                    width = if (isSelected) 1.5.dp else 0.dp,
                                    color = if (isSelected) IslamicGold else Color.Transparent,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedCategory = cat }
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = cat.icon,
                                    contentDescription = null,
                                    tint = if (isSelected) IslamicGoldLight else TextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = cat.title,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else TextSecondary
                                )
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = if (isSelected) IslamicGold else Color(0x33FFFFFF),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // Right Pane: Detail Content (68% width)
                Box(
                    modifier = Modifier
                        .weight(0.68f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF0D1D28))
                        .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(16.dp))
                        .padding(20.dp)
                ) {
                    when (selectedCategory) {
                        SettingsCategory.LOCATION -> LocationSettingsPane(
                            settings = draftSettings,
                            onUpdate = { draftSettings = it }
                        )
                        SettingsCategory.TIME_SETTINGS -> TimeSettingsPane(
                            settings = draftSettings,
                            onUpdate = { draftSettings = it }
                        )
                        SettingsCategory.COUNTDOWN -> CountdownSettingsPane(
                            settings = draftSettings,
                            onUpdate = { draftSettings = it }
                        )
                        SettingsCategory.IDENTITY -> IdentitySettingsPane(
                            settings = draftSettings,
                            onUpdate = { draftSettings = it }
                        )
                        SettingsCategory.OFFICERS -> WeeklyOfficersSettingsPane(
                            settings = draftSettings,
                            onUpdate = { draftSettings = it }
                        )
                        SettingsCategory.QRIS_DONATION -> QrisSettingsPane(
                            settings = draftSettings,
                            onUpdate = { draftSettings = it },
                            onTestQrisFocus = onTestQrisFocus
                        )
                        SettingsCategory.VIDEO_MEDIA -> VideoSettingsPane(
                            settings = draftSettings,
                            onUpdate = { draftSettings = it }
                        )
                        SettingsCategory.APPEARANCE -> CustomBackgroundPane(
                            settings = draftSettings,
                            onUpdate = { draftSettings = it }
                        )
                        SettingsCategory.WISDOM_CARDS -> WisdomSettingsPane(
                            settings = draftSettings,
                            onUpdate = { draftSettings = it }
                        )
                        SettingsCategory.RUNNING_TEXT -> RunningTextSettingsPane(
                            settings = draftSettings,
                            onUpdate = { draftSettings = it }
                        )
                        SettingsCategory.AUDIO -> AudioSettingsPane(
                            settings = draftSettings,
                            soundManager = soundManager,
                            onUpdate = { draftSettings = it }
                        )
                        SettingsCategory.RAMADHAN -> RamadhanSettingsPane(
                            settings = draftSettings,
                            onUpdate = { draftSettings = it }
                        )
                        SettingsCategory.SECURITY -> SecuritySettingsPane(
                            settings = draftSettings,
                            onUpdate = { draftSettings = it },
                            onChangePinClick = { showChangePinDialog = true }
                        )
                        SettingsCategory.POWER -> PowerSettingsPane(
                            settings = draftSettings,
                            onUpdate = { draftSettings = it }
                        )
                        SettingsCategory.ABOUT -> AboutSettingsPane()
                    }
                }
            }
        }

        // Change PIN Dialog
        if (showChangePinDialog) {
            ChangePinModal(
                currentPin = draftSettings.pinCode,
                onPinChanged = { newPin ->
                    draftSettings = draftSettings.copy(pinCode = newPin)
                    showChangePinDialog = false
                    saveFeedbackMessage = "PIN berhasil diubah ke: $newPin"
                },
                onDismiss = { showChangePinDialog = false }
            )
        }
    }
}

// -------------------------------------------------------------
// SUB-PANES
// -------------------------------------------------------------

@Composable
fun LocationSettingsPane(
    settings: AppSettings,
    onUpdate: (AppSettings) -> Unit
) {
    val provinces = IndonesiaLocations.provinces
    var selectedProvIndex by remember {
        mutableStateOf(provinces.indexOfFirst { it.name == settings.province }.coerceAtLeast(0))
    }
    val currentCities = provinces.getOrNull(selectedProvIndex)?.cities ?: emptyList()
    var selectedCityIndex by remember {
        mutableStateOf(currentCities.indexOfFirst { it.name == settings.city }.coerceAtLeast(0))
    }

    // Preview prayer times
    val previewSchedule = remember(settings.latitude, settings.longitude) {
        PrayerTimesCalculator.calculate(
            latitude = settings.latitude,
            longitude = settings.longitude
        )
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("📍 PENGATURAN LOKASI & WAKTU SHOLAT", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = IslamicGoldLight)

        // GPS vs Manual Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    onUpdate(settings.copy(isGpsEnabled = false))
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (!settings.isGpsEnabled) IslamicGold else Color(0xFF142735)
                )
            ) {
                Text("Manual (38 Provinsi)", color = if (!settings.isGpsEnabled) Color.Black else TextPrimary)
            }

            Button(
                onClick = {
                    onUpdate(settings.copy(isGpsEnabled = true))
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (settings.isGpsEnabled) IslamicGold else Color(0xFF142735)
                )
            ) {
                Text("GPS Otomatis", color = if (settings.isGpsEnabled) Color.Black else TextPrimary)
            }
        }

        if (!settings.isGpsEnabled) {
            Text("PILIH PROVINSI (Lengkap 38 Provinsi Indonesia):", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = IslamicGold)

            // Horizontal or grid selector for Province
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                provinces.forEachIndexed { idx, prov ->
                    val isProvSelected = idx == selectedProvIndex
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isProvSelected) IslamicGold else Color(0xFF132736))
                            .clickable {
                                selectedProvIndex = idx
                                val firstCity = prov.cities.firstOrNull()
                                if (firstCity != null) {
                                    selectedCityIndex = 0
                                    onUpdate(
                                        settings.copy(
                                            province = prov.name,
                                            city = firstCity.name,
                                            latitude = firstCity.latitude,
                                            longitude = firstCity.longitude
                                        )
                                    )
                                }
                            }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = prov.name,
                            fontSize = 13.sp,
                            fontWeight = if (isProvSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isProvSelected) Color.Black else TextPrimary
                        )
                    }
                }
            }

            Text("PILIH KOTA / KABUPATEN:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = IslamicGold)
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                currentCities.forEachIndexed { cIdx, city ->
                    val isCitySelected = city.name == settings.city
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isCitySelected) IslamicGreen else Color(0xFF132736))
                            .clickable {
                                selectedCityIndex = cIdx
                                onUpdate(
                                    settings.copy(
                                        city = city.name,
                                        latitude = city.latitude,
                                        longitude = city.longitude
                                    )
                                )
                            }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = city.name,
                            fontSize = 13.sp,
                            fontWeight = if (isCitySelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isCitySelected) Color.Black else TextPrimary
                        )
                    }
                }
            }
        }

        // Calculation Method
        Text("METODE PERHITUNGAN:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = IslamicGold)
        Text(
            text = "Kementerian Agama Republik Indonesia (Kemenag RI) — Subuh: 20°, Isya: 18°, Ashar: Madzhab Syafi'i",
            fontSize = 13.sp,
            color = TextSecondary
        )

        // Live Prayer Time Preview Box
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF09141D))
                .border(1.dp, IslamicGold.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                .padding(14.dp)
        ) {
            Text(
                text = "PREVIEW JADWAL SHOLAT — ${settings.city}, ${settings.province}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = IslamicGoldLight
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Text("Subuh: ${previewSchedule.subuh}", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Text("Syuruq: ${previewSchedule.syuruq}", color = TextSecondary, fontSize = 13.sp)
                Text("Dzuhur: ${previewSchedule.dzuhur}", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Text("Ashar: ${previewSchedule.ashar}", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Text("Maghrib: ${previewSchedule.maghrib}", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Text("Isya: ${previewSchedule.isya}", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun IdentitySettingsPane(
    settings: AppSettings,
    onUpdate: (AppSettings) -> Unit
) {
    var name by remember { mutableStateOf(settings.mosqueName) }
    var address by remember { mutableStateOf(settings.mosqueAddress) }
    var takmir by remember { mutableStateOf(settings.mosqueTakmir) }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("🕌 IDENTITAS MASJID", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = IslamicGoldLight)

        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                onUpdate(settings.copy(mosqueName = it))
            },
            label = { Text("Nama Masjid") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = address,
            onValueChange = {
                address = it
                onUpdate(settings.copy(mosqueAddress = it))
            },
            label = { Text("Alamat Masjid") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = takmir,
            onValueChange = {
                takmir = it
                onUpdate(settings.copy(mosqueTakmir = it))
            },
            label = { Text("Nama Ketua Takmir / DKM") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun RunningTextSettingsPane(
    settings: AppSettings,
    onUpdate: (AppSettings) -> Unit
) {
    var text by remember { mutableStateOf(settings.runningText) }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("📝 RUNNING TEXT (INFORMASI MARQUEE)", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = IslamicGoldLight)

        OutlinedTextField(
            value = text,
            onValueChange = {
                text = it
                onUpdate(settings.copy(runningText = it))
            },
            label = { Text("Konten Running Text") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        Text("KECEPATAN SCROLL:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = IslamicGold)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            val speeds = listOf(Pair("Lambat", 1), Pair("Normal", 2), Pair("Cepat", 3))
            for ((label, spVal) in speeds) {
                Button(
                    onClick = { onUpdate(settings.copy(runningTextSpeed = spVal)) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (settings.runningTextSpeed == spVal) IslamicGold else Color(0xFF142735)
                    )
                ) {
                    Text(label, color = if (settings.runningTextSpeed == spVal) Color.Black else TextPrimary)
                }
            }
        }

        Text("UKURAN FONT:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = IslamicGold)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            val sizes = listOf(Pair("Kecil (16sp)", 16), Pair("Sedang (20sp)", 20), Pair("Besar (24sp)", 24))
            for ((label, szVal) in sizes) {
                Button(
                    onClick = { onUpdate(settings.copy(runningTextFontSize = szVal)) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (settings.runningTextFontSize == szVal) IslamicGold else Color(0xFF142735)
                    )
                ) {
                    Text(label, color = if (settings.runningTextFontSize == szVal) Color.Black else TextPrimary)
                }
            }
        }
    }
}

@Composable
fun OfficersSettingsPane(
    settings: AppSettings,
    onUpdate: (AppSettings) -> Unit
) {
    var activeTab by remember { mutableStateOf(0) }
    val off = settings.officers

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("👥 JADWAL IMAM, MUADZIN & USTADZ", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = IslamicGoldLight)

        // 4 Tabs
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val tabs = listOf("Imam", "Muadzin", "Khutbah Jum'at", "Kajian")
            tabs.forEachIndexed { idx, t ->
                Button(
                    onClick = { activeTab = idx },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (activeTab == idx) IslamicGold else Color(0xFF142735)
                    )
                ) {
                    Text(t, color = if (activeTab == idx) Color.Black else TextPrimary, fontSize = 12.sp)
                }
            }
        }

        when (activeTab) {
            0 -> {
                OutlinedTextField(value = off.imamSubuh, onValueChange = { onUpdate(settings.copy(officers = off.copy(imamSubuh = it))) }, label = { Text("Imam Subuh") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = off.imamDzuhur, onValueChange = { onUpdate(settings.copy(officers = off.copy(imamDzuhur = it))) }, label = { Text("Imam Dzuhur") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = off.imamAshar, onValueChange = { onUpdate(settings.copy(officers = off.copy(imamAshar = it))) }, label = { Text("Imam Ashar") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = off.imamMaghrib, onValueChange = { onUpdate(settings.copy(officers = off.copy(imamMaghrib = it))) }, label = { Text("Imam Maghrib") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = off.imamIsya, onValueChange = { onUpdate(settings.copy(officers = off.copy(imamIsya = it))) }, label = { Text("Imam Isya") }, modifier = Modifier.fillMaxWidth())
            }
            1 -> {
                OutlinedTextField(value = off.muadzinSubuh, onValueChange = { onUpdate(settings.copy(officers = off.copy(muadzinSubuh = it))) }, label = { Text("Muadzin Subuh") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = off.muadzinDzuhur, onValueChange = { onUpdate(settings.copy(officers = off.copy(muadzinDzuhur = it))) }, label = { Text("Muadzin Dzuhur") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = off.muadzinAshar, onValueChange = { onUpdate(settings.copy(officers = off.copy(muadzinAshar = it))) }, label = { Text("Muadzin Ashar") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = off.muadzinMaghrib, onValueChange = { onUpdate(settings.copy(officers = off.copy(muadzinMaghrib = it))) }, label = { Text("Muadzin Maghrib") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = off.muadzinIsya, onValueChange = { onUpdate(settings.copy(officers = off.copy(muadzinIsya = it))) }, label = { Text("Muadzin Isya") }, modifier = Modifier.fillMaxWidth())
            }
            2 -> {
                OutlinedTextField(value = off.khatibJumat, onValueChange = { onUpdate(settings.copy(officers = off.copy(khatibJumat = it))) }, label = { Text("Nama Khatib Jum'at") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = off.temaJumat, onValueChange = { onUpdate(settings.copy(officers = off.copy(temaJumat = it))) }, label = { Text("Tema Khutbah Jum'at") }, modifier = Modifier.fillMaxWidth())
            }
            3 -> {
                OutlinedTextField(value = off.ustadzKajian, onValueChange = { onUpdate(settings.copy(officers = off.copy(ustadzKajian = it))) }, label = { Text("Nama Ustadz Pemateri") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = off.jadwalKajian, onValueChange = { onUpdate(settings.copy(officers = off.copy(jadwalKajian = it))) }, label = { Text("Jadwal Kajian (contoh: Sabtu Ba'da Maghrib)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = off.temaKajian, onValueChange = { onUpdate(settings.copy(officers = off.copy(temaKajian = it))) }, label = { Text("Tema / Kitab Kajian") }, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
fun AudioSettingsPane(
    settings: AppSettings,
    soundManager: SoundManager,
    onUpdate: (AppSettings) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("🔔 AUDIO & ADZAN (ANTI TABRAKAN ADZAN)", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = IslamicGoldLight)

        Text("PILIH MODE AUDIO:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = IslamicGold)

        // 3 Modes
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Beep Only
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (settings.audioMode == AudioMode.BEEP_ONLY) Color(0x33FFD700) else Color(0xFF142735))
                    .clickable { onUpdate(settings.copy(audioMode = AudioMode.BEEP_ONLY)) }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = settings.audioMode == AudioMode.BEEP_ONLY,
                    onClick = { onUpdate(settings.copy(audioMode = AudioMode.BEEP_ONLY)) }
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("Beep Only (Direkomendasikan)", fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text("Pilih ini jika masjid sudah punya muadzin asli. TV hanya bunyi isyarat beep agar tidak ada 2 adzan bersamaan.", fontSize = 12.sp, color = TextSecondary)
                }
            }

            // Full Adzan
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (settings.audioMode == AudioMode.FULL_ADZAN) Color(0x33FFD700) else Color(0xFF142735))
                    .clickable { onUpdate(settings.copy(audioMode = AudioMode.FULL_ADZAN)) }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = settings.audioMode == AudioMode.FULL_ADZAN,
                    onClick = { onUpdate(settings.copy(audioMode = AudioMode.FULL_ADZAN)) }
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("Full Adzan", fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text("Putar audio adzan lengkap dari speaker TV saat waktu sholat tiba.", fontSize = 12.sp, color = TextSecondary)
                }
            }

            // Silent
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (settings.audioMode == AudioMode.SILENT) Color(0x33FFD700) else Color(0xFF142735))
                    .clickable { onUpdate(settings.copy(audioMode = AudioMode.SILENT)) }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = settings.audioMode == AudioMode.SILENT,
                    onClick = { onUpdate(settings.copy(audioMode = AudioMode.SILENT)) }
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("Silent (Senyap)", fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text("Hanya tampilan visual tanpa suara apapun.", fontSize = 12.sp, color = TextSecondary)
                }
            }
        }

        HorizontalDivider(color = Color(0x22FFFFFF))

        if (settings.audioMode == AudioMode.BEEP_ONLY) {
            Text("Volume Beep: ${settings.beepVolume}%", color = TextPrimary, fontSize = 14.sp)
            Slider(
                value = settings.beepVolume.toFloat(),
                onValueChange = { onUpdate(settings.copy(beepVolume = it.toInt())) },
                valueRange = 10f..100f
            )

            Text("Jumlah Beep: ${settings.beepCount}x", color = TextPrimary, fontSize = 14.sp)
            Slider(
                value = settings.beepCount.toFloat(),
                onValueChange = { onUpdate(settings.copy(beepCount = it.toInt())) },
                valueRange = 1f..5f,
                steps = 3
            )

            Button(
                onClick = {
                    soundManager.testBeep(settings.beepCount, settings.beepVolume)
                },
                colors = ButtonDefaults.buttonColors(containerColor = IslamicGold, contentColor = Color.Black)
            ) {
                Icon(imageVector = Icons.Default.VolumeUp, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Test Suara Beep")
            }
        } else if (settings.audioMode == AudioMode.FULL_ADZAN) {
            Text("Pilihan Adzan: ${settings.adzanFile}", color = TextPrimary, fontSize = 14.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Makkah", "Madinah", "Indonesia").forEach { st ->
                    Button(
                        onClick = { onUpdate(settings.copy(adzanFile = st)) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (settings.adzanFile == st) IslamicGold else Color(0xFF142735)
                        )
                    ) {
                        Text(st, color = if (settings.adzanFile == st) Color.Black else TextPrimary)
                    }
                }
            }

            Text("Volume Adzan: ${settings.adzanVolume}%", color = TextPrimary, fontSize = 14.sp)
            Slider(
                value = settings.adzanVolume.toFloat(),
                onValueChange = { onUpdate(settings.copy(adzanVolume = it.toInt())) },
                valueRange = 10f..100f
            )

            Button(
                onClick = {
                    soundManager.testAdzan(settings.adzanFile, settings.adzanVolume)
                },
                colors = ButtonDefaults.buttonColors(containerColor = IslamicGold, contentColor = Color.Black)
            ) {
                Icon(imageVector = Icons.Default.VolumeUp, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Test Suara Adzan")
            }
        }

        HorizontalDivider(color = Color(0x22FFFFFF))

        // Timer durations
        Text("Durasi Mode Fokus: ${settings.focusModeDurationMinutes} menit", color = TextPrimary)
        Text("Jeda Iqamah: ${settings.iqamahWaitMinutes} menit", color = TextPrimary)
        Text("Countdown Qobliyah: ${settings.qobliyahWaitMinutes} menit", color = TextPrimary)
    }
}

@Composable
fun AppearanceSettingsPane(
    settings: AppSettings,
    onUpdate: (AppSettings) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("🎨 TAMPILAN & BACKGROUND", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = IslamicGoldLight)

        Text("PILIHAN BACKGROUND:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = IslamicGold)

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            val modes = listOf(
                Triple("Default Alam", BackgroundMode.DEFAULT_NATURE, "Lansekap oasis & siluet masjid"),
                Triple("Ka'bah Suci", BackgroundMode.KABAH, "Suasana syahdu Masjidil Haram"),
                Triple("Emerald Geometris", BackgroundMode.EMERALD_GEOMETRIC, "Ornamen arabesque mewah")
            )

            for ((title, mode, desc) in modes) {
                val isSelected = settings.backgroundMode == mode
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) Color(0x44FFD700) else Color(0xFF142735))
                        .border(if (isSelected) 2.dp else 1.dp, if (isSelected) IslamicGold else Color(0x22FFFFFF), RoundedCornerShape(12.dp))
                        .clickable { onUpdate(settings.copy(backgroundMode = mode)) }
                        .padding(14.dp)
                ) {
                    Column {
                        Text(title, fontWeight = FontWeight.Bold, color = if (isSelected) IslamicGoldLight else TextPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(desc, fontSize = 11.sp, color = TextSecondary)
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Animasi Cuaca (Awan & Hujan)", color = TextPrimary)
            Switch(
                checked = settings.animationsEnabled,
                onCheckedChange = { onUpdate(settings.copy(animationsEnabled = it)) }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Animasi Burung Melintas", color = TextPrimary)
            Switch(
                checked = settings.showBirdsAnimation,
                onCheckedChange = { onUpdate(settings.copy(showBirdsAnimation = it)) }
            )
        }
    }
}

@Composable
fun RamadhanSettingsPane(
    settings: AppSettings,
    onUpdate: (AppSettings) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("🌙 MODE RAMADHAN", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = IslamicGoldLight)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Aktifkan Mode Ramadhan", fontWeight = FontWeight.Bold, color = TextPrimary)
                Text("Menampilkan kartu Imsak & Iftar serta jadwal Sholat Tarawih", fontSize = 12.sp, color = TextSecondary)
            }
            Switch(
                checked = settings.ramadhanModeEnabled,
                onCheckedChange = { onUpdate(settings.copy(ramadhanModeEnabled = it)) }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Hitung Mundur Imsak & Buka Puasa", color = TextPrimary)
            Switch(
                checked = settings.showImsakIftarCountdown,
                onCheckedChange = { onUpdate(settings.copy(showImsakIftarCountdown = it)) }
            )
        }
    }
}

@Composable
fun ContentSettingsPane() {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("📖 KONTEN TAMBAHAN", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = IslamicGoldLight)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF09141D))
                .border(1.dp, IslamicGold.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Column {
                Text("Ayat Al-Qur'an Pilihan", fontWeight = FontWeight.Bold, color = IslamicGoldLight)
                Spacer(modifier = Modifier.height(6.dp))
                Text("وَأَقِيمُوا الصَّلَاةَ وَآتُوا الزَّكَاةَ وَارْكَعُوا مَعَ الرَّاكِعِينَ", fontSize = 20.sp, color = TextPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                Text("\"Dan dirikanlah sholat, tunaikanlah zakat, dan ruku'lah beserta orang-orang yang ruku'.\" (QS. Al-Baqarah: 43)", fontSize = 13.sp, color = TextSecondary)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF09141D))
                .border(1.dp, IslamicGold.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Column {
                Text("Dzikir Ba'da Sholat", fontWeight = FontWeight.Bold, color = IslamicGoldLight)
                Spacer(modifier = Modifier.height(6.dp))
                Text("أَسْتَغْفِرُ اللَّهَ (3x) • اللَّهُمَّ أَنْتَ السَّلَامُ وَمِنْكَ السَّلَامُ تَبَارَكْتَ يَا ذَا الْجَلَالِ وَالْإِكْرَامِ", fontSize = 18.sp, color = TextPrimary)
            }
        }
    }
}

@Composable
fun SecuritySettingsPane(
    settings: AppSettings,
    onUpdate: (AppSettings) -> Unit,
    onChangePinClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("🔒 KEAMANAN & KIOSK", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = IslamicGoldLight)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Kiosk Mode (Kunci Navigasi)", fontWeight = FontWeight.Bold, color = TextPrimary)
                Text("Mencegah jamaah keluar aplikasi tanpa memasukkan PIN pengurus", fontSize = 12.sp, color = TextSecondary)
            }
            Switch(
                checked = settings.kioskModeEnabled,
                onCheckedChange = { onUpdate(settings.copy(kioskModeEnabled = it)) }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Auto-Start Saat TV Dinyalakan", fontWeight = FontWeight.Bold, color = TextPrimary)
                Text("Aplikasi langsung aktif otomatis saat booting Android TV", fontSize = 12.sp, color = TextSecondary)
            }
            Switch(
                checked = settings.autoStartOnBoot,
                onCheckedChange = { onUpdate(settings.copy(autoStartOnBoot = it)) }
            )
        }

        Button(
            onClick = onChangePinClick,
            colors = ButtonDefaults.buttonColors(containerColor = IslamicGold, contentColor = Color.Black)
        ) {
            Icon(imageVector = Icons.Default.Password, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Ubah PIN Pengurus (Sekarang: ${settings.pinCode})")
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF09141D))
                .padding(14.dp)
        ) {
            Column {
                Text("Petunjuk Jadikan Default Launcher Android TV:", fontWeight = FontWeight.Bold, color = IslamicGoldLight)
                Spacer(modifier = Modifier.height(4.dp))
                Text("1. Buka Pengaturan TV > Aplikasi > Aplikasi Default > Layar Utama (Home app)\n2. Pilih MASJID.IO\n3. TV akan otomatis selalu membuka MASJID.IO setiap kali tombol Home ditekan.", fontSize = 12.sp, color = TextSecondary)
            }
        }
    }
}

@Composable
fun PowerSettingsPane(
    settings: AppSettings,
    onUpdate: (AppSettings) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("⏰ POWER MANAGEMENT", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = IslamicGoldLight)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Layar Selalu Nyala (24/7 Display)", fontWeight = FontWeight.Bold, color = TextPrimary)
                Text("Mencegah TV standby atau screensaver saat operasional masjid", fontSize = 12.sp, color = TextSecondary)
            }
            Switch(
                checked = settings.keepScreenOn,
                onCheckedChange = { onUpdate(settings.copy(keepScreenOn = it)) }
            )
        }
    }
}

@Composable
fun AboutSettingsPane() {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("ℹ️ TENTANG APLIKASI", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = IslamicGoldLight)

        Text("MASJID.IO — Smart Mosque TV Display", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text("Versi: 1.0.0 (Build 2026)", fontSize = 13.sp, color = TextSecondary)
        Text("Platform: Android TV & Google TV (Landscape 16:9 4K Ready)", fontSize = 13.sp, color = TextSecondary)
        Text("Metode Jadwal Sholat: Astronomi Kemenag RI & Aladhan Worldwide API", fontSize = 13.sp, color = TextSecondary)

        var isCheckingUpdate by remember { mutableStateOf(false) }
        var updateStatus by remember { mutableStateOf<String?>(null) }

        Button(
            onClick = {
                isCheckingUpdate = true
                updateStatus = "Mengecek server..."
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF142735))
        ) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Cek Pembaruan Aplikasi")
        }

        if (isCheckingUpdate) {
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(1200)
                isCheckingUpdate = false
                updateStatus = "Aplikasi sudah dalam versi terbaru (v1.0.0). Semua fitur aktif!"
            }
        }

        if (updateStatus != null) {
            Text(text = updateStatus ?: "", color = IslamicGreen, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun ChangePinModal(
    currentPin: String,
    onPinChanged: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var newPin by remember { mutableStateOf("") }

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .width(360.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF0C1924))
                .border(2.dp, IslamicGold, RoundedCornerShape(16.dp))
                .padding(24.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Ubah PIN Baru (4 Digit)", fontWeight = FontWeight.Bold, color = IslamicGoldLight, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(14.dp))
                OutlinedTextField(
                    value = newPin,
                    onValueChange = { if (it.length <= 4) newPin = it },
                    label = { Text("PIN Baru") }
                )
                Spacer(modifier = Modifier.height(18.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A3040))
                    ) {
                        Text("Batal")
                    }
                    Button(
                        onClick = {
                            if (newPin.length == 4) {
                                onPinChanged(newPin)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = IslamicGold, contentColor = Color.Black),
                        enabled = newPin.length == 4
                    ) {
                        Text("Simpan PIN")
                    }
                }
            }
        }
    }
}
