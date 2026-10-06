package dev.andikune.masjidio.ui.settings

import android.net.Uri
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
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Mosque
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import dev.andikune.masjidio.data.model.AppSettings
import dev.andikune.masjidio.ui.theme.IslamicGold
import dev.andikune.masjidio.ui.theme.IslamicGoldLight
import dev.andikune.masjidio.ui.theme.IslamicGreen
import dev.andikune.masjidio.ui.theme.TextPrimary
import dev.andikune.masjidio.ui.theme.TextSecondary
import dev.andikune.masjidio.ui.theme.UrgentRed
import dev.andikune.masjidio.util.MediaPersistenceHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun IdentitySettingsPane(
    settings: AppSettings,
    onUpdate: (AppSettings) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Helper: proses URI hasil pilih file
    fun processLogoUri(uri: Uri) {
        scope.launch {
            val localPath = withContext(Dispatchers.IO) {
                MediaPersistenceHelper.copyToPermanent(
                    context = context,
                    sourceUri = uri.toString(),
                    folder = MediaPersistenceHelper.FOLDER_LOGO,
                    fileNamePrefix = "logo"
                )
            }
            if (localPath != null) {
                settings.officerPhotoUri?.let { old ->
                    if (old != localPath) MediaPersistenceHelper.deleteFile(old)
                }
                onUpdate(settings.copy(officerPhotoUri = localPath))
            } else {
                onUpdate(settings.copy(officerPhotoUri = uri.toString()))
            }
        }
    }

    // TOMBOL 1: GALERI
    val logoGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) processLogoUri(uri)
    }

    // TOMBOL 2: FILE MANAGER
    val logoFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) processLogoUri(uri)
    }

    var mosqueNameText by remember { mutableStateOf(settings.mosqueName) }
    var addressText by remember { mutableStateOf(settings.mosqueAddress) }
    var takmirText by remember { mutableStateOf(settings.mosqueTakmir) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // HEADER
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Mosque,
                contentDescription = null,
                tint = IslamicGold,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "IDENTITAS MASJID",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = IslamicGoldLight
                )
                Text(
                    text = "Atur nama, alamat, dan logo masjid",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }
        }

        // LOGO MASJID
        Text(
            text = "LOGO MASJID",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGoldLight
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF091620))
                .border(1.dp, Color(0x33FFD700), RoundedCornerShape(14.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Unggah logo masjid untuk ditampilkan di layar utama. Format: PNG / JPG, resolusi minimal 512x512.",
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 16.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0x22000000))
                        .border(2.dp, IslamicGold, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (!settings.officerPhotoUri.isNullOrBlank()) {
                        AsyncImage(
                            model = settings.officerPhotoUri,
                            contentDescription = "Logo Masjid",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Mosque,
                                contentDescription = null,
                                tint = IslamicGold.copy(alpha = 0.6f),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Belum ada logo",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TvActionButton(
                            icon = Icons.Default.AddPhotoAlternate,
                            label = "GALERI",
                            backgroundColor = IslamicGold,
                            textColor = Color(0xFF09141D),
                            onClick = { logoGalleryLauncher.launch("image/*") }
                        )
                        TvActionButton(
                            icon = Icons.Default.FolderOpen,
                            label = "FILE",
                            backgroundColor = IslamicGold,
                            textColor = Color(0xFF09141D),
                            onClick = { logoFileLauncher.launch(arrayOf("image/*")) }
                        )
                    }

                    if (!settings.officerPhotoUri.isNullOrBlank()) {
                        TvActionButton(
                            icon = Icons.Default.Delete,
                            label = "HAPUS LOGO",
                            backgroundColor = Color.Transparent,
                            textColor = UrgentRed,
                            isOutlined = true,
                            onClick = {
                                settings.officerPhotoUri?.let { old ->
                                    MediaPersistenceHelper.deleteFile(old)
                                }
                                onUpdate(settings.copy(officerPhotoUri = null))
                            }
                        )
                    }

                    Text(
                        text = "💡 Kalau GALERI tidak bisa pilih di TV, coba FILE.",
                        fontSize = 10.sp,
                        color = TextSecondary.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // NAMA MASJID
        Text(
            text = "INFORMASI MASJID",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = IslamicGoldLight
        )

        SettingsTextField(
            label = "Nama Masjid",
            value = mosqueNameText,
            placeholder = "Contoh: MASJID AL-IKHLAS",
            onValueChange = {
                mosqueNameText = it
                onUpdate(settings.copy(mosqueName = it))
            }
        )

        SettingsTextField(
            label = "Alamat Lengkap",
            value = addressText,
            placeholder = "Contoh: Jl. Raya Madinah No. 7, Gambir, Jakarta Pusat",
            onValueChange = {
                addressText = it
                onUpdate(settings.copy(mosqueAddress = it))
            }
        )

        SettingsTextField(
            label = "Nama Takmir / Pengurus",
            value = takmirText,
            placeholder = "Contoh: H. Muhammad Syarif, S.E.",
            onValueChange = {
                takmirText = it
                onUpdate(settings.copy(mosqueTakmir = it))
            }
        )
    }
}
// ============================================================
// KOMPONEN PENDUKUNG
// ============================================================

@Composable
private fun TvActionButton(
    icon: ImageVector,
    label: String,
    backgroundColor: Color,
    textColor: Color,
    onClick: () -> Unit,
    isOutlined: Boolean = false
) {
    var isFocused by remember { mutableStateOf(false) }

    val borderWidth by animateDpAsState(
        targetValue = if (isFocused) 4.dp else if (isOutlined) 1.5.dp else 0.dp,
        animationSpec = tween(200),
        label = "btn_border_width"
    )

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.05f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 800f),
        label = "btn_scale"
    )

    Row(
        modifier = Modifier
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale
            )
            .clip(RoundedCornerShape(10.dp))
            .background(if (isOutlined) Color.Transparent else backgroundColor)
            .border(
                width = borderWidth,
                color = when {
                    isFocused -> Color(0xFFFFE44D)
                    isOutlined -> textColor.copy(alpha = 0.6f)
                    else -> Color.Transparent
                },
                shape = RoundedCornerShape(10.dp)
            )
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = textColor,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
private fun SettingsTextField(
    label: String,
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
                    width = borderWidth,
                    color = if (isFocused) Color(0xFFFFE44D) else Color(0x44FFFFFF),
                    shape = RoundedCornerShape(10.dp)
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
