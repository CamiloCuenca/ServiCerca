package com.servicerca.app.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.servicerca.app.core.components.button.PrimaryButton

@Composable
fun HomeScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit
) {

    val gradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF2EC4B6), // turquesa
            Color(0xFF00B4D8), // azul
            Color(0xFF52B788)  // verde suave
        ),
        start = Offset.Zero,
        end = Offset.Infinite
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {

                Image(
                    painter = painterResource(id = com.servicerca.app.R.drawable.logo_servicerca),
                    contentDescription = null
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(com.servicerca.app.R.string.nameApp),
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Encuentra servicios cerca de ti en segundos",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Light,
                    color = Color.White
                )

            }

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {

                PrimaryButton(
                    text = "Iniciar sesión",
                    onClick = onNavigateToLogin,
                    modifier = Modifier.fillMaxWidth()
                )

                PrimaryButton(
                    text = "Crear cuenta",
                    onClick = onNavigateToRegister,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true )
fun HomeScreenPreview() {
    HomeScreen(
        onNavigateToLogin = {},
        onNavigateToRegister = {}
    )
}
