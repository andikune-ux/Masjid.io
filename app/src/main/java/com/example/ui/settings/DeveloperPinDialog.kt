package dev.andikune.masjidio.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.andikune.masjidio.ui.theme.IslamicGold
import dev.andikune.masjidio.ui.theme.IslamicGoldLight
import dev.andikune.masjidio.ui.theme.TextPrimary
import dev.andikune.masjidio.ui.theme.TextSecondary
import dev.andikune.masjidio.ui.theme.UrgentRed
import kotlinx.coroutines.delay

// ============================================================
// V1.04.430 — DEVELOPER PIN DIALOG
//
// FIX:
// 1. Fix D-pad (FocusRequester + delay 500ms + retry)
// 2. Compact HP (fillMaxWidth + fillMaxHeight + verticalScroll)
// 3. Padding & spacing dikurangi
// ============================================================
@Composable
fun DeveloperPinDialog(
    correctPin: String = "140399",
    onSuccess: () -> Unit,
    onDismiss: () -> Unit
) {
    var pin by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isFocused by remember { mutableStateOf(false) }

    // State fokus tombol
    var isFocusedBatal by remember { mutableStateOf(false) }
    var isFocusedMasuk by remember { mutableStateOf(false) }

    // FocusRequester untuk auto-fokus ke input PIN
    val pinFocusRequester = remember { FocusRequester() }

    // Auto-fokus saat dialog muncul (delay 500ms + retry)
    LaunchedEffect(Unit) {
        delay(500L)
        runCatching { pinFocusRequester.requestFocus() }
        delay(300L)
        runCatching { pinFocusRequester.requestFocus() }
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
                    .fillMaxWidth(0.92f)
                    .widthIn(max = 420.dp)
                    .fillMaxHeight(0.94f)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFF0B1720))
                    .border(2.dp, IslamicGold, RoundedCornerShape(18.dp))
                    .padding(horizontal = 18.dp, vertical = 16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "🔐 AKSES DEVELOPER",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = IslamicGoldLight
                )

                Text(
                    text = "Masukkan PIN Developer (6 digit)",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )

                // ============================================================
                // INPUT PIN
                // ============================================================
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF142735))
                        .border(
                            width = if (isFocused) 3.dp else 1.5.dp,
                            color = if (isFocused) IslamicGoldLight else IslamicGold.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .focusRequester(pinFocusRequester)
                        .onFocusChanged { isFocused = it.isFocused }
                        .focusable()
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    BasicTextField(
                        value = pin,
                        onValueChange = {
                            if (it.length <= 6 && it.all { c -> c.isDigit() }) {
                                pin = it
                                errorMessage = null
                            }
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.NumberPassword
                        ),
                        textStyle = TextStyle(
                            fontSize = 20.sp,
                            color = IslamicGoldLight,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // ============================================================
                // INDIKATOR 6 DIGIT
                // ============================================================
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(6) { index ->
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    if (index < pin.length) IslamicGold
                                    else Color(0x33FFFFFF)
                                )
                        )
                    }
                }

                // ============================================================
                // PESAN ERROR
                // ============================================================
                if (errorMessage != null) {
                    Text(
                        text = errorMessage ?: "",
                        fontSize = 12.sp,
                        color = UrgentRed,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // ============================================================
                // TOMBOL BATAL + MASUK
                // ============================================================
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // ---------- TOMBOL BATAL ----------
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isFocusedBatal) Color(0xFF1F3A52)
                                else Color(0xFF142735)
                            )
                            .border(
                                width = if (isFocusedBatal) 3.dp else 0.dp,
                                color = if (isFocusedBatal) IslamicGoldLight else Color.Transparent,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .focusable()
                            .onFocusChanged { isFocusedBatal = it.isFocused }
                            .clickable { onDismiss() }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "BATAL",
                            color = if (isFocusedBatal) IslamicGoldLight else TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    // ---------- TOMBOL MASUK ----------
                    val canSubmit = pin.length == 6
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                when {
                                    !canSubmit -> Color(0x33FFD700)
                                    isFocusedMasuk -> Color(0xFFFFE44D)
                                    else -> IslamicGold
                                }
                            )
                            .border(
                                width = if (isFocusedMasuk && canSubmit) 3.dp else 0.dp,
                                color = if (isFocusedMasuk && canSubmit) Color.White else Color.Transparent,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .focusable(enabled = canSubmit)
                            .onFocusChanged { isFocusedMasuk = it.isFocused }
                            .clickable(enabled = canSubmit) {
                                if (pin == correctPin) {
                                    onSuccess()
                                } else {
                                    errorMessage = "PIN salah"
                                    pin = ""
                                }
                            }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "MASUK",
                            color = if (canSubmit) Color(0xFF09141D) else TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}
