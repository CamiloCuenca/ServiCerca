package com.servicerca.app.core.components.navigation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.servicerca.app.core.navigation.DashboardRoutes

@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    titleTopBar: (String) -> Unit
){
    // Obtener la entrada actual de la pila de navegación
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Actualizar el título de la barra superior según la pantalla actual
    LaunchedEffect(currentDestination) {
        val destination = Destination.entries.find { it.route::class.qualifiedName == currentDestination?.route }
        if (destination != null) {
            titleTopBar(destination.label)
        }
    }

    // Crear la barra de navegación inferior
    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
    ){
        // Iteramos cada item de navegación definido en Destination
        Destination.entries.forEachIndexed { index, destination ->

            // Verificar si el item está seleccionado
            val isSelected = currentDestination?.route == destination.route::class.qualifiedName

            NavigationBarItem(
                label = {
                    // Etiqueta del item de navegación
                    Text(
                        text = destination.label
                    )
                },
                onClick = {
                    // Navegar a la ruta correspondiente al item seleccionado
                    navController.navigate(destination.route){
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    // Icono del item de navegación
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = destination.label
                    )
                },
                selected = isSelected
            )
        }
    }
}

// Definición de los items de navegación de la barra inferior
enum class Destination(
    val route: DashboardRoutes,
    val label: String,
    val icon: ImageVector,
){
    CHAT(DashboardRoutes.Chat, "Chat", Icons.Default.ChatBubble),
    SEARCH(DashboardRoutes.Search, "Buscar", Icons.Default.Search),
    HOME(DashboardRoutes.HomeUser, "Inicio", Icons.Default.Home ),
    RESERVATION(DashboardRoutes.Reservation, "Reservas", Icons.Default.CalendarMonth),
    PROFILE(DashboardRoutes.Profile, "Perfil", Icons.Default.AccountCircle),


}