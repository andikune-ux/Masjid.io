package dev.andikune.masjidio.ui.settings

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import dev.andikune.masjidio.data.local.PrayerTimesCalculator
import dev.andikune.masjidio.data.model.AppSettings
import dev.andikune.masjidio.data.model.PrayerSchedule
import dev.andikune.masjidio.ui.components.TvToggle
import dev.andikune.masjidio.ui.theme.IslamicGold
import dev.andikune.masjidio.ui.theme.IslamicGoldLight
import dev.andikune.masjidio.ui.theme.IslamicGreen
import dev.andikune.masjidio.ui.theme.TextPrimary
import dev.andikune.masjidio.ui.theme.TextSecondary
import dev.andikune.masjidio.ui.theme.UrgentRed
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.time.LocalDate

@SuppressLint("MissingPermission")
@Composable
fun LocationSettingsPane(
    settings: AppSettings,
    onUpdate: (AppSettings) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    var countryText by remember { mutableStateOf(settings.country) }
    var provinceText by remember { mutableStateOf(settings.province) }
    var cityText by remember { mutableStateOf(settings.city) }
    var districtText by remember { mutableStateOf(settings.district) }
    var latText by remember { mutableStateOf(settings.latitude.toString()) }
    var lonText by remember { mutableStateOf(settings.longitude.toString()) }
    var isDetecting by remember { mutableStateOf(false) }
    var gpsStatus by remember { mutableStateOf<String?>(null) }
    var gpsError by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        val granted = perms[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                perms[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            detectLocation(
                fusedLocationClient = fusedLocationClient,
                onSuccess = { lat, lon ->
                    latText = String.format("%.4f", lat)
                    lonText = String.format("%.4f", lon)
                    onUpdate(settings.copy(latitude = lat, longitude = lon, isGpsEnabled = true))
                    gpsStatus = "✅ Lokasi terdeteksi: ${String.format("%.4f", lat)}, ${String.format("%.4f", lon)}"
                    gpsError = false
                    isDetecting = false
                },
                onError = { err ->
                    gpsStatus = "❌ $err"
                    gpsError = true
                    isDetecting = false
                }
            )
        } else {
            gpsStatus = "❌ Izin lokasi ditolak"
            gpsError = true
            isDetecting = false
        }
    }

    fun startDetection() {
        val hasFine = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (hasFine || hasCoarse) {
            isDetecting = true
            gpsStatus = "⏳ Mendeteksi lokasi..."
            gpsError = false
            detectLocation(
                fusedLocationClient = fusedLocationClient,
                onSuccess = { lat, lon ->
                    latText = String.format("%.4f", lat)
                    lonText = String.format("%.4f", lon)
                    onUpdate(settings.copy(latitude = lat, longitude = lon, isGpsEnabled = true))
                    gpsStatus = "✅ Lokasi terdeteksi: ${String.format("%.4f", lat)}, ${String.format("%.4f", lon)}"
                    gpsError = false
                    isDetecting = false
                },
                onError = { err ->
                    gpsStatus = "❌ $err"
                    gpsError = true
                    isDetecting = false
                }
            )
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    val previewSchedule: PrayerSchedule = remember(latText, lonText) {
        val lat = latText.toDoubleOrNull() ?: -6.1754
        val lon = lonText.toDoubleOrNull() ?: 106.8272
        PrayerTimesCalculator.calculate(
            date = LocalDate.now(),
            latitude = lat,
            longitude = lon
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // HEADER
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = IslamicGold,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "LOKASI & WAKTU SHOLAT",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = IslamicGoldLight
                )
                Text(
                    text = "Atur lokasi masjid untuk perhitungan jadwal sholat",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }
        }

        // MODE GPS
        TvToggle(
            label = "Deteksi Otomatis (GPS)",
            description = if (settings.isGpsEnabled)
                "Lokasi diambil otomatis dari GPS TV + internet"
            else
                "Input lokasi manual di bawah",
            isChecked = settings.isGpsEnabled,
            onToggle = { onUpdate(settings.copy(isGpsEnabled = it)) }
        )

        // TOMBOL DETEKSI GPS
        if (settings.isGpsEnabled) {
            ActionButton(
                icon = Icons.Default.MyLocation,
                title = if (isDetecting) "MENDETEKSI..." else "DETEKSI SEKARANG",
                description = "Ambil koordinat dari GPS TV + internet",
                backgroundColor = IslamicGold,
                textColor = Color(0xFF09141D),
                enabled = !isDetecting,
                onClick = { startDetection() }
            )

            if (gpsStatus != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0x33000000), RoundedCornerShape(8.dp))
                        .border(
                            1.dp,
                            if (gpsError) UrgentRed else IslamicGreen,
                            RoundedCornerShape(8.dp)
                        )
                        .padding(12.dp)
                ) {
                    Text(
                        text = gpsStatus ?: "",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (gpsError) UrgentRed else IslamicGreen
                    )
                }
            }
        }

        // INPUT MANUAL
        Text(
            text = "INPUT LOKASI MANUAL",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGoldLight
        )

        SettingsInput(
            label = "Negara",
            value = countryText,
            placeholder = "Contoh: Indonesia",
            onValueChange = {
                countryText = it
                onUpdate(settings.copy(country = it))
            }
        )

        SettingsInput(
            label = "Provinsi",
            value = provinceText,
            placeholder = "Contoh: DKI Jakarta",
            onValueChange = {
                provinceText = it
                onUpdate(settings.copy(province = it))
            }
        )

        SettingsInput(
            label = "Kota / Kabupaten",
            value = cityText,
            placeholder = "Contoh: Jakarta Pusat",
            onValueChange = {
                cityText = it
                onUpdate(settings.copy(city = it))
            }
        )

        SettingsInput(
            label = "Kecamatan",
            value = districtText,
            placeholder = "Contoh: Gambir",
            onValueChange = {
                districtText = it
                onUpdate(settings.copy(district = it))
            }
        )

        SettingsInput(
            label = "Latitude",
            value = latText,
            placeholder = "Contoh: -6.1754",
            onValueChange = {
                latText = it
                it.toDoubleOrNull()?.let { v -> onUpdate(settings.copy(latitude = v)) }
            }
        )

        SettingsInput(
            label = "Longitude",
            value = lonText,
            placeholder = "Contoh: 106.8272",
            onValueChange = {
                lonText = it
                it.toDoubleOrNull()?.let { v -> onUpdate(settings.copy(longitude = v)) }
            }
        )

        // METODE PERHITUNGAN
        Text(
            text = "METODE PERHITUNGAN",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGoldLight
        )

        val methods = listOf(
            "Kementerian Agama RI (Kemenag)",
            "Muslim World League",
            "ISNA (Amerika Utara)",
            "Umm Al-Qura (Arab Saudi)",
            "Egyptian General Authority"
        )

        for (method in methods) {
            val isSelected = settings.calculationMethod == method
            SelectionItem(
                title = method,
                isSelected = isSelected,
                onClick = { onUpdate(settings.copy(calculationMethod = method)) }
            )
        }

        // PREVIEW JADWAL SHOLAT
        Text(
            text = "PREVIEW JADWAL SHOLAT HARI INI",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGoldLight
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0x22000000))
                .border(1.dp, IslamicGold.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PreviewRow("Imsak", previewSchedule.imsak)
            PreviewRow("Subuh", previewSchedule.subuh)
            PreviewRow("Syuruq", previewSchedule.syuruq)
            PreviewRow("Dzuhur", previewSchedule.dzuhur)
            PreviewRow("Ashar", previewSchedule.ashar)
            PreviewRow("Maghrib", previewSchedule.maghrib)
            PreviewRow("Isya", previewSchedule.isya)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

// ============================================================
// FUNGSI DETEKSI LOKASI
// ============================================================

@SuppressLint("MissingPermission")
private fun detectLocation(
    fusedLocationClient: FusedLocationProviderClient,
    onSuccess: (Double, Double) -> Unit,
    onError: (String) -> Unit
) {
    try {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { loc: Location? ->
                if (loc != null) {
                    onSuccess(loc.latitude, loc.longitude)
                } else {
                    onError("Lokasi terakhir tidak tersedia. Coba lagi setelah 5 detik.")
                }
            }
            .addOnFailureListener { e ->
                onError("Gagal deteksi: ${e.message}")
            }
    } catch (e: SecurityException) {
        onError("Izin lokasi belum diberikan")
    }
}

// ============================================================
// KOMPONEN PENDUKUNG
// ============================================================

@Composable
private fun ActionButton(
    icon: ImageVector,
    title: String,
    description: String,
    backgroundColor: Color,
    textColor: Color,
    enabled: Boolean,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (enabled) backgroundColor else backgroundColor.copy(alpha = 0.4f))
            .border(
                if (isFocused && enabled) 4.dp else 0.dp,
                if (isFocused && enabled) Color(0xFFFFE44D) else Color.Transparent,
                RoundedCornerShape(12.dp)
            )
            .onFocusChanged { isFocused = it.isFocused }
            .focusable(enabled)
            .clickable(enabled) { onClick() }
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = textColor,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Text(
                text = description,
                fontSize = 12.sp,
                color = textColor.copy(alpha = 0.75f)
            )
        }
    }
}

@Composable
private fun SettingsInput(
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
                    if (isFocused) 4.dp else 1.dp,
                    if (isFocused) Color(0xFFFFE44D) else Color(0x44FFFFFF),
                    RoundedCornerShape(10.dp)
                )
                .padding(14.dp)
        ) {
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    fontSize = 15.sp,
                    color = TextSecondary.copy(alpha = 0.5f)
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
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

@Composable
private fun SelectionItem(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(
                when {
                    isSelected -> Color(0x33A5D6A7)
                    isFocused -> Color(0x33FFD700)
                    else -> Color(0x22000000)
                }
            )
            .border(
                if (isFocused) 4.dp else if (isSelected) 2.dp else 1.dp,
                when {
                    isFocused -> Color(0xFFFFE44D)
                    isSelected -> IslamicGreen
                    else -> Color(0x33FFFFFF)
                },
                RoundedCornerShape(10.dp)
            )
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(if (isSelected) IslamicGreen else Color(0x33FFFFFF)),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Text(
                    text = "✓",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) IslamicGreen else TextPrimary
        )
    }
}

@Composable
private fun PreviewRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = TextSecondary
        )
        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGoldLight
        )
    }
}
