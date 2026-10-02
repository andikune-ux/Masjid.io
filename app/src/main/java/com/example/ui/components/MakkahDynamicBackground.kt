package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.data.local.SunMoonCalculator
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

// ============================================================
// DATA CLASS
// ============================================================
private data class CloudPuff(
    val x: Float, val y: Float, val scale: Float,
    val speed: Float, val alpha: Float, val layer: Int
)
private data class Star(val x: Float, val y: Float, val size: Float, val twinklePhase: Float)
private data class RainDrop(val x: Float, val y: Float, val speed: Float, val length: Float, val alpha: Float)
private data class Bird(val y: Float, val size: Float, val speed: Float, val flapPhase: Float)
private data class Pilgrim(val x: Float, val isWoman: Boolean, val size: Float)

@Composable
fun MakkahDynamicBackground(weatherCondition: String = "Cerah", modifier: Modifier = Modifier) {
    var now by remember { mutableStateOf(LocalDateTime.now()) }
    LaunchedEffect(Unit) {
        while (true) { now = LocalDateTime.now(); delay(30_000L) }
    }

    val sunPos = remember(now) { SunMoonCalculator.getSunPosition(now, -21.42, 39.83) }
    val moonPos = remember(now) { SunMoonCalculator.getMoonPosition(now, -21.42, 39.83) }
    val moonPhase = remember(now) { SunMoonCalculator.getMoonPhase(now) }
    val skyColors = remember(now) { SunMoonCalculator.getSkyGradient(now) }
    val isNight = remember(now) { SunMoonCalculator.isNightTime(now) }
    val isGolden = remember(now) { SunMoonCalculator.isGoldenHour(now) }

    val isRainy = weatherCondition.contains("Hujan", true) || weatherCondition.contains("Gerimis", true)
    val isStormy = weatherCondition.contains("Petir", true)
    val isCloudy = weatherCondition.contains("Berawan", true) || weatherCondition.contains("Kabut", true)

    val t = rememberInfiniteTransition(label = "makkah")
    val cloudMove by t.animateFloat(0f, 1f,
        infiniteRepeatable(tween(240_000, easing = LinearEasing), RepeatMode.Restart), label = "cm")
    val birdMove by t.animateFloat(-0.1f, 1.1f,
        infiniteRepeatable(tween(60_000, easing = LinearEasing), RepeatMode.Restart), label = "bm")
    val wingFlap by t.animateFloat(-1f, 1f,
        infiniteRepeatable(tween(500, easing = LinearEasing), RepeatMode.Reverse), label = "wf")
    val twinkle by t.animateFloat(0.3f, 1f,
        infiniteRepeatable(tween(2200, easing = LinearEasing), RepeatMode.Reverse), label = "tw")
    val rainFall by t.animateFloat(0f, 1f,
        infiniteRepeatable(tween(650, easing = LinearEasing), RepeatMode.Restart), label = "rf")

    var lightning by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(isStormy) {
        if (!isStormy) { lightning = 0f; return@LaunchedEffect }
        while (true) {
            delay((12_000L..18_000L).random())
            lightning = 0.85f; delay(70)
            lightning = 0.15f; delay(90)
            lightning = 1f; delay(130)
            lightning = 0f
        }
    }

    // Partikel
    val clouds = remember {
        List(8) { i ->
            val layer = if (i < 4) 0 else 1
            CloudPuff(
                x = Random.nextFloat() * 1.3f,
                y = if (layer == 0) 0.05f + Random.nextFloat() * 0.15f
                    else 0.18f + Random.nextFloat() * 0.18f,
                scale = if (layer == 0) 0.6f + Random.nextFloat() * 0.5f
                        else 1.0f + Random.nextFloat() * 0.7f,
                speed = if (layer == 0) 0.4f + Random.nextFloat() * 0.2f
                        else 0.6f + Random.nextFloat() * 0.3f,
                alpha = if (layer == 0) 0.55f + Random.nextFloat() * 0.2f
                        else 0.75f + Random.nextFloat() * 0.2f,
                layer = layer
            )
        }
    }
    val stars = remember {
        List(70) { Star(Random.nextFloat(), Random.nextFloat() * 0.5f,
            0.6f + Random.nextFloat() * 2.2f, Random.nextFloat() * 6.28f) }
    }
    val rainDrops = remember {
        List(160) { RainDrop(Random.nextFloat(), Random.nextFloat(),
            0.5f + Random.nextFloat() * 0.6f, 22f + Random.nextFloat() * 40f,
            0.35f + Random.nextFloat() * 0.45f) }
    }
    val birds = remember {
        List(6) { i ->
            Bird(y = 0.10f + (i - 3) * 0.03f + Random.nextFloat() * 0.02f,
                size = 0.85f + Random.nextFloat() * 0.5f,
                speed = 0.85f + Random.nextFloat() * 0.25f,
                flapPhase = Random.nextFloat() * 6.28f)
        }
    }
    val pilgrims = remember {
        List(40) {
            val x = Random.nextFloat()
            // Hindari tengah (Ka'bah) — hanya di kiri & kanan
            val safeX = if (x in 0.35f..0.65f) {
                if (Random.nextBoolean()) 0.15f + Random.nextFloat() * 0.18f
                else 0.67f + Random.nextFloat() * 0.18f
            } else x
            Pilgrim(x = safeX, isWoman = Random.nextFloat() < 0.4f,
                size = 0.7f + Random.nextFloat() * 0.4f)
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // 1. LANGIT
        drawRect(brush = Brush.verticalGradient(skyColors, 0f, h * 0.68f), size = Size(w, h))

        // 2. BINTANG (malam cerah)
        if (isNight && !isRainy && !isStormy) {
            stars.forEach { s ->
                val tw = 0.3f + (sin(s.twinklePhase + twinkle * 3f) + 1f) * 0.35f
                drawCircle(Color.White.copy(alpha = tw * twinkle), s.size * density,
                    Offset(s.x * w, s.y * h))
            }
        }

        // 3. BULAN
        if (moonPos.isVisible && isNight) {
            drawMoonRealistic(
                Offset((moonPos.azimuth / 360f) * w, h * (0.62f - moonPos.elevation / 90f * 0.5f)),
                moonPhase, 24f * density
            )
        }

        // 4. MATAHARI
        if (sunPos.isVisible) {
            drawSunRealistic(
                Offset((sunPos.azimuth / 360f) * w, h * (0.62f - sunPos.elevation / 90f * 0.5f)),
                32f * density * sunPos.sizeScale, isGolden
            )
        }

        // 5. AWAN REALISTIS (2 layer untuk depth)
        // Layer belakang dulu
        clouds.filter { it.layer == 0 }.forEach { c ->
            val cx = ((c.x + cloudMove * c.speed) % 1.5f - 0.25f) * w
            drawRealisticCloud(Offset(cx, c.y * h), c.scale * density, c.alpha * 0.8f)
        }

        // 6. BURUNG (siang, tidak hujan/badai)
        if (!isNight && !isRainy && !isStormy) {
            birds.forEachIndexed { idx, b ->
                val bx = (birdMove * b.speed * w) + (idx * 50f * density)
                val by = b.y * h + sin(birdMove * PI * 4).toFloat() * 18f * density
                drawBird(Offset(bx, by), b.size * density, wingFlap, 0.75f)
            }
        }

        // 7. KABUT SUBUH
        val hour = now.hour + now.minute / 60f
        if (hour in 4f..6f) drawFog(w, h, density)

        // 8. BANGUNAN MASJIDIL HARAM — ARCADE + MENARA + KA'BAH
        drawMasjidilHaram(w, h, isNight, isGolden)

        // 9. JAMAAH
        drawPilgrims(w, h, pilgrims, density, isNight)

        // 10. LANTAI MARMER + REFLEKSI
        drawMarbleFloor(w, h, sunPos.azimuth, isNight, isGolden)

        // 11. AWAN DEPAN (lapisan atas)
        clouds.filter { it.layer == 1 }.forEach { c ->
            val cx = ((c.x + cloudMove * c.speed) % 1.5f - 0.25f) * w
            drawRealisticCloud(Offset(cx, c.y * h), c.scale * density, c.alpha)
        }

        // 12. HUJAN
        if (isRainy) drawRain(rainDrops, rainFall, w, h)

        // 13. PETIR
        if (isStormy && lightning > 0.05f) {
            drawRect(Color.White.copy(alpha = lightning * 0.55f), size = Size(w, h))
        }

        // 14. OVERLAY MALAM
        if (isNight) drawRect(Color(0xFF000018).copy(alpha = 0.18f), size = Size(w, h))
    }
}

// ============================================================
// MATAHARI REALISTIS — inti + corona + sinar radial
// ============================================================
private fun DrawScope.drawSunRealistic(pos: Offset, r: Float, isGolden: Boolean) {
    val core = if (isGolden) Color(0xFFFFCC33) else Color(0xFFFFF59D)
    val glow = if (isGolden) Color(0xFFFFA726) else Color(0xFFFFF176)
    // 4 lapis corona
    drawCircle(glow.copy(alpha = 0.05f), r * 5f, pos)
    drawCircle(glow.copy(alpha = 0.10f), r * 3.2f, pos)
    drawCircle(glow.copy(alpha = 0.20f), r * 2f, pos)
    drawCircle(glow.copy(alpha = 0.35f), r * 1.4f, pos)
    // Sinar radial (12 sinar)
    for (i in 0 until 12) {
        val angle = (i * 30f + (System.currentTimeMillis() / 100) % 360).toDouble()
        val rad = Math.toRadians(angle)
        val sx = pos.x + cos(rad).toFloat() * r * 1.5f
        val sy = pos.y + sin(rad).toFloat() * r * 1.5f
        val ex = pos.x + cos(rad).toFloat() * r * 2.5f
        val ey = pos.y + sin(rad).toFloat() * r * 2.5f
        drawLine(glow.copy(alpha = 0.15f), Offset(sx, sy), Offset(ex, ey), strokeWidth = 2f)
    }
    // Inti + highlight
    drawCircle(core, r, pos)
    drawCircle(Color.White, r * 0.55f, pos)
}

// ============================================================
// BULAN REALISTIS — kawah + fase
// ============================================================
private fun DrawScope.drawMoonRealistic(pos: Offset, phase: SunMoonCalculator.MoonPhase, r: Float) {
    drawCircle(Color(0xFFB0BEC5).copy(alpha = 0.12f), r * 2.5f, pos)
    drawCircle(Color(0xFFCFD8DC).copy(alpha = 0.18f), r * 1.6f, pos)
    drawCircle(Color(0xFFEEF2F4), r, pos)
    // Kawah
    drawCircle(Color(0xFFB0BEC5).copy(alpha = 0.35f), r * 0.20f,
        Offset(pos.x - r * 0.30f, pos.y - r * 0.25f))
    drawCircle(Color(0xFFB0BEC5).copy(alpha = 0.28f), r * 0.14f,
        Offset(pos.x + r * 0.35f, pos.y + r * 0.15f))
    drawCircle(Color(0xFFB0BEC5).copy(alpha = 0.22f), r * 0.17f,
        Offset(pos.x - r * 0.10f, pos.y + r * 0.40f))
    drawCircle(Color(0xFFB0BEC5).copy(alpha = 0.18f), r * 0.10f,
        Offset(pos.x + r * 0.05f, pos.y - r * 0.45f))
    // Fase
    val pa = phase.phaseAngle
    val waxing = pa in 0f..180f
    when {
        phase.illumination < 0.08f -> drawCircle(Color(0xFF1A1A1A), r * 0.96f, pos)
        phase.illumination > 0.92f -> {}
        else -> {
            val off = if (waxing) -r * (2f * phase.illumination - 1f)
                      else r * (2f * phase.illumination - 1f)
            val path = Path().apply {
                addOval(Rect(pos.x - r + off, pos.y - r, pos.x + r + off, pos.y + r))
            }
            drawPath(path, Color(0xFF0A0A25).copy(alpha = 0.88f))
        }
    }
}

// ============================================================
// AWAN REALISTIS — banyak bulatan dengan shading top/bottom
// ============================================================
private fun DrawScope.drawRealisticCloud(center: Offset, scale: Float, alpha: Float) {
    val r = 42f * scale
    val topColor = Color.White.copy(alpha = alpha * 0.95f)
    val midColor = Color(0xFFF5F5F5).copy(alpha = alpha * 0.85f)
    val botColor = Color(0xFFB0BEC5).copy(alpha = alpha * 0.55f)

    // Bagian atas awan (5 bulatan terang)
    drawCircle(topColor, r * 1.0f, Offset(center.x - r * 0.9f, center.y - r * 0.15f))
    drawCircle(topColor, r * 1.3f, Offset(center.x - r * 0.15f, center.y - r * 0.35f))
    drawCircle(topColor, r * 1.15f, Offset(center.x + r * 0.7f, center.y - r * 0.20f))
    drawCircle(topColor, r * 0.85f, Offset(center.x - r * 1.6f, center.y))
    drawCircle(topColor, r * 0.80f, Offset(center.x + r * 1.5f, center.y + r * 0.05f))

    // Bagian tengah (bulatan sedang)
    drawCircle(midColor, r * 1.1f, Offset(center.x - r * 0.5f, center.y + r * 0.15f))
    drawCircle(midColor, r * 1.0f, Offset(center.x + r * 0.4f, center.y + r * 0.20f))

    // Bagian bawah (bulatan gelap — shadow)
    drawCircle(botColor, r * 0.9f, Offset(center.x - r * 0.7f, center.y + r * 0.55f))
    drawCircle(botColor, r * 1.0f, Offset(center.x + r * 0.1f, center.y + r * 0.60f))
    drawCircle(botColor, r * 0.85f, Offset(center.x + r * 0.9f, center.y + r * 0.55f))
    drawCircle(botColor, r * 0.7f, Offset(center.x - r * 1.3f, center.y + r * 0.50f))
}

// ============================================================
// BANGUNAN MASJIDIL HARAM — ARCADE + MENARA + KA'BAH
// ============================================================
private fun DrawScope.drawMasjidilHaram(w: Float, h: Float, isNight: Boolean, isGolden: Boolean) {
    val horizonY = h * 0.68f
    val floorY = h * 0.88f

    // ===== WARNA =====
    val arcadeLight = when {
        isNight -> Color(0xFF2A2E38)
        isGolden -> Color(0xFFD4B896)
        else -> Color(0xFFEDE3D0)
    }
    val arcadeShadow = when {
        isNight -> Color(0xFF1A1E28)
        isGolden -> Color(0xFFA88A60)
        else -> Color(0xFFC9BAA0)
    }
    val minaretColor = when {
        isNight -> Color(0xFF252830)
        isGolden -> Color(0xFFD8C0A0)
        else -> Color(0xFFF0E8D8)
    }

    // ===== ARCADE KIRI (0% - 35%) =====
    drawArcadeTier(0f, w * 0.35f, horizonY - h * 0.10f, horizonY + h * 0.02f,
        arcadeLight, arcadeShadow, isNight)
    drawArcadeTier(0f, w * 0.35f, horizonY - h * 0.18f, horizonY - h * 0.10f,
        arcadeLight, arcadeShadow, isNight, isUpper = true)

    // ===== ARCADE KANAN (65% - 100%) =====
    drawArcadeTier(w * 0.65f, w, horizonY - h * 0.10f, horizonY + h * 0.02f,
        arcadeLight, arcadeShadow, isNight)
    drawArcadeTier(w * 0.65f, w, horizonY - h * 0.18f, horizonY - h * 0.10f,
        arcadeLight, arcadeShadow, isNight, isUpper = true)

    // ===== MENARA (2 buah di belakang Ka'bah) =====
    drawMinaretRealistic(w * 0.32f, h * 0.20f, horizonY - h * 0.18f, minaretColor, isNight, density)
    drawMinaretRealistic(w * 0.68f, h * 0.20f, horizonY - h * 0.18f, minaretColor, isNight, density)

    // ===== KA'BAH (kubus hitam dengan pita emas) =====
    drawKaabahRealistic(w, h, horizonY, floorY, isNight, isGolden)
}

// ============================================================
// ARCADE — 1 tier lengkungan
// ============================================================
private fun DrawScope.drawArcadeTier(
    xStart: Float, xEnd: Float, yTop: Float, yBot: Float,
    lightColor: Color, shadowColor: Color, isNight: Boolean, isUpper: Boolean = false
) {
    val width = xEnd - xStart
    val height = yBot - yTop

    // Base
    drawRect(lightColor, Offset(xStart, yTop), Size(width, height))

    // Bayangan bawah
    drawRect(shadowColor.copy(alpha = 0.5f),
        Offset(xStart, yBot - height * 0.15f), Size(width, height * 0.15f))

    // Lengkungan berulang
    val archCount = if (isUpper) 8 else 10
    val archW = width / archCount
    val archH = height * 0.72f
    val archGap = archW * 0.15f
    val archInnerW = archW - archGap * 2f

    for (i in 0 until archCount) {
        val ax = xStart + i * archW + archGap
        val ay = yTop + height * 0.12f

        // Lubang lengkungan (gelap)
        val archDark = if (isNight) Color(0xFF050810) else Color(0xFF4A4038)
        drawRoundRect(
            color = archDark,
            topLeft = Offset(ax, ay),
            size = Size(archInnerW, archH),
            cornerRadius = CornerRadius(archInnerW / 2f, archInnerW / 2f)
        )

        // Lampu dalam lengkungan (malam)
        if (isNight) {
            drawCircle(Color(0xFFFFE082).copy(alpha = 0.75f),
                archInnerW * 0.15f, Offset(ax + archInnerW / 2f, ay + archH * 0.55f))
        }

        // Aksen emas di bagian atas lengkungan
        drawRoundRect(
            color = Color(0xFFD4AF37).copy(alpha = 0.55f),
            topLeft = Offset(ax, ay),
            size = Size(archInnerW, archH * 0.15f),
            cornerRadius = CornerRadius(archInnerW / 2f, archInnerW / 2f),
            style = Stroke(width = 2f)
        )
    }

    // Garis atas arcade
    drawLine(Color(0xFFD4AF37).copy(alpha = 0.4f),
        Offset(xStart, yTop), Offset(xEnd, yTop), strokeWidth = 2f)
}

// ============================================================
// MENARA — tinggi + kubah + bulan sabit
// ============================================================
private fun DrawScope.drawMinaretRealistic(
    cx: Float, topY: Float, botY: Float,
    color: Color, isNight: Boolean, density: Float
) {
    val width = 22f * density
    val halfW = width / 2f

    // Base menara
    drawRect(color, Offset(cx - halfW, topY + 40f * density),
        Size(width, botY - topY - 40f * density))

    // Balkon (2x)
    val balconyYs = listOf(topY + 80f * density, topY + 150f * density)
    balconyYs.forEach { by ->
        drawRect(color, Offset(cx - halfW * 1.6f, by), Size(width * 1.6f, 6f * density))
    }

    // Kubah atas (2 tier)
    drawRoundRect(
        color = color,
        topLeft = Offset(cx - halfW * 1.2f, topY + 25f * density),
        size = Size(width * 1.2f, 20f * density),
        cornerRadius = CornerRadius(width * 0.6f, width * 0.6f)
    )

    // Puncak kubah (bulat)
    drawCircle(color, halfW * 0.8f, Offset(cx, topY + 20f * density))

    // Batang bulan sabit
    drawLine(color, Offset(cx, topY + 8f * density),
        Offset(cx, topY + 20f * density), strokeWidth = 2f * density)

    // Bulan sabit kecil
    drawArc(
        color = Color(0xFFD4AF37).copy(alpha = 0.95f),
        startAngle = 30f, sweepAngle = 300f, useCenter = false,
        topLeft = Offset(cx - 5f * density, topY - 2f * density),
        size = Size(10f * density, 10f * density),
        style = Stroke(width = 2f * density)
    )

    // Lampu menara (malam)
    if (isNight) {
        drawCircle(Color(0xFFFFE082).copy(alpha = 0.8f),
            3f * density, Offset(cx, topY + 60f * density))
        drawCircle(Color(0xFFFFE082).copy(alpha = 0.7f),
            3f * density, Offset(cx, topY + 130f * density))
    }
}

// ============================================================
// KA'BAH — kubus hitam + pita emas Hizam bermotif + highlight
// ============================================================
private fun DrawScope.drawKaabahRealistic(
    w: Float, h: Float, horizonY: Float, floorY: Float,
    isNight: Boolean, isGolden: Boolean
) {
    // Posisi Ka'bah
    val kaabahW = w * 0.28f
    val kaabahH = h * 0.32f
    val kaabahX = (w - kaabahW) / 2f
    val kaabahY = floorY - kaabahH

    // Base Ka'bah (gradient hitam)
    val baseColor = when {
        isNight -> Color(0xFF050505)
        isGolden -> Color(0xFF1A1410)
        else -> Color(0xFF0A0A0A)
    }
    val highlightColor = when {
        isNight -> Color(0xFF1A1A1A)
        isGolden -> Color(0xFF3A2A1A)
        else -> Color(0xFF222222)
    }

    // Body Ka'bah
    drawRect(baseColor, Offset(kaabahX, kaabahY), Size(kaabahW, kaabahH))

    // Highlight kiri (kena matahari)
    drawRect(highlightColor.copy(alpha = 0.5f),
        Offset(kaabahX, kaabahY), Size(kaabahW * 0.15f, kaabahH))

    // Highlight atas
    drawRect(highlightColor.copy(alpha = 0.3f),
        Offset(kaabahX, kaabahY), Size(kaabahW, kaabahH * 0.08f))

    // ===== PITA EMAS HIZAM (di 42% dari atas) =====
    val hizamY = kaabahY + kaabahH * 0.42f
    val hizamH = kaabahH * 0.06f

    // Background pita emas
    drawRect(
        Brush.horizontalGradient(listOf(
            Color(0xFFB8860B), Color(0xFFFFD700),
            Color(0xFFFFF176), Color(0xFFFFD700), Color(0xFFB8860B)
        )),
        Offset(kaabahX, hizamY), Size(kaabahW, hizamH)
    )

    // Motif kaligrafi (bulatan kecil emas gelap berulang)
    val motifCount = 22
    val motifW = kaabahW / motifCount
    for (i in 0 until motifCount) {
        val mx = kaabahX + i * motifW + motifW / 2f
        // Bulatan motif
        drawCircle(Color(0xFF8B6914).copy(alpha = 0.7f),
            hizamH * 0.22f, Offset(mx, hizamY + hizamH / 2f))
        // Aksen
        drawCircle(Color(0xFFFFF176).copy(alpha = 0.9f),
            hizamH * 0.08f, Offset(mx, hizamY + hizamH / 2f))
    }

    // Garis tepi pita emas (atas & bawah)
    drawLine(Color(0xFFFFD700), Offset(kaabahX, hizamY),
        Offset(kaabahX + kaabahW, hizamY), strokeWidth = 2f)
    drawLine(Color(0xFFFFD700), Offset(kaabahX, hizamY + hizamH),
        Offset(kaabahX + kaabahW, hizamY + hizamH), strokeWidth = 2f)

    // ===== PINTU KA'BAH =====
    val doorW = kaabahW * 0.10f
    val doorH = kaabahH * 0.22f
    val doorX = kaabahX + kaabahW * 0.45f
    val doorY = kaabahY + kaabahH * 0.68f
    drawRect(Color(0xFFB8860B), Offset(doorX, doorY), Size(doorW, doorH))
    drawRect(Color(0xFFFFD700), Offset(doorX, doorY),
        Size(doorW, doorH), style = Stroke(width = 1.5f))

    // ===== KILAU EMAS SAAT SIANG =====
    if (!isNight && isGolden) {
        drawRect(Color(0xFFFFF176).copy(alpha = 0.20f),
            Offset(kaabahX, hizamY - 2f), Size(kaabahW, hizamH + 4f))
    }

    // ===== BAYANGAN DI BAWAH KA'BAH =====
    drawRect(Color(0x44000000),
        Offset(kaabahX - 5f, floorY), Size(kaabahW + 10f, h * 0.02f))
}

// ============================================================
// JAMAAH — siluet kecil
// ============================================================
private fun DrawScope.drawPilgrims(
    w: Float, h: Float, pilgrims: List<Pilgrim>, density: Float, isNight: Boolean
) {
    val floorY = h * 0.88f

    pilgrims.forEach { p ->
        val px = p.x * w
        val py = floorY + (p.size - 0.7f) * 20f * density
        val bodyH = 12f * density * p.size
        val bodyW = 5f * density * p.size

        val color = when {
            isNight -> if (p.isWoman) Color(0xFF1A1A1A) else Color(0xFF404040)
            else -> if (p.isWoman) Color(0xFF0A0A0A) else Color(0xFFEEEEEE)
        }

        // Kepala
        drawCircle(color, bodyW * 0.6f, Offset(px, py - bodyH))
        // Badan
        drawRoundRect(
            color = color,
            topLeft = Offset(px - bodyW / 2f, py - bodyH + bodyW * 0.5f),
            size = Size(bodyW, bodyH - bodyW * 0.4f),
            cornerRadius = CornerRadius(bodyW / 2f, bodyW / 4f)
        )
    }
}

// ============================================================
// LANTAI MARMER + REFLEKSI
// ============================================================
private fun DrawScope.drawMarbleFloor(
    w: Float, h: Float, sunAzimuth: Float, isNight: Boolean, isGolden: Boolean
) {
    val floorY = h * 0.88f

    // Warna lantai
    val floorColors = when {
        isNight -> listOf(Color(0xFF1A1A22), Color(0xFF0A0A12))
        isGolden -> listOf(Color(0xFFE8D4B0), Color(0xFFC9B08A))
        else -> listOf(Color(0xFFF5EFE0), Color(0xFFE0D6C0))
    }

    drawRect(Brush.verticalGradient(floorColors, floorY, h),
        Offset(0f, floorY), Size(w, h - floorY))

    // Refleksi matahari (kalau siang)
    if (!isNight) {
        val sunX = (sunAzimuth / 360f) * w
        val reflWidth = w * 0.15f
        val reflAlpha = if (isGolden) 0.35f else 0.20f

        drawRect(
            Brush.verticalGradient(
                listOf(
                    Color(0xFFFFE082).copy(alpha = 0f),
                    Color(0xFFFFE082).copy(alpha = reflAlpha * 0.6f),
                    Color(0xFFFFE082).copy(alpha = reflAlpha)
                ),
                floorY, h
            ),
            Offset(sunX - reflWidth / 2f, floorY), Size(reflWidth, h - floorY)
        )

        // Refleksi Ka'bah (bayangan gelap)
        val kaabahReflW = w * 0.28f
        drawRect(
            Brush.verticalGradient(
                listOf(
                    Color(0x66000000),
                    Color(0x22000000),
                    Color.Transparent
                ),
                floorY, h
            ),
            Offset((w - kaabahReflW) / 2f, floorY), Size(kaabahReflW, (h - floorY) * 0.6f)
        )
    }

    // Garis keramik lantai (horizontal tipis)
    val lineCount = 4
    for (i in 1..lineCount) {
        val ly = floorY + (h - floorY) * i / (lineCount + 1)
        drawLine(
            Color(0x22000000), Offset(0f, ly), Offset(w, ly),
            strokeWidth = 1f
        )
    }

    // Garis keramik vertikal (perspektif)
    val vLineCount = 12
    for (i in 0..vLineCount) {
        val lx = w * i / vLineCount
        drawLine(
            Color(0x15000000),
            Offset(lx, floorY), Offset(lx + (lx - w / 2f) * 0.3f, h),
            strokeWidth = 1f
        )
    }
}

// ============================================================
// BURUNG
// ============================================================
private fun DrawScope.drawBird(pos: Offset, size: Float, flap: Float, alpha: Float) {
    val span = 14f * size
    val off = flap * 6f * size
    val path = Path().apply {
        moveTo(pos.x - span, pos.y - off)
        quadraticTo(pos.x - span * 0.5f, pos.y - 2f, pos.x, pos.y)
        quadraticTo(pos.x + span * 0.5f, pos.y - 2f, pos.x + span, pos.y - off)
    }
    drawPath(path, Color(0xFF1A1A1A).copy(alpha = alpha), style = Stroke(width = 2.2f * size))
}

// ============================================================
// KABUT SUBUH
// ============================================================
private fun DrawScope.drawFog(w: Float, h: Float, density: Float) {
    val fogY = h * 0.68f
    drawRect(
        Brush.verticalGradient(
            listOf(
                Color.White.copy(alpha = 0f),
                Color.White.copy(alpha = 0.10f),
                Color.White.copy(alpha = 0.22f),
                Color.White.copy(alpha = 0.32f)
            ),
            fogY - 80f * density, h
        ),
        Offset(0f, fogY - 80f * density), Size(w, h - fogY + 80f * density)
    )
}

// ============================================================
// HUJAN
// ============================================================
private fun DrawScope.drawRain(drops: List<RainDrop>, progress: Float, w: Float, h: Float) {
    drops.forEach { d ->
        val cy = ((d.y + progress * d.speed) % 1.2f) * h
        val cx = d.x * w
        drawLine(
            Color(0xFF90CAF9).copy(alpha = d.alpha),
            Offset(cx, cy), Offset(cx - 8f, cy + d.length),
            strokeWidth = 2f
        )
    }
}
