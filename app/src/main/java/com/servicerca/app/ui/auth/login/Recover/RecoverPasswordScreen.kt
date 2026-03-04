package com.servicerca.app.ui.auth.login.Recover

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.servicerca.app.core.components.button.PrimaryButton
import com.servicerca.app.core.utils.RequestResult
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecoverPasswordScreen(
    viewModel: RecoverPasswordViewModel = viewModel(),
    onBackClick: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToResetPassword: () -> Unit
) {


    val snackBarHostState = remember { SnackbarHostState() }
    val recoverResult by viewModel.recoverResult.collectAsState()

    LaunchedEffect(recoverResult) {
        recoverResult?.let { result ->
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

                onNavigateToResetPassword()
            }

            // Limpiamos el resultado en el ViewModel para evitar que el efecto se dispare de nuevo innecesariamente
            viewModel.resetRecoverResult()
        }
    }




    Scaffold(
        snackbarHost = @androidx.compose.runtime.Composable {
            SnackbarHost(snackBarHostState) { data ->
                // Personalización del color según el tipo de resultado
                // Nota: Usamos una variable local para capturar el estado actual del error antes de que se resetee
                val isError = recoverResult is RequestResult.Failure
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
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            // 🔝 Parte superior
            Column {

                Spacer(modifier = Modifier.height(16.dp))

                // Icono decorativo
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

                Text(
                    text = "Recuperar contraseña",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Introduce tu correo electrónico para recibir instrucciones de recuperación",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Correo electrónico",
                    style = MaterialTheme.typography.labelMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = viewModel.email.value,
                    onValueChange = { viewModel.email.onChange(it) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Done
                    ),
                    supportingText = viewModel.email.error?.let { error ->
                        { Text(text = error) }
                    },
                )

                Spacer(modifier = Modifier.height(24.dp))

                PrimaryButton(
                    text = "Enviar instrucciones",
                    onClick = { viewModel.recover()},
                    modifier = Modifier.fillMaxWidth()
                )
            }


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.Center
            ) {

                Text(
                    text = "¿Ya recordaste tu clave? ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "Inicia sesión",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable {
                        onNavigateToLogin()
                    }
                )
            }
        }
    }
}
@Composable
@Preview(showBackground = true, showSystemUi = true)
fun RecoverPasswordScreenPreview() {
    RecoverPasswordScreen(
        onNavigateToLogin = {}
        , onBackClick = {},
        onNavigateToResetPassword = {}
    )
}


