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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalContext
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
// V1.04.430 — DIALOG PIN & CHANGE PIN
//
// FIX:
// 1. Fix D-pad (FocusRequester + delay 500ms + retry)
// 2. Compact HP (fillMaxWidth + fillMaxHeight + verticalScroll)
// 3. Padding & spacing dikurangi
// ============================================================

// Warna helper
private val DIALOG_BG = Color(0xFF0C1B26)
private val NUMPAD_BG = Color(0x33000000)
private val NUMPAD_BORDER = Color(0x33FFD700)
private val NUMPAD_BG_FOCUS = Color(0x55FFD700)
private val NUMPAD_BORDER_FOCUS = Color(0xFFFFE44D)

// ============================================================
// KOMPONEN: PIN DOTS (4 titik)
// ============================================================
@Composable
private fun PinDots(
    enteredPin: String,
    isError: Boolean
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until 4) {
            val isFilled = i < enteredPin.length
            Box(
                modifier = Modifier
                    .size(16.dp)
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
// KOMPONEN: PIN PAD
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
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        for ((rowIdx, row) in numpad.withIndex()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
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
// KOMPONEN: PIN PAD KEY
// ============================================================
@Composable
private fun PinPadKey(
    key: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    var focused by remember { mutableStateOf(false) }

    NeonFocusBorder(
        focused = focused,
        cornerRadius = 10.dp,
        modifier = modifier
            .height(44.dp)
            .focusable()
            .onFocusChanged { focused = it.isFocused }
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(10.dp))
                .background(if (focused) NUMPAD_BG_FOCUS else NUMPAD_BG)
                .border(1.dp, NUMPAD_BORDER, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (key == "⌫") {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Backspace,
                    contentDescription = "Hapus",
                    tint = if (focused) NUMPAD_BORDER_FOCUS else IslamicGold,
                    modifier = Modifier.size(18.dp)
                )
            } else {
                Text(
                    text = key,
                    fontSize = 16.sp,
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
}

// ============================================================
// KOMPONEN: DIALOG ACTION BUTTON
// ============================================================
@Composable
private fun DialogActionButton(
    text: String,
    modifier: Modifier = Modifier,
    textColor: Color = TextSecondary,
    onClick: () -> Unit
) {
    var focused by remember { mutableStateOf(false) }

    NeonFocusBorder(
        focused = focused,
        cornerRadius = 8.dp,
        modifier = modifier
            .height(38.dp)
            .focusable()
            .onFocusChanged { focused = it.isFocused }
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp))
                .background(if (focused) Color(0x33FFD700) else Color.Transparent)
                .padding(horizontal = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = if (focused) IslamicGoldLight else textColor,
                fontSize = 13.sp,
                fontWeight = if (focused) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}
// ============================================================
// DIALOG 1: PinDialog — untuk masuk ke pengaturan
// ============================================================
@Composable
fun PinDialog(
    correctPin: String,
    onSuccess: () -> Unit,
    onDismiss: () -> Unit
) {
    var enteredPin by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    val isTelevision = remember {
        val uiMode = LocalContext.current.resources.configuration.uiMode
        (uiMode and android.content.res.Configuration.UI_MODE_TYPE_MASK) ==
            android.content.res.Configuration.UI_MODE_TYPE_TELEVISION
    }
    val dialogWidth = if (isTelevision) 0.92f else 0.82f
    val dialogMaxWidth = if (isTelevision) 400.dp else 320.dp
    val dialogMaxHeight = if (isTelevision) 0.94f else 0.88f

    val firstKeyFocusRequester = remember { FocusRequester() }

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
            Column(
                modifier = Modifier
                    .fillMaxWidth(dialogWidth)
                    .widthIn(max = dialogMaxWidth)
                    .fillMaxHeight(dialogMaxHeight)
                    .clip(RoundedCornerShape(18.dp))
                    .background(DIALOG_BG)
                    .border(2.dp, IslamicGold, RoundedCornerShape(18.dp))
                    .padding(horizontal = 14.dp, vertical = 12.dp)
                    .verticalScroll(rememberScrollState())
                    .focusGroup(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = IslamicGold,
                    modifier = Modifier.size(30.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "PIN KEAMANAN TAKMIR",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = IslamicGoldLight
                )

                Text(
                    text = "Masukkan PIN untuk membuka pengaturan / keluar",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                PinDots(enteredPin = enteredPin, isError = isError)

                if (isError) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "PIN salah! Coba lagi.",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = UrgentRed
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

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

                Spacer(modifier = Modifier.height(6.dp))

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
// DIALOG 2: ChangePinDialog — 3 step ubah PIN
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
    val isTelevision = remember {
        val uiMode = LocalContext.current.resources.configuration.uiMode
        (uiMode and android.content.res.Configuration.UI_MODE_TYPE_MASK) ==
            android.content.res.Configuration.UI_MODE_TYPE_TELEVISION
    }
    val dialogWidth = if (isTelevision) 0.92f else 0.82f
    val dialogMaxWidth = if (isTelevision) 400.dp else 320.dp
    val dialogMaxHeight = if (isTelevision) 0.94f else 0.88f

    val firstKeyFocusRequester = remember { FocusRequester() }

    LaunchedEffect(step) {
        enteredPin = ""
        isError = false
        errorMessage = ""
        delay(300L)
        runCatching { firstKeyFocusRequester.requestFocus() }
    }

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
            Column(
                modifier = Modifier
                    .fillMaxWidth(dialogWidth)
                    .widthIn(max = dialogMaxWidth)
                    .fillMaxHeight(dialogMaxHeight)
                    .clip(RoundedCornerShape(18.dp))
                    .background(DIALOG_BG)
                    .border(2.dp, IslamicGold, RoundedCornerShape(18.dp))
                    .padding(horizontal = 14.dp, vertical = 12.dp)
                    .verticalScroll(rememberScrollState())
                    .focusGroup(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = IslamicGold,
                    modifier = Modifier.size(30.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = IslamicGoldLight,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

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

                Spacer(modifier = Modifier.height(12.dp))

                PinDots(enteredPin = enteredPin, isError = isError)

                if (isError) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = errorMessage,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = UrgentRed,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

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

                Spacer(modifier = Modifier.height(6.dp))

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
