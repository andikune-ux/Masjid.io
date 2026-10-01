package com.example.ui.components

import android.graphics.Matrix
import android.graphics.Paint as AndroidPaint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.SweepGradient
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/**
 * BorderPhase — State machine untuk animasi fokus.
 *
 *   HIDDEN      → tidak fokus (tidak render)
 *   LOADING_1X  → fase 1: 1x putaran emas (1200ms) setelah fokus
 *   DELAY       → delay 1000ms setelah fase 1
 *   IDLE        → fase 2: loop tanpa berhenti (putih + emas, 2000ms/putaran)
 *   PRESSED     → fase 3: emas kedip cepat
 */
private enum class BorderPhase {
    HIDDEN,
    LOADING_1X,
    DELAY,
    IDLE,
    PRESSED
}

/**
 * NeonFocusBorder — Border fokus dengan animasi multi-fase.
 *
 *   FASE 1 — Setelah fokus: 1x putaran emas dengan ekor memudar (1200ms)
 *   FASE 2 — Setelah diam 1s: loop tanpa berhenti, 2 kutub (putih + emas) searah jarum jam (2000ms/putaran)
 *   FASE 3 — Saat OK ditekan: emas glow lebih terang + kedip (150ms)
 *
 *   Glow menyebar KELUAR 13.5dp dari tepi tombol.
 *   Scale 1.05x saat fokus.
 */
@Composable
fun NeonFocusBorder(
    focused: Boolean,
    pressed: Boolean = false,
    modifier: Modifier = Modifier,
    borderWidth: Dp = 5.dp,
    cornerRadius: Dp = 12.dp,
    glowRadius: Dp = 13.5.dp,
    content: @Composable () -> Unit
) {
    // ============================================================
    // SCALE — 1.05x saat fokus
    // ============================================================
    val scale by animateFloatAsState(
        targetValue = if (focused) 1.05f else 1f,
        animationSpec = tween(durationMillis = 200),
        label = "focus_scale"
    )

    // ============================================================
    // ROTATION & PHASE
    // ============================================================
    val rotation = remember { Animatable(0f) }
    var currentPhase by remember { mutableStateOf(BorderPhase.HIDDEN) }

    // ============================================================
    // STATE MACHINE — dijalankan saat fokus berubah
    // ============================================================
    LaunchedEffect(focused) {
        if (!focused) {
            currentPhase = BorderPhase.HIDDEN
            rotation.snapTo(0f)
            return@LaunchedEffect
        }

        // ---- FASE 1: 1x putaran emas (1200ms) ----
        currentPhase = BorderPhase.LOADING_1X
        rotation.snapTo(0f)
        rotation.animateTo(
            targetValue = 360f,
            animationSpec = tween(durationMillis = 1200, easing = LinearEasing)
        )

        // ---- DELAY: diam 1000ms ----
        currentPhase = BorderPhase.DELAY
        delay(1000)

        // ---- FASE 2: loop tanpa berhenti (2000ms/putaran) ----
        currentPhase = BorderPhase.IDLE
        while (true) {
            rotation.snapTo(0f)
            rotation.animateTo(
                targetValue = 360f,
                animationSpec = tween(durationMillis = 2000, easing = LinearEasing)
            )
        }
    }

    // ============================================================
    // PRESSED FLASH — emas kedip 150ms
    // ============================================================
    val flashAlpha = remember { Animatable(1f) }
    LaunchedEffect(pressed) {
        if (pressed) {
            while (true) {
                flashAlpha.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(150, easing = LinearEasing)
                )
                flashAlpha.animateTo(
                    targetValue = 0.3f,
                    animationSpec = tween(150, easing = LinearEasing)
                )
            }
        } else {
            flashAlpha.snapTo(1f)
        }
    }

    // ============================================================
    // RENDER
    // ============================================================
    Box(modifier = modifier.scale(scale)) {
        // Canvas glow + border (di bawah konten)
        if (focused) {
            Canvas(modifier = Modifier.matchParentSize()) {
                val strokePx = borderWidth.toPx()
                val glowPx = glowRadius.toPx()
                val radiusPx = cornerRadius.toPx()
                val cx = size.width / 2f
                val cy = size.height / 2f
                val phase = currentPhase
                val rot = rotation.value
                val flash = flashAlpha.value
                val isPressed = pressed

                drawIntoCanvas { canvas ->
                    val native = canvas.nativeCanvas

                    // ===== GLOW LAYERS (menyebar keluar) =====
                    // 5 layer dengan alpha bertingkat
                    val layers = listOf(
                        1.0f to 0.10f,   // terluar: lebar 1.0, alpha 10%
                        0.8f to 0.15f,
                        0.6f to 0.20f,
                        0.4f to 0.25f,
                        0.2f to 0.30f    // terdalam (dekat core): alpha 30%
                    )

                    layers.forEach { (widthFactor, alpha) ->
                        val layerWidth = strokePx + (glowPx * 2f * widthFactor)
                        val inset = layerWidth / 2f
                        val rect = RectF(
                            inset, inset,
                            size.width - inset, size.height - inset
                        )

                        val paint = AndroidPaint().apply {
                            isAntiAlias = true
                            style = AndroidPaint.Style.STROKE
                            this.strokeWidth = layerWidth
                            this.alpha = (255 * alpha).toInt()
                        }

                        applyShaderOrColor(
                            paint = paint,
                            phase = phase,
                            isPressed = isPressed,
                            flash = flash,
                            rot = rot,
                            cx = cx,
                            cy = cy
                        )

                        native.drawRoundRect(rect, radiusPx, radiusPx, paint)
                    }

                    // ===== CORE BORDER (tepi tombol) =====
                    val corePaint = AndroidPaint().apply {
                        isAntiAlias = true
                        style = AndroidPaint.Style.STROKE
                        this.strokeWidth = strokePx
                    }

                    applyShaderOrColor(
                        paint = corePaint,
                        phase = phase,
                        isPressed = isPressed,
                        flash = flash,
                        rot = rot,
                        cx = cx,
                        cy = cy
                    )

                    val coreInset = strokePx / 2f
                    val coreRect = RectF(
                        coreInset, coreInset,
                        size.width - coreInset, size.height - coreInset
                    )
                    native.drawRoundRect(coreRect, radiusPx, radiusPx, corePaint)
                }
            }
        }

        // Konten (di atas glow — menutup bagian dalam border)
        content()
    }
}

// ============================================================
// HELPER — Set shader atau color ke paint sesuai fase
// ============================================================
private fun applyShaderOrColor(
    paint: AndroidPaint,
    phase: BorderPhase,
    isPressed: Boolean,
    flash: Float,
    rot: Float,
    cx: Float,
    cy: Float
) {
    when {
        // ===== PRESSED: emas kedip =====
        isPressed -> {
            paint.color = 0xFFFFD700.toInt()
            paint.alpha = (paint.alpha * flash).toInt()
        }

        // ===== FASE 1 & DELAY: 1 ekor emas =====
        phase == BorderPhase.LOADING_1X || phase == BorderPhase.DELAY -> {
            paint.shader = createSingleTailGradient(cx, cy, rot, 0xFFFFD700.toInt())
        }

        // ===== FASE 2: 2 kutub (putih + emas) =====
        phase == BorderPhase.IDLE -> {
            paint.shader = createDualTailGradient(
                cx, cy, rot,
                white = 0xFFFFFFFF.toInt(),
                gold = 0xFFFFD700.toInt()
            )
        }

        // ===== Fallback: emas solid =====
        else -> {
            paint.color = 0xFFFFD700.toInt()
        }
    }
}

// ============================================================
// HELPER — SweepGradient 1 ekor memudar
// ============================================================
private fun createSingleTailGradient(
    cx: Float, cy: Float, rot: Float, color: Int
): Shader {
    val rgb = color and 0x00FFFFFF
    val transparent = rgb  // alpha 0

    // Ekor memudar: transparan → kepala terang → memudar cepat
    val colors = intArrayOf(
        transparent,                       // 0°   - start ekor
        (rgb) or (0x80 shl 24),            // 18°  - ekor medium (50% alpha)
        color,                             // 30°  - kepala terang (100% alpha)
        transparent,                       // 55°  - memudar cepat
        transparent                        // 360° - transparan
    )
    val positions = floatArrayOf(0f, 0.05f, 0.083f, 0.15f, 1f)

    val shader = SweepGradient(cx, cy, colors, positions)
    val matrix = Matrix().apply { setRotate(rot, cx, cy) }
    shader.setLocalMatrix(matrix)
    return shader
}

// ============================================================
// HELPER — SweepGradient 2 kutub (putih + emas) searah jarum jam
// ============================================================
private fun createDualTailGradient(
    cx: Float, cy: Float, rot: Float,
    white: Int, gold: Int
): Shader {
    val whiteRgb = white and 0x00FFFFFF
    val goldRgb = gold and 0x00FFFFFF
    val transparentWhite = whiteRgb
    val transparentGold = goldRgb

    // 2 kutub: putih di 0°, emas di 180°
    // Masing-masing punya ekor memudar di belakangnya
    val colors = intArrayOf(
        transparentWhite,                       // 0°   - sebelum putih
        (whiteRgb) or (0x80 shl 24),            // 18°  - ekor putih medium
        white,                                  // 36°  - kepala putih terang
        transparentWhite,                       // 60°  - memudar
        transparentGold,                        // 180° - sebelum emas
        (goldRgb) or (0x80 shl 24),             // 198° - ekor emas medium
        gold,                                   // 216° - kepala emas terang
        transparentGold,                        // 240° - memudar
        transparentWhite                        // 360° - loop
    )
    val positions = floatArrayOf(
        0f, 0.05f, 0.10f, 0.1667f,
        0.50f, 0.55f, 0.60f, 0.6667f,
        1f
    )

    val shader = SweepGradient(cx, cy, colors, positions)
    val matrix = Matrix().apply { setRotate(rot, cx, cy) }
    shader.setLocalMatrix(matrix)
    return shader
}
