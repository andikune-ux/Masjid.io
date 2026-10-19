package dev.andikune.masjidio.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
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
import kotlinx.coroutines.launch

private val DIALOG_BG = Color(0xFF0C1B26)
private val NUMPAD_BG = Color(0x33000000)

@Composable
private fun PinDots(enteredPin: String, isError: Boolean) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
        for (i in 0 until 4) {
            val isFilled = i < enteredPin.length
            Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(if (isFilled) (if (isError) UrgentRed else IslamicGold) else Color(0x33FFFFFF)).border(1.5.dp, if (isError) UrgentRed else IslamicGold, CircleShape))
        }
    }
}

@Composable
private fun PinPad(onKeyPress: (String) -> Unit, firstKeyFocusRequester: FocusRequester? = null) {
    val numpad = listOf(listOf("1", "2", "3"), listOf("4", "5", "6"), listOf("7", "8", "9"), listOf("C", "0", "⌫"))
    Column(modifier = Modifier.fillMaxWidth().focusGroup(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        for ((rowIdx, row) in numpad.withIndex()) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                for ((colIdx, key) in row.withIndex()) {
                    val isFirst = rowIdx == 0 && colIdx == 0
                    PinPadKey(
                        key = key, 
                        modifier = Modifier.weight(1f), 
                        focusRequester = if (isFirst) firstKeyFocusRequester else null, 
                        onClick = { onKeyPress(key) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PinPadKey(key: String, modifier: Modifier = Modifier, focusRequester: FocusRequester? = null, onClick: () -> Unit) {
    var focused by remember { mutableStateOf(false) }
    NeonFocusBorder(focused = focused, cornerRadius = 10.dp, borderWidth = 3.dp, modifier = modifier.height(44.dp)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(3.dp) // <-- PADDING AGAR BORDER NEONFOCUSBORDER TIDAK TERTUTUP BACKGROUND
                .clip(RoundedCornerShape(8.dp))
                .background(if (focused) Color(0x55FFD700) else NUMPAD_BG)
                .let { if (focusRequester != null) it.focusRequester(focusRequester) else it }
                .onFocusChanged { focused = it.isFocused } // <-- URUTAN DIPERBAIKI
                .focusable()
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            if (key == "⌫") {
                Icon(Icons.AutoMirrored.Filled.Backspace, "Hapus", tint = if (focused) Color(0xFFFFE44D) else IslamicGold, modifier = Modifier.size(18.dp))
            } else {
                Text(key, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = if (focused) Color(0xFFFFE44D) else if (key == "C") UrgentRed else TextPrimary)
            }
        }
    }
}

@Composable
private fun DialogActionButton(text: String, modifier: Modifier = Modifier, textColor: Color = TextSecondary, onClick: () -> Unit) {
    var focused by remember { mutableStateOf(false) }
    NeonFocusBorder(focused = focused, cornerRadius = 8.dp, borderWidth = 3.dp, modifier = modifier.height(38.dp)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(3.dp) // <-- PADDING
                .clip(RoundedCornerShape(6.dp))
                .background(if (focused) Color(0x33FFD700) else Color.Transparent)
                .onFocusChanged { focused = it.isFocused }
                .focusable()
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(text, color = if (focused) IslamicGoldLight else textColor, fontSize = 13.sp, fontWeight = if (focused) FontWeight.Bold else FontWeight.Normal)
        }
    }
}

@Composable
fun PinDialog(correctPin: String, onSuccess: () -> Unit, onDismiss: () -> Unit) {
    var enteredPin by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    val firstKeyFocusRequester = remember { FocusRequester() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        delay(400L)
        runCatching { firstKeyFocusRequester.requestFocus() }
        delay(300L)
        runCatching { firstKeyFocusRequester.requestFocus() }
    }

    fun handleNumberInput(num: String) {
        if (enteredPin.length < 4) {
            enteredPin += num
            isError = false
            if (enteredPin.length == 4) {
                if (enteredPin == correctPin) onSuccess()
                else {
                    isError = true
                    scope.launch {
                        delay(300)
                        firstKeyFocusRequester.requestFocus()
                    }
                }
            }
        }
    }

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false, dismissOnBackPress = true, dismissOnClickOutside = false)) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                modifier = Modifier.fillMaxWidth(0.92f).widthIn(max = 400.dp).fillMaxHeight(0.94f).clip(RoundedCornerShape(18.dp)).background(DIALOG_BG).border(2.dp, IslamicGold, RoundedCornerShape(18.dp)).padding(horizontal = 18.dp, vertical = 16.dp).verticalScroll(rememberScrollState()).focusGroup()
                    .onPreviewKeyEvent { event ->
                        if (event.type == KeyEventType.KeyDown) {
                            val num = when(event.key) {
                                Key.Zero -> "0"; Key.One -> "1"; Key.Two -> "2"; Key.Three -> "3"; Key.Four -> "4"
                                Key.Five -> "5"; Key.Six -> "6"; Key.Seven -> "7"; Key.Eight -> "8"; Key.Nine -> "9"
                                else -> null
                            }
                            if (num != null) {
                                handleNumberInput(num)
                                true
                            } else false
                        } else false
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.Lock, null, tint = IslamicGold, modifier = Modifier.size(30.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text("PIN KEAMANAN TAKMIR", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = IslamicGoldLight)
                Text("Masukkan PIN untuk membuka pengaturan / keluar", fontSize = 11.sp, color = TextSecondary, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(12.dp))
                PinDots(enteredPin = enteredPin, isError = isError)
                if (isError) { Spacer(modifier = Modifier.height(6.dp)); Text("PIN salah! Coba lagi.", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = UrgentRed) }
                Spacer(modifier = Modifier.height(14.dp))
                PinPad(
                    onKeyPress = { key ->
                        when (key) {
                            "C" -> { enteredPin = ""; isError = false }
                            "⌫" -> { if (enteredPin.isNotEmpty()) { enteredPin = enteredPin.dropLast(1); isError = false } }
                            else -> handleNumberInput(key)
                        }
                    }, firstKeyFocusRequester = firstKeyFocusRequester
                )
                Spacer(modifier = Modifier.height(6.dp))
                DialogActionButton(text = "Batal", modifier = Modifier.fillMaxWidth(), textColor = TextSecondary, onClick = onDismiss)
            }
        }
    }
}
