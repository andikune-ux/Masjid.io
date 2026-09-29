package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.IslamicGold
import com.example.ui.theme.IslamicGoldLight
import com.example.ui.theme.IslamicGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.UrgentRed

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

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .width(360.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF0C1B26))
                .border(2.dp, IslamicGold, RoundedCornerShape(20.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = IslamicGold,
                modifier = Modifier.size(36.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "PIN KEAMANAN TAKMIR",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = IslamicGoldLight
            )

            Text(
                text = "Masukkan PIN untuk membuka pengaturan / keluar",
                fontSize = 12.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            PinDots(enteredPin = enteredPin, isError = isError)

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
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Batal", color = TextSecondary, fontSize = 14.sp)
            }
        }
    }
}

// ============================================================
// DIALOG 2: ChangePinDialog — untuk mengubah PIN
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

    // Reset input setiap ganti step
    LaunchedEffect(step) {
        enteredPin = ""
        isError = false
        errorMessage = ""
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

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .width(360.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF0C1B26))
                .border(2.dp, IslamicGold, RoundedCornerShape(20.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = IslamicGold,
                modifier = Modifier.size(36.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

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

            // Step indicator
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

            PinDots(enteredPin = enteredPin, isError = isError)

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
                                    // Cek setelah 4 digit
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
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Tombol Kembali (kalau bukan step 1)
                if (step != ChangePinStep.ENTER_OLD) {
                    TextButton(
                        onClick = {
                            step = when (step) {
                                ChangePinStep.ENTER_NEW -> ChangePinStep.ENTER_OLD
                                ChangePinStep.CONFIRM_NEW -> ChangePinStep.ENTER_NEW
                                else -> ChangePinStep.ENTER_OLD
                            }
                        }
                    ) {
                        Text(
                            text = "← Kembali",
                            color = IslamicGold,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                TextButton(onClick = onDismiss) {
                    Text(
                        text = "Batal",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

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
                    .border(
                        1.5.dp,
                        if (isError) UrgentRed else IslamicGold,
                        CircleShape
                    )
            )
        }
    }
}

// ============================================================
// KOMPONEN HELPER: PinPad (Number pad 3x4)
// ============================================================

@Composable
private fun PinPad(
    onKeyPress: (String) -> Unit
) {
    val numpad = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("C", "0", "⌫")
    )

    for (row in numpad) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            for (key in row) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0x33000000))
                        .border(1.dp, Color(0x33FFD700), RoundedCornerShape(10.dp))
                        .clickable { onKeyPress(key) }
                        .focusable(),
                    contentAlignment = Alignment.Center
                ) {
                    if (key == "⌫") {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Backspace,
                            contentDescription = "Hapus",
                            tint = IslamicGold,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text(
                            text = key,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (key == "C") UrgentRed else TextPrimary
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}
