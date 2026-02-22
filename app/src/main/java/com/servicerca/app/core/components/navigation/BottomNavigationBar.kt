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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.servicerca.app.core.navigation.DashboardRoutes

@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    titleTopBar: (String) -> Unit
) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    LaunchedEffect(currentDestination) {
        val destination = Destination.entries.find {
            it.route::class.qualifiedName == currentDestination?.route
        }
        destination?.let {
            titleTopBar(it.label)
        }
    }

    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 3.dp
    ) {

        Destination.entries.forEach { destination ->

            val isSelected =
                currentDestination?.route ==
                        destination.route::class.qualifiedName

            NavigationBarItem(
                selected = isSelected,

                onClick = {
                    navController.navigate(destination.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },

                icon = {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = destination.label
                    )
                },

                label = {
                    Text(destination.label)
                },


                alwaysShowLabel = true
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