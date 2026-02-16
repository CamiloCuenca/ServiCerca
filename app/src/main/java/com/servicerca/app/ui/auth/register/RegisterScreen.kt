package com.servicerca.app.ui.auth.register

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.servicerca.app.R
import com.servicerca.app.core.components.button.PrimaryButton
import com.servicerca.app.core.components.button.SocialButton
import com.servicerca.app.core.components.input.AppPasswordField
import com.servicerca.app.core.components.input.AppTextField


@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }

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
                    onClick = onNavigateToLogin, // TODO @CamiloCuenca cambiar para que recuerde cual fue lapantalla anteriro (ver guias del profe)
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





            // Subtítulo
            Text(
                text = stringResource(R.string.registration_subtitle),
                fontWeight = FontWeight.Light,
                modifier = Modifier.fillMaxWidth()
            )


            // Formulario
            AppTextField(
                value = name,
                onValueChange = { name = it },
                label = stringResource(R.string.register_label_full_name),
                placeholder = stringResource(R.string.register_placeholder_example_name)
            )

            AppTextField(
                value = email,
                onValueChange = { email = it },
                label = stringResource(R.string.emailLabel),
                placeholder = stringResource(R.string.placeholderEmail),
                keyboardType = KeyboardType.Email
            )

            AppPasswordField(
                password = password,
                onPasswordChange = { password = it },
                label = stringResource(R.string.passwordLabel)
            )

            AppPasswordField(
                password = confirmPassword,
                onPasswordChange = { confirmPassword = it },
                label = stringResource(R.string.register_confirm_password)
            )


            // Botón principal
            PrimaryButton(
                text = stringResource(R.string.registrarse),
                onClick = { /* Registrarse */ }
            )

            // Botones sociales
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                SocialButton(
                    text = "Google",
                    onClick = {},
                    modifier = Modifier.weight(1f)
                )
                SocialButton(
                    text = "Facebook",
                    onClick = {},
                    modifier = Modifier.weight(1f)
                )
            }


            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.register_already_have_account_text))

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
    RegisterScreen( onNavigateToLogin = {})
}