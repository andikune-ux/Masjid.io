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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.rtsp.RtspMediaSource
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.example.data.model.AppSettings
import com.example.data.model.CctvPosition
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight

/**
 * Widget CCTV PiP (Picture in Picture) di sudut layar.
 *
 * Support 4 mode:
 * 1. RTSP Stream — via ExoPlayer (rtsp://user:pass@ip:554/stream)
 * 2. Snapshot JPG — via WebView (http://ip/snapshot.jpg)
 * 3. MJPEG Stream — via WebView (http://ip:8080/video)
 * 4. Web DVR Dashboard — via WebView (http://ip)
 */
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

    // Ukuran widget: persen dari lebar layar
    val sizePercent = settings.cctvSizePercent.coerceIn(10, 40)
    val widgetWidth = (sizePercent * 19).dp
    val widgetHeight = widgetWidth * 9 / 16

    // Deteksi tipe URL
    val urlType = detectCctvType(settings.cctvUrl)

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
                    text = urlType.displayLabel,
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
                when (urlType) {
                    CctvType.RTSP -> CctvRtspPlayer(
                        url = settings.cctvUrl,
                        modifier = Modifier.fillMaxSize()
                    )
                    else -> CctvWebView(
                        url = settings.cctvUrl,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

// ============================================================
// TIPE URL CCTV
// ============================================================

enum class CctvType(val displayLabel: String) {
    RTSP("RTSP"),
    HTTP("LIVE"),
    UNKNOWN("N/A")
}

private fun detectCctvType(url: String): CctvType {
    return try {
        val uri = Uri.parse(url)
        when (uri.scheme?.lowercase()) {
            "rtsp", "rtsps" -> CctvType.RTSP
            "http", "https" -> CctvType.HTTP
            else -> CctvType.UNKNOWN
        }
    } catch (e: Exception) {
        CctvType.UNKNOWN
    }
}

// ============================================================
// RTSP PLAYER (via ExoPlayer)
// ============================================================

@Composable
private fun CctvRtspPlayer(
    url: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Buat ExoPlayer khusus RTSP
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            playWhenReady = true
            repeatMode = ExoPlayer.REPEAT_MODE_ALL
            volume = 0f
        }
    }

    // Load RTSP URL saat komposisi pertama
    DisposableEffect(url) {
        try {
            val mediaItem = MediaItem.fromUri(url)
            val rtspSource = RtspMediaSource.Factory()
                .setForceUseRtpTcp(true)  // Fallback ke TCP kalau UDP diblokir
                .setTimeoutMs(10_000)
                .createMediaSource(mediaItem)
            exoPlayer.setMediaSource(rtspSource)
            exoPlayer.prepare()
            exoPlayer.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        onDispose {
            try {
                exoPlayer.stop()
                exoPlayer.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            PlayerView(ctx).apply {
                useController = false
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                player = exoPlayer
                setBackgroundColor(android.graphics.Color.BLACK)
            }
        }
    )
}

// ============================================================
// WEBVIEW UNTUK HTTP (Snapshot / MJPEG / DVR Dashboard)
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
