package com.servicerca.app.ui.auth.login.Reset

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.servicerca.app.core.components.button.PrimaryButton
import com.servicerca.app.core.components.input.AppPasswordField
import com.servicerca.app.core.components.input.AppTextField
import com.servicerca.app.core.utils.RequestResult
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPassword(
    viewModel: ResetPasswordViewModel = viewModel(),
    onNavigateToLogin: () -> Unit,
    onBackClick: () -> Unit
) {
    // Estado para el icono de visibilizacion de contraseña
    var passwordVisible by remember { mutableStateOf(false) }
    val snackBarHostState = remember { SnackbarHostState() }
    val resetResult by viewModel.resetResult.collectAsState()

    LaunchedEffect(resetResult) {
        resetResult?.let { result ->
            val message = when(result){
                is RequestResult.Success -> result.message
                is RequestResult.Failure -> result.errorMessage
            }

            // Muestra el snackbar y espera a que se oculte o descarte
            snackBarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )

            // Si el login fue exitoso, esperamos un momento para que el usuario vea el mensaje y navegamos
            if (result is RequestResult.Success) {
                delay(300)

                onNavigateToLogin()

            }

            // Limpiamos el resultado en el ViewModel para evitar que el efecto se dispare de nuevo innecesariamente
            viewModel.resetResult()
        }
    }
    Scaffold(
        snackbarHost = {
            SnackbarHost(snackBarHostState) { data ->
                // Personalización del color según el tipo de resultado
                // Nota: Usamos una variable local para capturar el estado actual del error antes de que se resetee
                val isError = resetResult is RequestResult.Failure
                Snackbar(
                    containerColor = if(isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ) {
                    Text(data.visuals.message)
                }
            }
        },
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            // — Ícono decorativo —
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LockReset,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // — Título —
            Text(
                text = "Restablecer contraseña",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // — Descripción —
            Text(
                text = "Introduce el código de recuperación que recibiste y define tu nueva contraseña.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            // — Campo: Código de recuperación —
            Text(
                text = "Código de recuperación",
                style = MaterialTheme.typography.labelMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            AppTextField(
                value = viewModel.codeReset.value,
                onValueChange = { viewModel.codeReset.onChange(it) },
                placeholder = "Ej: 123456",
                modifier = Modifier
                    .fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                isError = viewModel.codeReset.error != null,
                supportingText = viewModel.codeReset.error?.let { error ->
                    { Text(text = error) }
                },


            )

            Spacer(modifier = Modifier.height(16.dp))

            // — Campo: Nueva contraseña —
            Text(
                text = "Nueva contraseña",
                style = MaterialTheme.typography.labelMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            AppTextField(
                value = viewModel.newPassword.value,
                onValueChange = { viewModel.newPassword.onChange(it) },
                placeholder = "Mínimo 8 caracteres",
                isPassword = !passwordVisible,
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff
                                          else Icons.Default.Visibility,
                            contentDescription = if (passwordVisible) "Ocultar contraseña"
                                                 else "Mostrar contraseña"
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                isError = viewModel.newPassword.error != null,
                supportingText = viewModel.newPassword.error?.let { error ->
                    { Text(text = error) }
                },
            )

            Spacer(modifier = Modifier.height(32.dp))

            // — Botón de acción —
            PrimaryButton(
                text = "Restablecer contraseña",
                onClick = {viewModel.reset()},
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // — Link: reenviar código —
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "¿No te llegó el código? ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Reenviar",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable(onClick = { /* TODO: lógica de reenvío */ })
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ——— Funciones de validación ———

/**
 * Valida que el código de restablecimiento tenga exactamente 6 caracteres
 * y esté compuesto únicamente por letras y dígitos (alfanumérico).
 *
 * @param code El código ingresado por el usuario.
 * @return Un mensaje de error si el código no es válido, o null si es correcto.
 */
fun validateResetCode(code: String): String? {
    return when {
        code.isBlank()                              -> "El código no puede estar vacío"
        code.length != 6                            -> "El código debe tener exactamente 6 caracteres"
        !code.matches(Regex("^[a-zA-Z0-9]{6}$"))   -> "El código solo puede contener letras y números"
        else                                        -> null
    }
}

/**
 * Valida que la nueva contraseña cumpla los requisitos mínimos de seguridad:
 * - No puede estar vacía
 * - Mínimo 8 caracteres
 * - Al menos una letra mayúscula
 * - Al menos una letra minúscula
 * - Al menos un dígito
 *
 * @param password La contraseña ingresada por el usuario.
 * @return Un mensaje de error si no es válida, o null si cumple todos los requisitos.
 */
fun validatePassword(password: String): String? {
    return when {
        password.isBlank()                 -> "La contraseña no puede estar vacía"
        password.length < 8                -> "La contraseña debe tener al menos 8 caracteres"
        !password.any { it.isUpperCase() } -> "Debe contener al menos una letra mayúscula"
        !password.any { it.isLowerCase() } -> "Debe contener al menos una letra minúscula"
        !password.any { it.isDigit() }     -> "Debe contener al menos un número"
        else                               -> null
    }
}

// ——— Preview ———

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun ResetPasswordPreview() {
    ResetPassword(
        onNavigateToLogin = {},
        onBackClick = {}
    )
}