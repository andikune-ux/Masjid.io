package com.example.ui.components

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
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.compose.AsyncImage

// ============================================================
// DATA MODEL
// ============================================================

data class OfficerInfo(
    val role: String = "",           // Contoh: "IMAM MAGHRIB", "KHATIB JUM'AT"
    val name: String = "",           // Contoh: "Ust. Ahmad Fauzi"
    val subtitle: String = "",       // Contoh: "Tema: Menjaga Ukhuwah"
    val photoUri: String? = null,    // URL atau content:// foto
    val badgeColor: Color = Color(0xFFD4AF37),  // warna emas default
    val iconType: OfficerIconType = OfficerIconType.PERSON
)

enum class OfficerIconType {
    PERSON,     // untuk imam/muadzin/ustadz
    BOOK,       // untuk kajian
    EVENT       // untuk khatib/event
}

// ============================================================
// KOMPONEN UTAMA
// ============================================================

@Composable
fun OfficerCarousel(
    officers: List<OfficerInfo>,
    modifier: Modifier = Modifier,
    onOfficerClick: (OfficerInfo) -> Unit = {}
) {
    if (officers.isEmpty()) return

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        officers.forEach { officer ->
            OfficerCard(
                officer = officer,
                modifier = Modifier.weight(1f),
                onClick = { onOfficerClick(officer) }
            )
        }
    }
}

// ============================================================
// KARTU PER OFFICER
// ============================================================

@Composable
private fun OfficerCard(
    officer: OfficerInfo,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val cardShape = RoundedCornerShape(20.dp)

    Card(
        onClick = onClick,
        shape = CardDefaults.shape(shape = cardShape),
        colors = CardDefaults.colors(
            containerColor = Color(0xCC0A1A2F),  // biru gelap semi-transparan
            focusedContainerColor = Color(0xE60A1A2F)
        ),
        border = CardDefaults.border(
            border = androidx.compose.foundation.BorderStroke(
                width = 2.dp,
                color = officer.badgeColor.copy(alpha = 0.6f)
            ),
            focusedBorder = androidx.compose.foundation.BorderStroke(
                width = 3.dp,
                color = officer.badgeColor
            )
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0x33FFFFFF),
                            Color(0x11FFFFFF),
                            Color.Transparent
                        )
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ---- FOTO / IKON (BESAR) ----
                OfficerAvatar(
                    officer = officer,
                    size = 120.dp
                )

                Spacer(modifier = Modifier.width(20.dp))

                // ---- TEKS INFO ----
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.Center
                ) {
                    // Role (contoh: "IMAM MAGHRIB")
                    Text(
                        text = officer.role.uppercase(),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = officer.badgeColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Nama (contoh: "Ust. Ahmad Fauzi")
                    Text(
                        text = officer.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Subtitle (contoh: "Tema: Menjaga Ukhuwah")
                    if (officer.subtitle.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = officer.subtitle,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.White.copy(alpha = 0.75f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

// ============================================================
// FOTO / IKON BULAT BESAR
// ============================================================

@Composable
private fun OfficerAvatar(
    officer: OfficerInfo,
    size: androidx.compose.ui.unit.Dp = 120.dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        officer.badgeColor.copy(alpha = 0.3f),
                        Color(0x33FFFFFF)
                    )
                )
            )
            .border(3.dp, officer.badgeColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (officer.photoUri != null) {
            AsyncImage(
                model = officer.photoUri,
                contentDescription = officer.name,
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )
        } else {
            // Placeholder ikon berdasarkan tipe
            Icon(
                imageVector = when (officer.iconType) {
                    OfficerIconType.PERSON -> Icons.Filled.Person
                    OfficerIconType.BOOK -> Icons.AutoMirrored.Filled.MenuBook
                    OfficerIconType.EVENT -> Icons.Filled.EventNote
                },
                contentDescription = officer.role,
                tint = officer.badgeColor,
                modifier = Modifier.size(60.dp)
            )
        }
    }
}

// ============================================================
// PREVIEW / CONTOH PEMAKAIAN
// ============================================================

@Composable
fun OfficerCarouselPreview() {
    OfficerCarousel(
        officers = listOf(
            OfficerInfo(
                role = "KHATIB JUM'AT",
                name = "Prof. Dr. KH. Zainuddin MZ",
                subtitle = "Tema: Menjaga Ukhuwah & Istiqomah",
                photoUri = null,
                badgeColor = Color(0xFFD4AF37),
                iconType = OfficerIconType.EVENT
            ),
            OfficerInfo(
                role = "KAJIAN RUTIN PEKANAN",
                name = "Ust. Hanan Attaki, Lc",
                subtitle = "Tema: Tafsir Ayat-Ayat Rahmat",
                photoUri = null,
                badgeColor = Color(0xFF64B5F6),
                iconType = OfficerIconType.BOOK
            )
        ),
        modifier = Modifier.fillMaxWidth()
    )
}