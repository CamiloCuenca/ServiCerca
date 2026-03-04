package com.servicerca.app.ui.auth.register

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.servicerca.app.R
import com.servicerca.app.core.components.button.PrimaryButton
import com.servicerca.app.core.components.button.SocialButton
import com.servicerca.app.core.components.input.AppPasswordField
import com.servicerca.app.core.components.input.AppTextField
import com.servicerca.app.ui.auth.login.LoginViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onBackClick: () -> Unit,
    onVerifyEmail: () -> Unit,
    viewModel: RegisterViewModel = viewModel(),




    ) {


    var expanded by remember { mutableStateOf(false) }
    val opciones = listOf("Ciudad 1", "Ciudad 2", "Ciudad 3")

    var selectedOption by remember { mutableStateOf(opciones[0]) }



    Scaffold(
        modifier = Modifier.fillMaxSize()
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

            // Nombres
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ){
                Row(
                modifier = Modifier.
                padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Column(modifier = Modifier
                    .weight(1F)) {
                AppTextField(
                    value = viewModel.name.value,
                    onValueChange = { viewModel.name.onChange(it) },
                    label = stringResource(R.string.register_label_first_name),
                    placeholder = stringResource(R.string.register_placeholder_example_name)
                )

                }
                Column(modifier = Modifier
                    .weight(1F)) {  AppTextField(
                    value = viewModel.SecondName.value,
                    onValueChange = { viewModel.SecondName.onChange(it) },
                    label = stringResource(R.string.register_label_second_name),
                    placeholder = stringResource(R.string.register_placeholder_example_name)
                ) }


                }

                // Apellidos
                Row( modifier = Modifier.
                padding(bottom = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)){

                    Column(modifier = Modifier
                        .weight(1F)){
                        AppTextField(
                            value = viewModel.Lastname.value,
                            onValueChange = { viewModel.Lastname.onChange(it) },
                            label = stringResource(R.string.register_label_first_lastname),
                            placeholder = stringResource(R.string.register_placeholder_example_name)
                        )
                    }

                    Column(modifier = Modifier
                        .weight(1F)){
                        AppTextField(
                            value = viewModel.SecondLastname.value,
                            onValueChange = { viewModel.SecondLastname.onChange(it) },
                            label = stringResource(R.string.register_label_second_lastname),
                            placeholder = stringResource(R.string.register_placeholder_example_name)
                        )
                    }
                }

            }

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {

                TextField(
                    value = selectedOption,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF8F8F8),
                        unfocusedContainerColor = Color(0xFFF0F0F0),
                        focusedIndicatorColor = Color(0xFF6C63FF),
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)     )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    containerColor = Color.White,
                    shadowElevation = 10.dp,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    opciones.forEach { option ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    option,
                                    fontWeight = FontWeight.Medium
                                )
                            },
                            onClick = {
                                selectedOption = option
                                expanded = false
                            }
                        )
                    }
                }
            }

            AppTextField(
                value = viewModel.address.value,
                onValueChange = { viewModel.address.onChange(it)},
                label = "Dirección",
                placeholder = "Dirección"
            )

            // Corro y contraseña
            AppTextField(
                value = viewModel.email.value,
                onValueChange = { viewModel.email.onChange(it)},
                label = stringResource(R.string.emailLabel),
                placeholder = stringResource(R.string.placeholderEmail),
            )

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


            // Botón principal
            PrimaryButton(
                text = stringResource(R.string.registrarse),
                onClick = { onVerifyEmail() }
            )

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
                    modifier = Modifier.fillMaxWidth()
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