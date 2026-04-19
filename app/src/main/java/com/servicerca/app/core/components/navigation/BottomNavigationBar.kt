package com.servicerca.app.core.components.navigation

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.servicerca.app.R
import com.servicerca.app.core.navigation.DashboardRoutes
import com.servicerca.app.ui.theme.NavigationBarColors

@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    titleTopBar: (String) -> Unit
) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val context = LocalContext.current

    LaunchedEffect(currentDestination) {
        val destination = Destination.entries.find {
            it.route::class.qualifiedName == currentDestination?.route
        }
        destination?.let {
            titleTopBar(context.getString(it.labelRes))
        }
    }

    val (bgColor, contentColor) = NavigationBarColors()

    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 3.dp,
        containerColor = bgColor
    ) {

        Destination.entries.forEach { destination ->

            // Obtener una representación de ruta consistente (qualifiedName) usada al registrar composable<...>
            val routeName = destination.route::class.qualifiedName ?: ""

            val isSelected = currentDestination?.route == routeName

            NavigationBarItem(
                selected = isSelected,

                onClick = {
                    if (routeName.isNotEmpty()) {
                        navController.navigate(routeName) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },

                icon = {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = stringResource(destination.labelRes)
                    )
                },

                label = {
                    Text(stringResource(destination.labelRes))
                },

                alwaysShowLabel = true,

                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.onSecondary,
                    unselectedIconColor = contentColor.copy(alpha = 0.75f),
                    unselectedTextColor = contentColor.copy(alpha = 0.75f)
                )
            )
        }
    }
}

// Items de navegación
enum class Destination(
    val route: DashboardRoutes,
    @StringRes val labelRes: Int,
    val icon: ImageVector,
) {
    CHAT(DashboardRoutes.ChatList, R.string.nav_label_chat, Icons.Default.ChatBubble),
    SEARCH(DashboardRoutes.Search, R.string.nav_label_search, Icons.Default.Search),
    HOME(DashboardRoutes.HomeUser, R.string.nav_label_home, Icons.Default.Home),
    RESERVATION(DashboardRoutes.Reservation, R.string.nav_label_reservations, Icons.Default.CalendarMonth),
    PROFILE(DashboardRoutes.Profile, R.string.nav_label_profile, Icons.Default.AccountCircle),
}



// Moderador
@Composable
fun BottomNavigationBarModerator(
    navController: NavHostController,
    titleTopBar: (String) -> Unit
) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val context = LocalContext.current

    LaunchedEffect(currentDestination) {
        val destination = DestinationModerator.entries.find {
            it.route::class.qualifiedName == currentDestination?.route
        }
        destination?.let {
            titleTopBar(context.getString(it.labelRes))
        }
    }

    val (bgColor, contentColor) = NavigationBarColors()

    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 3.dp,
        containerColor = bgColor
    ) {

        DestinationModerator.entries.forEach { destination ->

            val routeName = destination.route::class.qualifiedName ?: ""

            val isSelected = currentDestination?.route == routeName

            NavigationBarItem(
                selected = isSelected,

                onClick = {
                    if (routeName.isNotEmpty()) {
                        navController.navigate(routeName) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },

                icon = {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = stringResource(destination.labelRes)
                    )
                },

                label = {
                    Text(stringResource(destination.labelRes))
                },

                alwaysShowLabel = true,

                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.onSecondary,
                    unselectedIconColor = contentColor.copy(alpha = 0.75f),
                    unselectedTextColor = contentColor.copy(alpha = 0.75f)
                )
            )
        }
    }
}

enum class DestinationModerator(
    val route: DashboardRoutes,
    @StringRes val labelRes: Int,
    val icon: ImageVector,
) {

    HOME(DashboardRoutes.HomeModerator, R.string.nav_label_home, Icons.Default.Home),
    PROFILE(DashboardRoutes.ProfileModerator, R.string.nav_label_profile, Icons.Default.AccountCircle),
    HISTORIAL(DashboardRoutes.Historial, R.string.nav_label_history, Icons.Default.History)

}