package dev.andikune.masjidio.ui.components

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
import dev.andikune.masjidio.data.local.SunMoonCalculator
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class CloudPuff(
    val x: Float, val y: Float, val scale: Float,
    val speed: Float, val alpha: Float, val layer: Int
)
private data class Star(val x: Float, val y: Float, val size: Float, val twinklePhase: Float)
private data class RainDrop(val x: Float, val y: Float, val speed: Float, val length: Float, val alpha: Float)
private data class Bird(val y: Float, val size: Float, val speed: Float, val flapPhase: Float)
private data class Pilgrim(val x: Float, val isWoman: Boolean, val size: Float, val z: Float)

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
    val lampGlow by t.animateFloat(0.7f, 1f,
        infiniteRepeatable(tween(3000, easing = LinearEasing), RepeatMode.Reverse), label = "lg")

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

    val clouds = remember {
        List(10) { i ->
            val layer = if (i < 5) 0 else 1
            CloudPuff(
                x = Random.nextFloat() * 1.3f,
                y = if (layer == 0) 0.04f + Random.nextFloat() * 0.14f
                    else 0.16f + Random.nextFloat() * 0.18f,
                scale = if (layer == 0) 0.55f + Random.nextFloat() * 0.45f
                        else 0.95f + Random.nextFloat() * 0.65f,
                speed = if (layer == 0) 0.4f + Random.nextFloat() * 0.2f
                        else 0.6f + Random.nextFloat() * 0.3f,
                alpha = if (layer == 0) 0.55f + Random.nextFloat() * 0.2f
                        else 0.78f + Random.nextFloat() * 0.18f,
                layer = layer
            )
        }
    }
    val stars = remember {
        List(80) { Star(Random.nextFloat(), Random.nextFloat() * 0.5f,
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
        List(55) {
            val baseX = Random.nextFloat()
            val safeX = if (baseX in 0.34f..0.66f) {
                if (Random.nextBoolean()) 0.10f + Random.nextFloat() * 0.22f
                else 0.68f + Random.nextFloat() * 0.22f
            } else baseX
            Pilgrim(
                x = safeX,
                isWoman = Random.nextFloat() < 0.42f,
                size = 0.65f + Random.nextFloat() * 0.55f,
                z = Random.nextFloat()
            )
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val floorY = h * 0.86f

        drawRect(brush = Brush.verticalGradient(skyColors, 0f, floorY), size = Size(w, h))

        if (isNight && !isRainy && !isStormy) {
            stars.forEach { s ->
                val tw = 0.3f + (sin(s.twinklePhase + twinkle * 3f) + 1f) * 0.35f
                drawCircle(Color.White.copy(alpha = tw * twinkle), s.size * density,
                    Offset(s.x * w, s.y * h))
            }
        }

        if (moonPos.isVisible && isNight) {
            drawMoonRealistic(
                Offset((moonPos.azimuth / 360f) * w, h * (0.55f - moonPos.elevation / 90f * 0.42f)),
                moonPhase, 26f * density
            )
        }

        if (sunPos.isVisible) {
            drawSunRealistic(
                Offset((sunPos.azimuth / 360f) * w, h * (0.55f - sunPos.elevation / 90f * 0.42f)),
                32f * density * sunPos.sizeScale, isGolden
            )
        }

        clouds.filter { it.layer == 0 }.forEach { c ->
            val cx = ((c.x + cloudMove * c.speed) % 1.5f - 0.25f) * w
            drawRealisticCloud(Offset(cx, c.y * h), c.scale * density, c.alpha * 0.85f)
        }

        if (!isNight && !isRainy && !isStormy) {
            birds.forEachIndexed { idx, b ->
                val bx = (birdMove * b.speed * w) + (idx * 50f * density)
                val by = b.y * h + sin(birdMove * PI * 4).toFloat() * 18f * density
                drawBird(Offset(bx, by), b.size * density, wingFlap, 0.75f)
            }
        }

        val hour = now.hour + now.minute / 60f
        if (hour in 4f..6f) drawFog(w, h, density)

        drawMasjidilHaramRealistic(w, h, floorY, isNight, isGolden, lampGlow, density)

        drawPilgrimsRealistic(w, h, floorY, pilgrims, density, isNight)

        drawMarbleFloorRealistic(w, h, floorY, skyColors, isNight, isGolden, density)

        clouds.filter { it.layer == 1 }.forEach { c ->
            val cx = ((c.x + cloudMove * c.speed) % 1.5f - 0.25f) * w
            drawRealisticCloud(Offset(cx, c.y * h), c.scale * density, c.alpha)
        }

        if (isRainy) drawRain(rainDrops, rainFall, w, h)

        if (isStormy && lightning > 0.05f) {
            drawRect(Color.White.copy(alpha = lightning * 0.55f), size = Size(w, h))
        }

        if (isNight) drawRect(Color(0xFF000018).copy(alpha = 0.20f), size = Size(w, h))
    }
}

private fun DrawScope.drawSunRealistic(pos: Offset, r: Float, isGolden: Boolean) {
    val core = if (isGolden) Color(0xFFFFCC33) else Color(0xFFFFF59D)
    val glow = if (isGolden) Color(0xFFFFA726) else Color(0xFFFFF176)
    drawCircle(glow.copy(alpha = 0.05f), r * 5f, pos)
    drawCircle(glow.copy(alpha = 0.10f), r * 3.2f, pos)
    drawCircle(glow.copy(alpha = 0.20f), r * 2f, pos)
    drawCircle(glow.copy(alpha = 0.35f), r * 1.4f, pos)
    for (i in 0 until 12) {
        val angle = (i * 30f + (System.currentTimeMillis() / 100) % 360).toDouble()
        val rad = Math.toRadians(angle)
        val sx = pos.x + cos(rad).toFloat() * r * 1.5f
        val sy = pos.y + sin(rad).toFloat() * r * 1.5f
        val ex = pos.x + cos(rad).toFloat() * r * 2.5f
        val ey = pos.y + sin(rad).toFloat() * r * 2.5f
        drawLine(glow.copy(alpha = 0.15f), Offset(sx, sy), Offset(ex, ey), strokeWidth = 2f)
    }
    drawCircle(core, r, pos)
    drawCircle(Color.White, r * 0.55f, pos)
}

private fun DrawScope.drawMoonRealistic(pos: Offset, phase: SunMoonCalculator.MoonPhase, r: Float) {
    drawCircle(Color(0xFFB0BEC5).copy(alpha = 0.12f), r * 2.5f, pos)
    drawCircle(Color(0xFFCFD8DC).copy(alpha = 0.18f), r * 1.6f, pos)
    drawCircle(Color(0xFFEEF2F4), r, pos)
    drawCircle(Color(0xFFB0BEC5).copy(alpha = 0.35f), r * 0.20f,
        Offset(pos.x - r * 0.30f, pos.y - r * 0.25f))
    drawCircle(Color(0xFFB0BEC5).copy(alpha = 0.28f), r * 0.14f,
        Offset(pos.x + r * 0.35f, pos.y + r * 0.15f))
    drawCircle(Color(0xFFB0BEC5).copy(alpha = 0.22f), r * 0.17f,
        Offset(pos.x - r * 0.10f, pos.y + r * 0.40f))
    drawCircle(Color(0xFFB0BEC5).copy(alpha = 0.18f), r * 0.10f,
        Offset(pos.x + r * 0.05f, pos.y - r * 0.45f))

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

private fun DrawScope.drawRealisticCloud(center: Offset, scale: Float, alpha: Float) {
    val r = 40f * scale
    val topLight = Color.White.copy(alpha = alpha * 0.98f)
    val topMid = Color(0xFFFAFAFA).copy(alpha = alpha * 0.92f)
    val midGray = Color(0xFFE8E8E8).copy(alpha = alpha * 0.85f)
    val botGray = Color(0xFFA8B0B8).copy(alpha = alpha * 0.60f)

    drawCircle(topLight, r * 0.95f, Offset(center.x - r * 1.6f, center.y + r * 0.10f))
    drawCircle(topLight, r * 1.25f, Offset(center.x - r * 0.65f, center.y - r * 0.30f))
    drawCircle(topLight, r * 1.45f, Offset(center.x + r * 0.25f, center.y - r * 0.42f))
    drawCircle(topLight, r * 1.15f, Offset(center.x + r * 1.10f, center.y - r * 0.25f))
    drawCircle(topLight, r * 0.85f, Offset(center.x + r * 1.85f, center.y + r * 0.05f))

    drawCircle(topMid, r * 1.10f, Offset(center.x - r * 1.15f, center.y + r * 0.20f))
    drawCircle(midGray, r * 1.30f, Offset(center.x - r * 0.20f, center.y + r * 0.20f))
    drawCircle(topMid, r * 1.20f, Offset(center.x + r * 0.85f, center.y + r * 0.15f))

    drawCircle(botGray, r * 0.95f, Offset(center.x - r * 1.40f, center.y + r * 0.65f))
    drawCircle(botGray, r * 1.15f, Offset(center.x - r * 0.40f, center.y + r * 0.72f))
    drawCircle(botGray, r * 1.05f, Offset(center.x + r * 0.65f, center.y + r * 0.68f))
    drawCircle(botGray, r * 0.85f, Offset(center.x + r * 1.55f, center.y + r * 0.55f))
}

private fun DrawScope.drawMasjidilHaramRealistic(
    w: Float, h: Float, floorY: Float,
    isNight: Boolean, isGolden: Boolean, lampGlow: Float, density: Float
) {
    val arcadeBaseY = floorY
    val arcadeHeight1 = h * 0.14f
    val arcadeHeight2 = h * 0.10f
    val arcadeTopY = arcadeBaseY - arcadeHeight1 - arcadeHeight2

    val arcadeMain = when {
        isNight -> Color(0xFF2A2E38)
        isGolden -> Color(0xFFE8D4A8)
        else -> Color(0xFFF5EDDC)
    }
    val arcadeShadow = when {
        isNight -> Color(0xFF1A1E28)
        isGolden -> Color(0xFFB89A70)
        else -> Color(0xFFD5C8B0)
    }
    val archDark = when {
        isNight -> Color(0xFF050810)
        isGolden -> Color(0xFF4A3820)
        else -> Color(0xFF4A4038)
    }

    drawArcadeComplex(
        xStart = 0f, xEnd = w * 0.32f,
        topY = arcadeTopY, baseY = arcadeBaseY,
        mainColor = arcadeMain, shadowColor = arcadeShadow, archDark = archDark,
        isNight = isNight, lampGlow = lampGlow, density = density
    )

    drawArcadeComplex(
        xStart = w * 0.68f, xEnd = w,
        topY = arcadeTopY, baseY = arcadeBaseY,
        mainColor = arcadeMain, shadowColor = arcadeShadow, archDark = archDark,
        isNight = isNight, lampGlow = lampGlow, density = density
    )

    val minaretColor = when {
        isNight -> Color(0xFF252830)
        isGolden -> Color(0xFFE0C8A0)
        else -> Color(0xFFF0E8D8)
    }
    drawMinaretHD(w * 0.33f, h * 0.10f, arcadeTopY, minaretColor, isNight, lampGlow, density)
    drawMinaretHD(w * 0.67f, h * 0.10f, arcadeTopY, minaretColor, isNight, lampGlow, density)

    drawKaabahHD(w, h, floorY, isNight, isGolden, density)
}

private fun DrawScope.drawArcadeComplex(
    xStart: Float, xEnd: Float, topY: Float, baseY: Float,
    mainColor: Color, shadowColor: Color, archDark: Color,
    isNight: Boolean, lampGlow: Float, density: Float
) {
    val width = xEnd - xStart
    val totalH = baseY - topY
    val tier1H = totalH * 0.58f
    val tier2H = totalH * 0.38f
    val tier2Y = topY + totalH * 0.04f

    val t1Y = topY + tier2H + totalH * 0.04f
    drawRect(mainColor, Offset(xStart, t1Y), Size(width, tier1H))

    val archCount1 = 10
    val archW1 = width / archCount1
    val archGap1 = archW1 * 0.10f
    val archInnerW1 = archW1 - archGap1 * 2f
    val archH1 = tier1H * 0.78f

    for (i in 0 until archCount1) {
        val ax = xStart + i * archW1 + archGap1
        val ay = t1Y + tier1H * 0.12f
        drawRoundRect(
            color = archDark,
            topLeft = Offset(ax, ay),
            size = Size(archInnerW1, archH1),
            cornerRadius = CornerRadius(archInnerW1 / 2f, archInnerW1 / 2f)
        )
        drawRoundRect(
            color = Color(0xFFD4AF37).copy(alpha = 0.5f),
            topLeft = Offset(ax, ay),
            size = Size(archInnerW1, archH1 * 0.18f),
            cornerRadius = CornerRadius(archInnerW1 / 2f, archInnerW1 / 2f),
            style = Stroke(width = 1.8f)
        )
        if (isNight) {
            val lampX = ax + archInnerW1 / 2f
            val lampY = ay + archH1 * 0.60f
            drawCircle(Color(0xFFFFE082).copy(alpha = 0.9f * lampGlow),
                archInnerW1 * 0.14f, Offset(lampX, lampY))
            drawCircle(Color(0xFFFFF9C4).copy(alpha = 0.5f * lampGlow),
                archInnerW1 * 0.28f, Offset(lampX, lampY))
        }
    }

    drawLine(Color(0xFFD4AF37).copy(alpha = 0.6f),
        Offset(xStart, t1Y), Offset(xEnd, t1Y), strokeWidth = 2f)

    drawRect(mainColor, Offset(xStart, tier2Y), Size(width, tier2H))

    val archCount2 = 14
    val archW2 = width / archCount2
    val archGap2 = archW2 * 0.10f
    val archInnerW2 = archW2 - archGap2 * 2f
    val archH2 = tier2H * 0.70f

    for (i in 0 until archCount2) {
        val ax = xStart + i * archW2 + archGap2
        val ay = tier2Y + tier2H * 0.15f
        drawRoundRect(
            color = archDark.copy(alpha = 0.85f),
            topLeft = Offset(ax, ay),
            size = Size(archInnerW2, archH2),
            cornerRadius = CornerRadius(archInnerW2 / 2f, archInnerW2 / 2f)
        )
        if (isNight) {
            drawCircle(Color(0xFFFFE082).copy(alpha = 0.7f * lampGlow),
                archInnerW2 * 0.12f,
                Offset(ax + archInnerW2 / 2f, ay + archH2 * 0.55f))
        }
    }

    drawLine(Color(0xFFD4AF37).copy(alpha = 0.7f),
        Offset(xStart, tier2Y), Offset(xEnd, tier2Y), strokeWidth = 2.5f)

    drawRect(shadowColor.copy(alpha = 0.4f),
        Offset(xStart, baseY - totalH * 0.06f),
        Size(width, totalH * 0.06f))
}

private fun DrawScope.drawMinaretHD(
    cx: Float, topY: Float, botY: Float,
    color: Color, isNight: Boolean, lampGlow: Float, density: Float
) {
    val minaretW = 22f * density
    val halfW = minaretW / 2f

    drawRect(color, Offset(cx - halfW, topY + 42f * density),
        Size(minaretW, botY - topY - 42f * density))

    drawRect(color, Offset(cx - halfW * 1.7f, topY + 90f * density),
        Size(minaretW * 1.7f, 6f * density))
    drawRect(color, Offset(cx - halfW * 1.7f, topY + 160f * density),
        Size(minaretW * 1.7f, 6f * density))

    drawRoundRect(
        color = color,
        topLeft = Offset(cx - halfW * 1.3f, topY + 20f * density),
        size = Size(minaretW * 1.3f, 24f * density),
        cornerRadius = CornerRadius(minaretW * 0.65f, minaretW * 0.65f)
    )
    drawCircle(color, halfW * 0.85f, Offset(cx, topY + 16f * density))

    drawLine(color, Offset(cx, topY + 4f * density),
        Offset(cx, topY + 16f * density), strokeWidth = 2f * density)

    drawArc(
        color = Color(0xFFD4AF37),
        startAngle = 30f, sweepAngle = 300f, useCenter = false,
        topLeft = Offset(cx - 6f * density, topY - 4f * density),
        size = Size(12f * density, 12f * density),
        style = Stroke(width = 2.2f * density)
    )

    if (isNight) {
        drawCircle(Color(0xFFFFE082).copy(alpha = 0.85f * lampGlow),
            3.5f * density, Offset(cx, topY + 65f * density))
        drawCircle(Color(0xFFFFE082).copy(alpha = 0.75f * lampGlow),
            3.5f * density, Offset(cx, topY + 135f * density))
        drawCircle(Color(0xFFFFE082).copy(alpha = 0.65f * lampGlow),
            3.5f * density, Offset(cx, topY + 200f * density))
    }
}

private fun DrawScope.drawKaabahHD(
    w: Float, h: Float, floorY: Float,
    isNight: Boolean, isGolden: Boolean, density: Float
) {
    val kaabahW = w * 0.26f
    val kaabahH = h * 0.34f
    val kaabahX = (w - kaabahW) / 2f
    val kaabahY = floorY - kaabahH

    val baseColor = when {
        isNight -> Color(0xFF050505)
        isGolden -> Color(0xFF16100C)
        else -> Color(0xFF0A0A0A)
    }
    val highlightLeft = when {
        isNight -> Color(0xFF141414)
        isGolden -> Color(0xFF3A2610)
        else -> Color(0xFF1E1E1E)
    }
    val highlightTop = when {
        isNight -> Color(0xFF101010)
        isGolden -> Color(0xFF2A1E0C)
        else -> Color(0xFF161616)
    }

    drawRect(Color(0x44000000).copy(alpha = 0.5f),
        Offset(kaabahX - 6f, kaabahY - 4f),
        Size(kaabahW + 12f, kaabahH + 6f))

    drawRect(baseColor, Offset(kaabahX, kaabahY), Size(kaabahW, kaabahH))

    drawRect(
        Brush.horizontalGradient(
            listOf(
                highlightLeft.copy(alpha = 0.6f),
                highlightLeft.copy(alpha = 0.0f)
            ),
            kaabahX, kaabahX + kaabahW * 0.28f
        ),
        Offset(kaabahX, kaabahY), Size(kaabahW * 0.28f, kaabahH)
    )

    drawRect(
        Brush.verticalGradient(
            listOf(
                highlightTop.copy(alpha = 0.5f),
                highlightTop.copy(alpha = 0.0f)
            ),
            kaabahY, kaabahY + kaabahH * 0.22f
        ),
        Offset(kaabahX, kaabahY), Size(kaabahW, kaabahH * 0.22f)
    )

    val hizamY = kaabahY + kaabahH * 0.44f
    val hizamH = kaabahH * 0.075f

    drawRect(
        Brush.horizontalGradient(listOf(
            Color(0xFF8B6914), Color(0xFFFFD700), Color(0xFFFFF176),
            Color(0xFFFFD700), Color(0xFFB8860B), Color(0xFFFFD700),
            Color(0xFFFFF176), Color(0xFFFFD700), Color(0xFF8B6914)
        )),
        Offset(kaabahX, hizamY), Size(kaabahW, hizamH)
    )

    val motifCount = 24
    val motifW = kaabahW / motifCount
    for (i in 0 until motifCount) {
        val mx = kaabahX + i * motifW + motifW / 2f
        val my = hizamY + hizamH / 2f
        drawCircle(Color(0xFF5A3D0A).copy(alpha = 0.75f),
            hizamH * 0.24f, Offset(mx, my))
        drawCircle(Color(0xFFFFF176).copy(alpha = 0.9f),
            hizamH * 0.07f, Offset(mx, my))
        drawLine(Color(0xFF6B4A0E).copy(alpha = 0.6f),
            Offset(mx - hizamH * 0.10f, my - hizamH * 0.22f),
            Offset(mx - hizamH * 0.10f, my + hizamH * 0.22f),
            strokeWidth = 1.2f)
        drawLine(Color(0xFF6B4A0E).copy(alpha = 0.6f),
            Offset(mx + hizamH * 0.10f, my - hizamH * 0.22f),
            Offset(mx + hizamH * 0.10f, my + hizamH * 0.22f),
            strokeWidth = 1.2f)
    }

    drawLine(Color(0xFFFFD700),
        Offset(kaabahX, hizamY),
        Offset(kaabahX + kaabahW, hizamY), strokeWidth = 2.5f)
    drawLine(Color(0xFFFFD700),
        Offset(kaabahX, hizamY + hizamH),
        Offset(kaabahX + kaabahW, hizamY + hizamH), strokeWidth = 2.5f)

    val doorW = kaabahW * 0.12f
    val doorH = kaabahH * 0.24f
    val doorX = kaabahX + kaabahW * 0.44f
    val doorY = kaabahY + kaabahH * 0.70f

    drawRect(Color(0xFFB8860B), Offset(doorX - 2f, doorY - 2f),
        Size(doorW + 4f, doorH + 4f))
    drawRect(Color(0xFFD4AF37), Offset(doorX, doorY), Size(doorW, doorH))
    drawLine(Color(0xFF6B4A0E),
        Offset(doorX + doorW / 2f, doorY),
        Offset(doorX + doorW / 2f, doorY + doorH), strokeWidth = 1.5f)

    if (!isNight && isGolden) {
        drawRect(Color(0xFFFFF176).copy(alpha = 0.25f),
            Offset(kaabahX, hizamY - 3f), Size(kaabahW, hizamH + 6f))
    }

    drawRect(
        Brush.verticalGradient(
            listOf(
                Color(0x88000000),
                Color(0x44000000),
                Color(0x00000000)
            ),
            floorY, floorY + h * 0.05f
        ),
        Offset(kaabahX, floorY), Size(kaabahW, h * 0.05f)
    )
}

private fun DrawScope.drawPilgrimsRealistic(
    w: Float, h: Float, floorY: Float,
    pilgrims: List<Pilgrim>, density: Float, isNight: Boolean
) {
    pilgrims.sortedByDescending { it.z }.forEach { p ->
        val px = p.x * w
        val depthOffset = (1f - p.z) * h * 0.03f
        val py = floorY + depthOffset - h * 0.005f

        val sizeFactor = 0.7f + (1f - p.z) * 0.5f
        val bodyH = 14f * density * p.size * sizeFactor
        val bodyW = 5.5f * density * p.size * sizeFactor

        val color = when {
            isNight -> if (p.isWoman) Color(0xFF0A0A0A) else Color(0xFF2A2A2A)
            else -> if (p.isWoman) Color(0xFF0A0A0A) else Color(0xFFEDEDED)
        }
        val headColor = when {
            isNight -> if (p.isWoman) Color(0xFF0A0A0A) else Color(0xFF2A2A2A)
            else -> if (p.isWoman) Color(0xFF0A0A0A) else Color(0xFFF0E0C0)
        }

        drawOval(
            color = Color(0x55000000),
            topLeft = Offset(px - bodyW * 0.9f, py - 2f),
            size = Size(bodyW * 1.8f, bodyW * 0.7f)
        )

        drawCircle(headColor, bodyW * 0.55f, Offset(px, py - bodyH + bodyW * 0.3f))
        drawRoundRect(
            color = color,
            topLeft = Offset(px - bodyW / 2f, py - bodyH + bodyW * 0.6f),
            size = Size(bodyW, bodyH - bodyW * 0.5f),
            cornerRadius = CornerRadius(bodyW / 2f, bodyW * 0.3f)
        )
    }
}

private fun DrawScope.drawMarbleFloorRealistic(
    w: Float, h: Float, floorY: Float,
    skyColors: List<Color>, isNight: Boolean, isGolden: Boolean, density: Float
) {
    val marbleLight = when {
        isNight -> Color(0xFF2A2A38)
        isGolden -> Color(0xFFF0D8B0)
        else -> Color(0xFFF8F4E8)
    }
    val marbleDark = when {
        isNight -> Color(0xFF15151F)
        isGolden -> Color(0xFFC9A878)
        else -> Color(0xFFE0D8C8)
    }

    drawRect(
        Brush.verticalGradient(listOf(marbleLight, marbleDark), floorY, h),
        Offset(0f, floorY), Size(w, h - floorY)
    )

    if (skyColors.isNotEmpty()) {
        drawRect(
            Brush.verticalGradient(
                listOf(
                    skyColors.last().copy(alpha = if (isNight) 0.06f else 0.10f),
                    Color.Transparent
                ),
                floorY, h
            ),
            Offset(0f, floorY), Size(w, (h - floorY) * 0.7f)
        )
    }

    val hLineCount = 5
    for (i in 1..hLineCount) {
        val ly = floorY + (h - floorY) * i / (hLineCount + 1)
        val fade = 1f - i.toFloat() / hLineCount * 0.6f
        drawLine(
            Color(0xFF808080).copy(alpha = 0.15f * fade),
            Offset(0f, ly), Offset(w, ly), strokeWidth = 0.8f
        )
    }

    val vanishX = w / 2f
    val vLineCount = 14
    for (i in 0..vLineCount) {
        val startX = w * i / vLineCount
        val endX = startX + (vanishX - startX) * 0.35f
        drawLine(
            Color(0xFF808080).copy(alpha = 0.12f),
            Offset(startX, floorY), Offset(endX, h),
            strokeWidth = 0.8f
        )
    }

    if (!isNight) {
        val sunGlowX = w / 2f
        drawRect(
            Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFFF176).copy(alpha = if (isGolden) 0.28f else 0.15f),
                    Color.Transparent
                ),
                center = Offset(sunGlowX, floorY + (h - floorY) * 0.3f),
                radius = w * 0.35f
            ),
            Offset(0f, floorY), Size(w, h - floorY)
        )
    }

    val kaabahShadowW = w * 0.26f
    drawRect(
        Brush.verticalGradient(
            listOf(
                Color(0x66000000),
                Color(0x33000000),
                Color(0x00000000)
            ),
            floorY, floorY + h * 0.08f
        ),
        Offset((w - kaabahShadowW) / 2f, floorY),
        Size(kaabahShadowW, h * 0.08f)
    )
}

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

private fun DrawScope.drawFog(w: Float, h: Float, density: Float) {
    val fogY = h * 0.60f
    drawRect(
        Brush.verticalGradient(
            listOf(
                Color.White.copy(alpha = 0f),
                Color.White.copy(alpha = 0.08f),
                Color.White.copy(alpha = 0.18f),
                Color.White.copy(alpha = 0.30f)
            ),
            fogY - 80f * density, h
        ),
        Offset(0f, fogY - 80f * density),
        Size(w, h - fogY + 80f * density)
    )
}

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
