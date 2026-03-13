package com.servicerca.app.ui.auth.register

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.servicerca.app.R
import com.servicerca.app.core.components.button.PrimaryButton
import com.servicerca.app.core.components.button.SocialButton
import com.servicerca.app.core.components.input.AppExposedDropdownMenu
import com.servicerca.app.core.components.input.AppPasswordField
import com.servicerca.app.core.components.input.AppTextField
import com.servicerca.app.core.utils.RequestResult
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onBackClick: () -> Unit,
    onVerifyEmail: () -> Unit,
    viewModel: RegisterViewModel = viewModel(),

    ) {

    val opciones = listOf("Hogar", "Tecnología", "Mecánica", "Salud", "Belleza")
    val snackbarHostState = remember { SnackbarHostState() }
    val registerResult by viewModel.registerResult.collectAsState()


    // Efecto para mostrar el snackbar cuando hay resultado
    LaunchedEffect(registerResult) {
        registerResult?.let { result ->
            // Obtener el mensaje según el resultado
            val message = when (result) {
                is RequestResult.Success -> result.message
                is RequestResult.Failure -> result.errorMessage
            }
            snackbarHostState.showSnackbar(message) // Mostrar el snackbar con el mensaje

            // Navegar a la pantalla de usuarios si el login fue exitoso. Se puede agregar un delay para que el usuario alcance a ver el mensaje
            if (result is RequestResult.Success) {
                delay(1000) // 2 segundos
                onNavigateToLogin()
            }

            // Reseta el estado del loginResult en el ViewModel después de mostrar el mensaje
            viewModel.resetLoginResult()
        }
    }


    Scaffold(
        modifier = Modifier.fillMaxSize(),

        snackbarHost = {
            // Mostrar el SnackbarHost para gestionar los snackbars. Un SnackbarHost es un contenedor que muestra los snackbars.
            SnackbarHost(snackbarHostState) { data ->
                val isError = registerResult is RequestResult.Failure
                // Mostrar el Snackbar con el estilo adecuado según si es error o éxito
                Snackbar(
                    containerColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ) {
                    Text(data.visuals.message)
                }
            }
        }

    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {


                IconButton(
                    onClick = onBackClick, 
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver"
                    )
                }


                Text(
                    text = stringResource(R.string.register_create_an_account),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center)
                )


            }

            // Formulario
            Text(
                text = "Únete a la comunidad y encuentra los mejores\n" +
                        "servicios cerca de ti.",
                fontWeight = FontWeight.Light,

                )

            // --- Sección 1: Datos Personales ---
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Nombres
                Row(
                    modifier = Modifier.padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(modifier = Modifier.weight(1F)) {
                        AppTextField(
                            value = viewModel.name.value,
                            onValueChange = { viewModel.name.onChange(it) },
                            label = stringResource(R.string.register_label_first_name),
                            placeholder = stringResource(R.string.register_placeholder_example_name)
                        )
                    }
                    Column(modifier = Modifier.weight(1F)) {
                        AppTextField(
                            value = viewModel.SecondName.value,
                            onValueChange = { viewModel.SecondName.onChange(it) },
                            label = stringResource(R.string.register_label_second_name),
                            placeholder = stringResource(R.string.register_placeholder_example_name)
                        )
                    }
                }

                // Apellidos
                Row(
                    modifier = Modifier.padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(modifier = Modifier.weight(1F)) {
                        AppTextField(
                            value = viewModel.Lastname.value,
                            onValueChange = { viewModel.Lastname.onChange(it) },
                            label = stringResource(R.string.register_label_first_lastname),
                            placeholder = stringResource(R.string.register_placeholder_example_name)
                        )
                    }
                    Column(modifier = Modifier.weight(1F)) {
                        AppTextField(
                            value = viewModel.SecondLastname.value,
                            onValueChange = { viewModel.SecondLastname.onChange(it) },
                            label = stringResource(R.string.register_label_second_lastname),
                            placeholder = stringResource(R.string.register_placeholder_example_name)
                        )
                    }
                }
            }

            // --- Sección 2: Contacto y Detalles ---
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AppTextField(
                    value = viewModel.email.value,
                    onValueChange = { viewModel.email.onChange(it)},
                    label = stringResource(R.string.emailLabel),
                    placeholder = stringResource(R.string.placeholderEmail),
                )

                AppExposedDropdownMenu(
                    label = "Categoría",
                    options = opciones,
                    selectedOption = viewModel.category.value,
                    onOptionSelected = { viewModel.category.onChange(it) },
                    errorMessage = viewModel.category.error
                )

                AppTextField(
                    value = viewModel.address.value,
                    onValueChange = { viewModel.address.onChange(it)},
                    label = "Dirección",
                    placeholder = "Dirección de servicio"
                )
            }

            Spacer(modifier = Modifier.padding(4.dp))

            // --- Sección 3: Seguridad ---
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AppPasswordField(
                    password = viewModel.password.value,
                    onPasswordChange = { viewModel.password.onChange(it)},
                    label = stringResource(R.string.passwordLabel)
                )

                AppPasswordField(
                    password = viewModel.confirmPassword.value,
                    onPasswordChange = { viewModel.confirmPassword.onChange(it)},
                    label = stringResource(R.string.register_confirm_password)
                )
            }

            Spacer(modifier = Modifier.padding(8.dp))

            // -- Botón principal --
            PrimaryButton(
                text = stringResource(R.string.registrarse),
                onClick = { viewModel.register() },
                modifier = Modifier.fillMaxWidth()
            )

            // -- Divisor Social --
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                )
                Text(
                    text = "O regístrate con",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Divider(
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                )
            }

            // Botones sociales
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


            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = stringResource(R.string.register_already_have_account_text))
                    Text(
                        text = "Iniciar sesión",
                        color = MaterialTheme.colorScheme.primary,
                        textDecoration = TextDecoration.Underline,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.clickable { onNavigateToLogin() }
                    )
                }

                Text(
                    text = stringResource(R.string.register_terms_and_privacy_notice_text),
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.Center,
                    fontSize = 10.sp,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
                )
            }


        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen( onNavigateToLogin = {} , onBackClick = {}, onVerifyEmail = {})
}
