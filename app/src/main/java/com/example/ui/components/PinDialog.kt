package dev.andikune.masjidio.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.andikune.masjidio.ui.theme.IslamicGold
import dev.andikune.masjidio.ui.theme.IslamicGoldLight
import dev.andikune.masjidio.ui.theme.IslamicGreen
import dev.andikune.masjidio.ui.theme.TextPrimary
import dev.andikune.masjidio.ui.theme.TextSecondary
import dev.andikune.masjidio.ui.theme.UrgentRed
import kotlinx.coroutines.delay

// ============================================================
// V1.04.428 — DIALOG PIN & CHANGE PIN (FIX D-PAD REMOTE)
//
// PERUBAHAN:
// 1. Tambah FocusRequester ke tombol pertama (auto-focus masuk dialog)
// 2. Urutan modifier: focusable() → onFocusChanged() → clickable()
// 3. focusGroup() di container Column
// 4. DialogProperties: usePlatformDefaultWidth=false
// 5. Tombol Batal & Kembali: Box focusable + border fokus visual
// 6. Numpad: border fokus terlihat
// ============================================================

// Warna helper
private val DIALOG_BG = Color(0xFF0C1B26)
private val NUMPAD_BG = Color(0x33000000)
private val NUMPAD_BORDER = Color(0x33FFD700)
private val NUMPAD_BG_FOCUS = Color(0x55FFD700)
private val NUMPAD_BORDER_FOCUS = Color(0xFFFFE44D)

// ============================================================
// KOMPONEN HELPER: PinDots (4 titik PIN)
// ============================================================
@Composable
private fun PinDots(
    enteredPin: String,
    isError: Boolean
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until 4) {
            val isFilled = i < enteredPin.length
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(
                        if (isFilled) (if (isError) UrgentRed else IslamicGold)
                        else Color(0x33FFFFFF)
                    )
                    .border(1.5.dp, if (isError) UrgentRed else IslamicGold, CircleShape)
            )
        }
    }
}

// ============================================================
// KOMPONEN HELPER: PinPad (numpad 1-9, C, 0, ⌫)
// V1.04.428 — Fix D-pad: focusable dulu, border fokus visual
// ============================================================
@Composable
private fun PinPad(
    onKeyPress: (String) -> Unit,
    firstKeyFocusRequester: FocusRequester? = null
) {
    val numpad = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("C", "0", "⌫")
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .focusGroup(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for ((rowIdx, row) in numpad.withIndex()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                for ((colIdx, key) in row.withIndex()) {
                    val isFirst = rowIdx == 0 && colIdx == 0
                    PinPadKey(
                        key = key,
                        modifier = Modifier
                            .weight(1f)
                            .let { base ->
                                if (isFirst && firstKeyFocusRequester != null) {
                                    base.focusRequester(firstKeyFocusRequester)
                                } else base
                            },
                        onClick = { onKeyPress(key) }
                    )
                }
            }
        }
    }
}

// ============================================================
// KOMPONEN HELPER: PinPadKey (1 tombol numpad, focusable)
// ============================================================
@Composable
private fun PinPadKey(
    key: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    var focused by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(if (focused) NUMPAD_BG_FOCUS else NUMPAD_BG)
            .border(
                width = if (focused) 2.dp else 1.dp,
                color = if (focused) NUMPAD_BORDER_FOCUS else NUMPAD_BORDER,
                shape = RoundedCornerShape(10.dp)
            )
            .focusable()
            .onFocusChanged { focused = it.isFocused }
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (key == "⌫") {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Backspace,
                contentDescription = "Hapus",
                tint = if (focused) NUMPAD_BORDER_FOCUS else IslamicGold,
                modifier = Modifier.size(20.dp)
            )
        } else {
            Text(
                text = key,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = when {
                    focused -> NUMPAD_BORDER_FOCUS
                    key == "C" -> UrgentRed
                    else -> TextPrimary
                }
            )
        }
    }
}

// ============================================================
// KOMPONEN HELPER: DialogActionButton (Batal / Kembali)
// V1.04.428 — Pengganti TextButton (yang tidak focusable visual)
// ============================================================
@Composable
private fun DialogActionButton(
    text: String,
    modifier: Modifier = Modifier,
    textColor: Color = TextSecondary,
    onClick: () -> Unit
) {
    var focused by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .height(40.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (focused) Color(0x33FFD700) else Color.Transparent)
            .border(
                width = if (focused) 2.dp else 0.dp,
                color = if (focused) IslamicGoldLight else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .focusable()
            .onFocusChanged { focused = it.isFocused }
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (focused) IslamicGoldLight else textColor,
            fontSize = 14.sp,
            fontWeight = if (focused) FontWeight.Bold else FontWeight.Normal
        )
    }
}
// ============================================================
// DIALOG 1: PinDialog — untuk masuk ke pengaturan
// V1.04.428 — Fix D-pad: FocusRequester + focusGroup
// ============================================================
@Composable
fun PinDialog(
    correctPin: String,
    onSuccess: () -> Unit,
    onDismiss: () -> Unit
) {
    var enteredPin by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    // ⭐ FocusRequester untuk tombol "1" (numpad pertama)
    val firstKeyFocusRequester = remember { FocusRequester() }

    // ⭐ V1.04.428 — Delay 500ms + retry, biar window dialog attach sempurna
    LaunchedEffect(Unit) {
        delay(500L)
        runCatching { firstKeyFocusRequester.requestFocus() }
        delay(300L)
        runCatching { firstKeyFocusRequester.requestFocus() }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // ⭐ focusGroup() supaya navigasi D-pad dalam dialog smooth
            Column(
                modifier = Modifier
                    .width(360.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(DIALOG_BG)
                    .border(2.dp, IslamicGold, RoundedCornerShape(20.dp))
                    .padding(24.dp)
                    .focusGroup(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ── Icon kunci ──
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = IslamicGold,
                    modifier = Modifier.size(36.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // ── Judul ──
                Text(
                    text = "PIN KEAMANAN TAKMIR",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = IslamicGoldLight
                )

                Text(
                    text = "Masukkan PIN untuk membuka pengaturan / keluar",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // ── Titik PIN ──
                PinDots(enteredPin = enteredPin, isError = isError)

                // ── Pesan error ──
                if (isError) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "PIN salah! Coba lagi.",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = UrgentRed
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // ── Numpad (dengan fokus otomatis di tombol "1") ──
                PinPad(
                    onKeyPress = { key ->
                        when (key) {
                            "C" -> {
                                enteredPin = ""
                                isError = false
                            }
                            "⌫" -> {
                                if (enteredPin.isNotEmpty()) {
                                    enteredPin = enteredPin.dropLast(1)
                                    isError = false
                                }
                            }
                            else -> {
                                if (enteredPin.length < 4) {
                                    enteredPin += key
                                    isError = false
                                    if (enteredPin.length == 4) {
                                        if (enteredPin == correctPin) {
                                            onSuccess()
                                        } else {
                                            isError = true
                                        }
                                    }
                                }
                            }
                        }
                    },
                    firstKeyFocusRequester = firstKeyFocusRequester
                )

                Spacer(modifier = Modifier.height(8.dp))

                // ── Tombol Batal (focusable, D-pad friendly) ──
                DialogActionButton(
                    text = "Batal",
                    modifier = Modifier.fillMaxWidth(),
                    textColor = TextSecondary,
                    onClick = onDismiss
                )
            }
        }
    }
}
// ============================================================
// DIALOG 2: ChangePinDialog — untuk mengubah PIN (3 step)
// V1.04.428 — Fix D-pad: FocusRequester + focusGroup
// ============================================================

private enum class ChangePinStep { ENTER_OLD, ENTER_NEW, CONFIRM_NEW }

@Composable
fun ChangePinDialog(
    currentPin: String,
    onPinChanged: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var step by remember { mutableStateOf(ChangePinStep.ENTER_OLD) }
    var enteredPin by remember { mutableStateOf("") }
    var newPin by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // ⭐ FocusRequester ke tombol "1"
    val firstKeyFocusRequester = remember { FocusRequester() }

    // Reset input saat ganti step
    LaunchedEffect(step) {
        enteredPin = ""
        isError = false
        errorMessage = ""
        // Re-focus ke tombol "1" setelah ganti step
        delay(300L)
        runCatching { firstKeyFocusRequester.requestFocus() }
    }

    // ⭐ Auto-focus awal saat dialog muncul (delay 500ms + retry)
    LaunchedEffect(Unit) {
        delay(500L)
        runCatching { firstKeyFocusRequester.requestFocus() }
        delay(300L)
        runCatching { firstKeyFocusRequester.requestFocus() }
    }

    val title = when (step) {
        ChangePinStep.ENTER_OLD -> "MASUKKAN PIN LAMA"
        ChangePinStep.ENTER_NEW -> "MASUKKAN PIN BARU"
        ChangePinStep.CONFIRM_NEW -> "ULANGI PIN BARU"
    }

    val subtitle = when (step) {
        ChangePinStep.ENTER_OLD -> "Verifikasi PIN Anda yang sekarang"
        ChangePinStep.ENTER_NEW -> "Buat PIN baru 4 digit"
        ChangePinStep.CONFIRM_NEW -> "Ketik ulang PIN baru untuk konfirmasi"
    }

    val stepIndicator = when (step) {
        ChangePinStep.ENTER_OLD -> "Langkah 1 dari 3"
        ChangePinStep.ENTER_NEW -> "Langkah 2 dari 3"
        ChangePinStep.CONFIRM_NEW -> "Langkah 3 dari 3"
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // ⭐ focusGroup() untuk navigasi D-pad
            Column(
                modifier = Modifier
                    .width(360.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(DIALOG_BG)
                    .border(2.dp, IslamicGold, RoundedCornerShape(20.dp))
                    .padding(24.dp)
                    .focusGroup(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ── Icon kunci ──
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = IslamicGold,
                    modifier = Modifier.size(36.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // ── Judul step ──
                Text(
                    text = title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = IslamicGoldLight,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                // ── Badge step indicator ──
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(IslamicGreen.copy(alpha = 0.2f))
                        .padding(horizontal = 10.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = stepIndicator,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = IslamicGreen,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ── Titik PIN ──
                PinDots(enteredPin = enteredPin, isError = isError)

                // ── Pesan error ──
                if (isError) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = UrgentRed,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // ── Numpad ──
                PinPad(
                    onKeyPress = { key ->
                        when (key) {
                            "C" -> {
                                enteredPin = ""
                                isError = false
                                errorMessage = ""
                            }
                            "⌫" -> {
                                if (enteredPin.isNotEmpty()) {
                                    enteredPin = enteredPin.dropLast(1)
                                    isError = false
                                    errorMessage = ""
                                }
                            }
                            else -> {
                                if (enteredPin.length < 4) {
                                    enteredPin += key
                                    isError = false
                                    if (enteredPin.length == 4) {
                                        when (step) {
                                            ChangePinStep.ENTER_OLD -> {
                                                if (enteredPin == currentPin) {
                                                    step = ChangePinStep.ENTER_NEW
                                                } else {
                                                    isError = true
                                                    errorMessage = "PIN lama salah"
                                                }
                                            }
                                            ChangePinStep.ENTER_NEW -> {
                                                if (enteredPin == currentPin) {
                                                    isError = true
                                                    errorMessage = "PIN baru tidak boleh sama dengan PIN lama"
                                                } else {
                                                    newPin = enteredPin
                                                    step = ChangePinStep.CONFIRM_NEW
                                                }
                                            }
                                            ChangePinStep.CONFIRM_NEW -> {
                                                if (enteredPin == newPin) {
                                                    onPinChanged(newPin)
                                                } else {
                                                    isError = true
                                                    errorMessage = "PIN tidak cocok, ulangi"
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    },
                    firstKeyFocusRequester = firstKeyFocusRequester
                )

                Spacer(modifier = Modifier.height(8.dp))

                // ── Tombol Kembali + Batal (focusable) ──
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (step != ChangePinStep.ENTER_OLD) {
                        DialogActionButton(
                            text = "← Kembali",
                            textColor = IslamicGold,
                            onClick = {
                                step = when (step) {
                                    ChangePinStep.ENTER_NEW -> ChangePinStep.ENTER_OLD
                                    ChangePinStep.CONFIRM_NEW -> ChangePinStep.ENTER_NEW
                                    else -> ChangePinStep.ENTER_OLD
                                }
                            }
                        )
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    DialogActionButton(
                        text = "Batal",
                        textColor = TextSecondary,
                        onClick = onDismiss
                    )
                }
            }
        }
    }
}
