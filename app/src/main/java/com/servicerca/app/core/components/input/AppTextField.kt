package com.servicerca.app.core.components.input

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.withStyle
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
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    isPassword: Boolean = false,
    singleLine: Boolean = true,
    leadingIcon: (@Composable (() -> Unit))? = null,
    trailingIcon: (@Composable (() -> Unit))? = null,
    isError: Boolean = false,
    supportingText: (@Composable (() -> Unit))? = null,
    required: Boolean = false
) {
    val errorColor = MaterialTheme.colorScheme.error
    val labelComposable: (@Composable () -> Unit)? = if (label.isNotEmpty()) {
        {
            if (required) {
                Text(
                    text = buildAnnotatedString {
                        append(label)
                        append(" ")
                        withStyle(SpanStyle(color = errorColor)) { append("*") }
                    }
                )
            } else {
                Text(label)
            }
        }
    } else null

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = labelComposable,
        placeholder = placeholder?.let { { Text(it) } },
        keyboardOptions = keyboardOptions,
        visualTransformation = if (isPassword)
            PasswordVisualTransformation()
        else
            VisualTransformation.None,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        singleLine = singleLine,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        isError = isError,
        supportingText = supportingText
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
    showVisibilityToggle: Boolean = true,
    required: Boolean = false
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val errorColor = MaterialTheme.colorScheme.error
    val labelComposable: (@Composable () -> Unit)? = if (label.isNotEmpty()) {
        {
            if (required) {
                Text(
                    text = buildAnnotatedString {
                        append(label)
                        append(" ")
                        withStyle(SpanStyle(color = errorColor)) { append("*") }
                    }
                )
            } else {
                Text(label)
            }
        }
    } else null

    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        label = labelComposable,
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

