package com.servicerca.app.ui.auth.register

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ContactMail
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
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
    viewModel: RegisterViewModel = hiltViewModel(),

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
                else -> ""
            }

            if (message.toString().isNotEmpty()) {
                snackbarHostState.showSnackbar(message.toString())
            }

            // Navegar a la pantalla de usuarios si el login fue exitoso. Se puede agregar un delay para que el usuario alcance a ver el mensaje
            if (result is RequestResult.Success) {
                delay(1000) // 1 segundo
                onVerifyEmail()
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

            // ── Formulario ────────────────────────────────────────────────
            Text(
                text = "Únete a la comunidad y encuentra los mejores servicios cerca de ti.",
                fontWeight = FontWeight.Light,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Leyenda de campos obligatorios
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = MaterialTheme.colorScheme.error)) { append("*") }
                    append(" Campos obligatorios")
                },
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // ── Sección 1: Datos personales ─────────────────────────────
            FormSectionHeader(icon = Icons.Default.Person, title = "Datos personales")

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Nombres
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(modifier = Modifier.weight(1F)) {
                        AppTextField(
                            value = viewModel.name.value,
                            onValueChange = { viewModel.name.onChange(it) },
                            label = stringResource(R.string.register_label_first_name),
                            placeholder = stringResource(R.string.register_placeholder_example_name),
                            required = true,
                            isError = viewModel.name.error != null,
                            supportingText = viewModel.name.error?.let { { Text(it) } }
                        )

                    }
                    Column(modifier = Modifier.weight(1F)) {
                        AppTextField(
                            value = viewModel.SecondName.value,
                            onValueChange = { viewModel.SecondName.onChange(it) },
                            label = stringResource(R.string.register_label_second_name),
                            placeholder = "Opcional",
                            required = false,
                            isError = viewModel.SecondName.error != null,
                            supportingText = viewModel.SecondName.error?.let { { Text(it) } }
                        )

                    }

                }

                // Apellidos
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(modifier = Modifier.weight(1F)) {
                        AppTextField(
                            value = viewModel.Lastname.value,
                            onValueChange = { viewModel.Lastname.onChange(it) },
                            label = stringResource(R.string.register_label_first_lastname),
                            placeholder = stringResource(R.string.register_placeholder_example_name),
                            required = true,
                            isError = viewModel.Lastname.error != null,
                            supportingText = viewModel.Lastname.error?.let { { Text(it) } }
                        )

                    }
                    Column(modifier = Modifier.weight(1F)) {
                        AppTextField(
                            value = viewModel.SecondLastname.value,
                            onValueChange = { viewModel.SecondLastname.onChange(it) },
                            label = stringResource(R.string.register_label_second_lastname),
                            placeholder = stringResource(R.string.register_placeholder_example_name),
                            required = true,
                            isError = viewModel.SecondLastname.error != null,
                            supportingText = viewModel.SecondLastname.error?.let { { Text(it) } }
                        )

                    }


                }
            }

            // ── Sección 2: Contacto ──────────────────────────────────────
            FormSectionHeader(icon = Icons.Default.ContactMail, title = "Contacto")

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AppTextField(
                    value = viewModel.email.value,
                    onValueChange = { viewModel.email.onChange(it) },
                    label = stringResource(R.string.emailLabel),
                    placeholder = stringResource(R.string.placeholderEmail),
                    required = true,
                    isError = viewModel.email.error != null,
                    supportingText = viewModel.email.error?.let { { Text(it) } }
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
                    onValueChange = { viewModel.address.onChange(it) },
                    label = "Dirección",
                    placeholder = "Dirección de servicio",
                    required = true,
                    isError = viewModel.address.error != null,
                    supportingText = viewModel.address.error?.let { { Text(it) } }
                )

                AppTextField(
                    value = viewModel.city.value,
                    onValueChange = { viewModel.city.onChange(it) },
                    label = stringResource(R.string.register_label_city),
                    placeholder = "Ciudad de residencia",
                    required = true,
                    isError = viewModel.city.error != null,
                    supportingText = viewModel.city.error?.let { { Text(it) } }
                )

            }


            // ── Sección 3: Seguridad ─────────────────────────────────────
            FormSectionHeader(icon = Icons.Default.Lock, title = "Seguridad")

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AppPasswordField(
                    password = viewModel.password.value,
                    onPasswordChange = { viewModel.password.onChange(it) },
                    label = stringResource(R.string.passwordLabel),
                    required = true
                )
                if (viewModel.password.error != null) {
                    Text(
                        text = viewModel.password.error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }


                AppPasswordField(
                    password = viewModel.confirmPassword.value,
                    onPasswordChange = { viewModel.confirmPassword.onChange(it) },
                    label = stringResource(R.string.register_confirm_password),
                    required = true
                )
                if (viewModel.confirmPassword.error != null) {
                    Text(
                        text = viewModel.confirmPassword.error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }

            }

            Spacer(modifier = Modifier.height(4.dp))

            // ── Botón principal ───────────────────────────────────────────
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

/**
 * Encabezado visual de sección del formulario.
 * Muestra un ícono y título en negrita seguidos de un divisor sutil.
 */
@Composable
private fun FormSectionHeader(icon: ImageVector, title: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant,
            thickness = 1.dp
        )
        Spacer(modifier = Modifier.height(12.dp))
    }
}
