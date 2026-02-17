package com.servicerca.app.ui.dashboard.user

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.servicerca.app.core.components.navigation.AppTopAppBar
import com.servicerca.app.core.components.navigation.BottomNavigationBar
import com.servicerca.app.core.navigation.UserNavigation


@Composable
fun UserScreen(
    onLogout: () -> Unit
) {

    // Estados para la navegación y el título de la barra superior
    val navController = rememberNavController()
    var title by remember { mutableStateOf("Inicio usuario") }

    // Estructura Scaffold (barra superior, barra inferior y contenido)
    Scaffold(
        topBar = {
            // Barra superior con título y botón de cierre de sesión
            AppTopAppBar(
                title = title,
                logout = onLogout // Función para cerrar sesión, que se pasa desde el componente padre
            )
        },
        bottomBar = {
            // Barra de navegación inferior con iconos y títulos
            BottomNavigationBar(
                navController = navController,
                titleTopBar = {
                    title = it
                }
            )
        }
    ) { padding ->
        // Contenido principal gestionado por la navegación (NavHost)
        UserNavigation(
            navController = navController,
            padding = padding
        )

    }
}

@Composable
fun HomeUserScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Bienvenido usuario 👋")
    }
}


@Composable
@Preview
fun userScreenPreview(){
    UserScreen( onLogout = {})
}


