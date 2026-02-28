package com.servicerca.app.core.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.servicerca.app.ui.dashboard.moderador.ModeratorPanelScreen

@Composable
fun ModeratorNavigation(
    navController: NavHostController,
    padding: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = DashboardRoutes.HomeModerator,
        modifier = Modifier.padding(padding)
    ) {

        composable<DashboardRoutes.HomeModerator> {
            ModeratorPanelScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable<DashboardRoutes.ProfileModerator> {
            // Pantalla perfil moderador
        }

        composable<DashboardRoutes.Historial> {
            // Pantalla historial
        }
    }
}