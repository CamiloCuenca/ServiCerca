package com.servicerca.app.core.components.input

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Campo OTP compuesto de [otpLength] cajas individuales.
 *
 * @param otpValue   Valor actual del OTP (manejado externamente).
 * @param onOtpChange Callback que se llama cada vez que el usuario escribe un dígito.
 * @param onOtpComplete Callback que se llama cuando se han ingresado todos los dígitos.
 * @param otpLength  Número de dígitos del OTP (por defecto 6).
 */
@Composable
fun OtpTextField(
    otpValue: String,
    onOtpChange: (String) -> Unit,
    onOtpComplete: (String) -> Unit = {},
    otpLength: Int = 6,
    modifier: Modifier = Modifier
) {
    BasicTextField(
        value = otpValue,
        onValueChange = { newValue ->
            // Solo aceptar dígitos y limitar a otpLength caracteres
            val filtered = newValue.filter { it.isDigit() }.take(otpLength)
            onOtpChange(filtered)
            if (filtered.length == otpLength) {
                onOtpComplete(filtered)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = modifier,
        decorationBox = {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                repeat(otpLength) { index ->
                    val char = otpValue.getOrNull(index)
                    val isCurrent = index == otpValue.length

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(
                                width = if (isCurrent) 2.dp else 1.dp,
                                color = if (isCurrent) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = char?.toString() ?: "",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun OtpTextFieldPreview() {
    OtpTextField(
        otpValue = "123",
        onOtpChange = {}
    )
}
