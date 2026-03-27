package com.servicerca.app.ui.auth.register

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.servicerca.app.core.components.button.PrimaryButton
import com.servicerca.app.core.components.input.OtpTextField
import com.servicerca.app.core.utils.RequestResult
import com.servicerca.app.ui.theme.Error
import com.servicerca.app.ui.theme.PrimaryLight

@Composable
fun VerifyEmailScreen(
    email: String,
    onNavigateToLogin: () -> Unit,
    viewModel: VerifyEmailViewModel = viewModel()
) {
    val otpCode by viewModel.otpCode.collectAsState()
    val verifyResult by viewModel.verifyResult.collectAsState()
    val resendResult by viewModel.resendResult.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // true = error (rojo), false = éxito (verde/primary)
    var isError by remember { mutableStateOf(false) }

    // Resultado de la verificación del OTP
    LaunchedEffect(verifyResult) {
        when (val result = verifyResult) {
            is RequestResult.Success -> {
                isError = false
                snackbarHostState.showSnackbar(result.message)
                viewModel.resetVerifyResult()
                onNavigateToLogin()
            }
            is RequestResult.Failure -> {
                isError = true
                snackbarHostState.showSnackbar(result.errorMessage)
                viewModel.resetVerifyResult()
            }
            null -> Unit
        }
    }

    // Resultado del reenvío de correo
    LaunchedEffect(resendResult) {
        when (val result = resendResult) {
            is RequestResult.Success -> {
                isError = false
                snackbarHostState.showSnackbar(result.message)
                viewModel.resetResendResult()
            }
            is RequestResult.Failure -> {
                isError = true
                snackbarHostState.showSnackbar(result.errorMessage)
                viewModel.resetResendResult()
            }
            null -> Unit
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = if (isError) Error else PrimaryLight,
                    contentColor = Color.White
                )
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 48.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Ícono de email
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .shadow(
                        elevation = 16.dp,
                        shape = CircleShape,
                        ambientColor = Color.Cyan,
                        spotColor = MaterialTheme.colorScheme.primary
                    )
                    .clip(CircleShape)
                    .background(Color.Cyan),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Revisa tu correo",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Enviamos instrucciones a $email",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Campo OTP conectado al ViewModel
            OtpTextField(
                otpValue = otpCode,
                onOtpChange = { viewModel.onOtpChanged(it) },
                onOtpComplete = { viewModel.onOtpChanged(it) },
                otpLength = 6
            )

            Spacer(modifier = Modifier.height(32.dp))

            PrimaryButton(
                text = "Ir a inicio de sesión",
                onClick = { viewModel.verifyEmail() }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Reenviar correo
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "¿No recibiste el correo?",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Reenviar",
                    color = MaterialTheme.colorScheme.primary,
                    textDecoration = TextDecoration.Underline,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.clickable { viewModel.resendEmail() }
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun VerifyEmailScreenPreview() {
    VerifyEmailScreen(
        email = "juanPerez@example.com",
        onNavigateToLogin = {}
    )
}