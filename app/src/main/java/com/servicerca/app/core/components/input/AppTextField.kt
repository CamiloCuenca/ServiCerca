package com.servicerca.app.core.components.input

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Campo de texto personalizado que se adapta a diferentes tipos de entrada, como texto normal, correo electrónico o contraseña.
 */
@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "",
    placeholder: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    singleLine: Boolean = true,
    leadingIcon: (@Composable (() -> Unit))? = null,
    trailingIcon: (@Composable (() -> Unit))? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = if (label.isNotEmpty()) {
            { Text(label) }
        } else null,
        placeholder = if (placeholder != null) {
            { Text(placeholder) }
        } else null,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        singleLine = singleLine,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon
    )
}



/**
 * Campo de texto especializado para contraseñas, con opción de mostrar/ocultar el texto ingresado.
 */
@Composable
fun AppPasswordField(
    password: String,
    onPasswordChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "",
    placeholder: String? = "••••••••",
    singleLine: Boolean = true,
    leadingIcon: (@Composable (() -> Unit))? = null,
    showVisibilityToggle: Boolean = true
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        label = if (label.isNotEmpty()) {
            { Text(label) }
        } else null,
        singleLine = singleLine,
        placeholder = if (placeholder != null) {
            { Text(placeholder) }
        } else null,
        leadingIcon = leadingIcon,
        trailingIcon = if (showVisibilityToggle) {
            {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña")
                }
            }
        } else null,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun OtpTextField(
    otpLength: Int = 6,
    onOtpComplete: (String) -> Unit
) {
    var otpValues by remember { mutableStateOf(List(otpLength) { "" }) }
    val focusRequesters = remember { List(otpLength) { FocusRequester() } }

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        otpValues.forEachIndexed { index, value ->
            BasicTextField(
                value = value,
                onValueChange = { newValue ->
                    // Solo acepta un dígito
                    val digit = newValue.filter { it.isDigit() }.takeLast(1)
                    val newOtp = otpValues.toMutableList()
                    newOtp[index] = digit
                    otpValues = newOtp

                    // Avanza al siguiente campo automáticamente
                    if (digit.isNotEmpty() && index < otpLength - 1) {
                        focusRequesters[index + 1].requestFocus()
                    }

                    // Si completó todos los campos
                    if (newOtp.all { it.isNotEmpty() }) {
                        onOtpComplete(newOtp.joinToString(""))
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .size(48.dp)
                    .focusRequester(focusRequesters[index])
                    .onKeyEvent { event ->
                        // Retrocede al campo anterior con backspace
                        if (event.key == Key.Backspace && value.isEmpty() && index > 0) {
                            focusRequesters[index - 1].requestFocus()
                            val newOtp = otpValues.toMutableList()
                            newOtp[index - 1] = ""
                            otpValues = newOtp
                            true
                        } else false
                    }
                    .border(
                        width = 1.5.dp,
                        color = if (value.isNotEmpty())
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(12.dp)
                    ),
                decorationBox = { innerTextField ->
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (value.isEmpty()) {
                            Text(
                                text = "0",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.outline,
                                textAlign = TextAlign.Center
                            )
                        }
                        innerTextField()
                    }
                },
                textStyle = MaterialTheme.typography.titleLarge.copy(
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                singleLine = true
            )
        }
    }
}