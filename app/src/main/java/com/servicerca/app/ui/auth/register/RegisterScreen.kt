package com.servicerca.app.ui.auth.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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

// TODO @CamiloCuenca me falta poner los Strings en el value strings.xml
@Composable
fun RegisterScreen() {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }


    Column( modifier = Modifier
        .fillMaxSize()
        .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween)
    {




        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_servicerca),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(35.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = stringResource(R.string.nameApp),
                style = MaterialTheme.typography.headlineMedium,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(text="Paso 1 de 2",fontWeight = FontWeight.Light, fontSize = 15.sp)
        }

        Text(
            text = "Crea una cuenta",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            fontWeight = FontWeight.Bold,

            )

        Text(text = "Únete a la comunidad y encuentra los mejores servicios cerca de ti.",
            fontWeight = FontWeight.Light
        )

        AppTextField(
            value = name,
            onValueChange = {name = it},
            label = "Nombre Completo",
            placeholder = "Ej. Juan Pérez"
        )

        AppTextField(
            value = email,
            onValueChange = { email = it },
            label = stringResource(R.string.emailLabel),
            placeholder = stringResource(R.string.placeholderEmail) ,
            keyboardType = KeyboardType.Email
        )

        AppPasswordField(
            password = password,
            onPasswordChange = { password = it },
            label = stringResource(R.string.passwordLabel)
        )

        AppPasswordField(
            password = password,
            onPasswordChange = { password = it },
            label = stringResource(R.string.register_confirm_password)
        )

        Text(
            text = "Selecionar Ubicación",
            fontWeight = FontWeight.Bold,
        )
        //TODO Ver como se pone el boton para solicitar los permios de gps para la app

        Text(
            text = "Necesitamos esto para mostrarte proveedores cercanos.",
            fontWeight = FontWeight.Light,
        )

        PrimaryButton(
            text = stringResource(R.string.registrarse),
            onClick = { /* Registrarse */ }
        )

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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "¿Ya tienes una cuenta? Inicia sesión")
            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = "Al registrarte, aceptas nuestros Términos de Servicio \ny Politica de Privacidad",
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

        }
    }
}



@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen()
}