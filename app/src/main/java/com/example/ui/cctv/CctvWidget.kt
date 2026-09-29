package com.example.ui.cctv

import android.annotation.SuppressLint
import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.model.AppSettings
import com.example.data.model.CctvPosition
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.TextSecondary

/**
 * Widget CCTV PiP (Picture in Picture) di sudut layar.
 *
 * Mendukung 3 mode URL:
 * 1. Snapshot JPG (auto-refresh) — contoh: http://ip/cgi-bin/snapshot.cgi
 * 2. Stream HTTP MJPEG — contoh: http://ip:8080/video
 * 3. Web page embed — contoh: http://ip (dashboard DVR)
 *
 * Catatan: RTSP tidak didukung di WebView. Untuk RTSP, butuh ExoPlayer
 * dengan extension RTSP (bisa ditambahkan di update berikutnya).
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun CctvWidget(
    settings: AppSettings,
    modifier: Modifier = Modifier
) {
    if (!settings.cctvEnabled || settings.cctvUrl.isBlank()) {
        return
    }

    // Tentukan posisi di layar
    val alignment = when (settings.cctvPosition) {
        CctvPosition.TOP_LEFT -> Alignment.TopStart
        CctvPosition.TOP_RIGHT -> Alignment.TopEnd
        CctvPosition.BOTTOM_LEFT -> Alignment.BottomStart
        CctvPosition.BOTTOM_RIGHT -> Alignment.BottomEnd
    }

    // Ukuran widget: persen dari lebar layar (baseline 20% = ± 320dp)
    val sizePercent = settings.cctvSizePercent.coerceIn(10, 40)
    // Konversi: asumsi 1920dp lebar TV, 20% = 384dp
    val widgetWidth = (sizePercent * 19).dp   // 20% → 380dp
    val widgetHeight = widgetWidth * 9 / 16    // rasio 16:9

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        contentAlignment = alignment
    ) {
        Column(
            modifier = Modifier
                .width(widgetWidth)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xDD000000))
                .border(2.5.dp, IslamicGold, RoundedCornerShape(14.dp))
                .padding(6.dp)
        ) {
            // ===== HEADER =====
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon CCTV + indikator LIVE
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEF5350))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Default.Videocam,
                    contentDescription = null,
                    tint = IslamicGold,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "CCTV MASJID",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = IslamicGoldLight,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "LIVE",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFEF5350),
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // ===== VIDEO AREA =====
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(widgetHeight - 40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                CctvWebView(
                    url = settings.cctvUrl,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

// ============================================================
// WEBVIEW UNTUK CCTV
// ============================================================

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun CctvWebView(
    url: String,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    loadWithOverviewMode = true
                    useWideViewPort = true
                    builtInZoomControls = false
                    displayZoomControls = false
                    mediaPlaybackRequiresUserGesture = false
                    // Cache untuk stream
                    cacheMode = android.webkit.WebSettings.LOAD_NO_CACHE
                }

                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: android.webkit.WebResourceRequest?
                    ): Boolean {
                        return false
                    }
                }

                // Background hitam biar tidak putih saat loading
                setBackgroundColor(android.graphics.Color.BLACK)
            }
        },
        update = { webView ->
            try {
                val currentUrl = webView.url
                if (currentUrl == null || currentUrl != url) {
                    webView.loadUrl(url)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    )
}

// ============================================================
// HELPER: VALIDASI URL CCTV
// ============================================================

fun isValidCctvUrl(url: String): Boolean {
    return try {
        val uri = Uri.parse(url)
        val scheme = uri.scheme?.lowercase()
        scheme in listOf("http", "https")
    } catch (e: Exception) {
        false
    }
}
