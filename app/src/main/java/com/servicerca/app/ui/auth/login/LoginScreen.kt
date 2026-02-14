package com.servicerca.app.ui.auth.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.servicerca.app.core.components.button.PrimaryButton
import com.servicerca.app.core.components.input.AppPasswordField
import com.servicerca.app.core.components.input.AppTextField
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.OutlinedButton
import androidx.compose.foundation.shape.RoundedCornerShape
import com.servicerca.app.R
import com.servicerca.app.core.components.button.SocialButton


@Composable
fun LoginScreen() {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Column {

            Spacer(modifier = Modifier.height(40.dp))

            Image(
                painter = painterResource(id = R.drawable.logo_servicerca),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(72.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.nameApp),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Text(
                text = stringResource(R.string.loginSubTitle),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            AppTextField(
                value = email,
                onValueChange = { email = it },
                label = stringResource(R.string.emailLabel),
                placeholder = stringResource(R.string.placeholderEmail),
                keyboardType = KeyboardType.Email
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppPasswordField(
                password = password,
                onPasswordChange = { password = it },
                label = stringResource(R.string.passwordLabel)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // TODO cambiar para que sea un hypervinculo
            Text(
                text = stringResource(R.string.login_forgot_password),
                modifier = Modifier.align(Alignment.End)
            )

            Spacer(modifier = Modifier.height(24.dp))

            PrimaryButton(
                text = stringResource(R.string.login_iniciar_secion),
                onClick = { /* login */ }
            )

            Spacer(modifier = Modifier.height(24.dp))

            DividerWithText(stringResource(R.string.login_continuar_con))

            Spacer(modifier = Modifier.height(16.dp))

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
        }

        // Parte inferior
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = stringResource(R.string.login_no_account))
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = { /* navegar a registro */ },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(stringResource(R.string.login_create_account))
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}



@Preview (showBackground = true , showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}


@Composable
fun DividerWithText(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f))
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        HorizontalDivider(modifier = Modifier.weight(1f))
    }
}
