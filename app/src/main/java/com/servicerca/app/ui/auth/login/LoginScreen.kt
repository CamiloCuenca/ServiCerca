package com.servicerca.app.ui.auth.login

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.servicerca.app.R
import com.servicerca.app.core.components.button.OutlineButton
import com.servicerca.app.core.components.button.PrimaryButton
import com.servicerca.app.core.components.button.SocialButton
import com.servicerca.app.core.components.input.AppPasswordField
import com.servicerca.app.core.components.input.AppTextField
import com.servicerca.app.core.utils.RequestResult
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onNavigateToRegister: () -> Unit,
    onNavigateToUsers: () -> Unit,
    onRecoverPassword: () -> Unit,
    onModeratorPanel: () -> Unit,
    onLoginSuccess: (userId: String, role: com.servicerca.app.domain.model.UserRole) -> Unit
) {
    val scrollState = rememberScrollState()
    val snackBarHostState = remember { SnackbarHostState() }
    val loginResult by viewModel.loginResult.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    // Efecto para manejar los resultados de login (Mostrar Snackbar y Navegar)
    LaunchedEffect(loginResult) {
        loginResult?.let { result ->
            val message = when(result){
                is RequestResult.Success -> result.message
                is RequestResult.SuccessLogin -> "Login exitoso"
                is RequestResult.Failure -> result.errorMessage
            }

            keyboardController?.hide()
            focusManager.clearFocus()
            // Muestra el snackbar y espera a que se oculte o descarte
            snackBarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )

            // Si el login fue exitoso, esperamos un momento para que el usuario vea el mensaje y navegamos
            when (result) {
                is RequestResult.SuccessLogin -> {
                    delay(300)
                    // Llamamos al callback que guardará la sesión en el SessionViewModel
                    Log.d("LoginScreen", "onLoginSuccess invoked with userId=${result.userId}, role=${result.role}")
                    onLoginSuccess(result.userId, result.role)
                }
                is RequestResult.Success -> {
                    delay(300)
                    onNavigateToUsers()
                }
                else -> { /* no-op para Failure ya mostrado */ }
            }

            // Limpiamos el resultado en el ViewModel para evitar que el efecto se dispare de nuevo innecesariamente
            viewModel.resetLoginResult()
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackBarHostState) { data ->
                // Personalización del color según el tipo de resultado
                // Nota: Usamos una variable local para capturar el estado actual del error antes de que se resetee
                val isError = loginResult is RequestResult.Failure
                Snackbar(
                    containerColor = if(isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Text(data.visuals.message)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .imePadding()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(48.dp))

                val logoRes = if (isSystemInDarkTheme()) R.drawable.logo_oscuro else R.drawable.logo_servicerca
                Image(
                    painter = painterResource(id = logoRes),
                    contentDescription = null,
                    modifier = Modifier.size(90.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.nameApp),
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stringResource(R.string.loginSubTitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(32.dp))

                AppTextField(
                    value = viewModel.email.value,
                    onValueChange = { viewModel.email.onChange(it) },
                    label = stringResource(R.string.emailLabel),
                    isError = viewModel.email.error != null,
                    supportingText = viewModel.email.error?.let { error ->
                        { Text(text = error) }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    placeholder = stringResource(R.string.placeholderEmail),
                )

                Spacer(modifier = Modifier.height(16.dp))

                AppPasswordField(
                    password = viewModel.password.value,
                    onPasswordChange = { viewModel.password.onChange(it) },
                    label = stringResource(R.string.passwordLabel)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.login_forgot_password),
                    color = MaterialTheme.colorScheme.primary,
                    textDecoration = TextDecoration.Underline,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .align(Alignment.End)
                        .clickable { onRecoverPassword() }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // BOTÓN DE LOGIN: Ahora llama a viewModel.login() en lugar de navegar directamente
                PrimaryButton(
                    text = stringResource(R.string.login_iniciar_secion),
                    onClick = { viewModel.login() }
                )

                Spacer(modifier = Modifier.height(32.dp))

                DividerWithText(stringResource(R.string.login_continuar_con))

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SocialButton(
                        text = "Google",
                        onClick = {},
                        iconRes = R.drawable.ic_google,
                        modifier = Modifier.weight(1f)
                    )
                    SocialButton(
                        text = "Facebook",
                        onClick = {},
                        iconRes = R.drawable.ic_facebook,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = stringResource(R.string.login_no_account),
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlineButton(
                    text = stringResource(R.string.login_create_account),
                    onClick = onNavigateToRegister,
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

/**
 * Componente visual para mostrar un divisor con texto en medio.
 */
@Composable
fun DividerWithText(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        HorizontalDivider(modifier = Modifier.weight(1f))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(
        onNavigateToRegister = {},
        onNavigateToUsers = {},
        onRecoverPassword = {},
        onModeratorPanel = {},
        onLoginSuccess = { _, _ -> }
    )
}
