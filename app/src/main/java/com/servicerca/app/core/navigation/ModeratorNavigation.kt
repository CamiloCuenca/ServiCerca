package com.servicerca.app.core.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.servicerca.app.ui.dashboard.moderador.DetailsVerificationModeratorScreen
import com.servicerca.app.ui.dashboard.moderador.ModerationHistory
import com.servicerca.app.ui.dashboard.moderador.ModeratorPanelScreen
import com.servicerca.app.ui.dashboard.moderador.ProfileModerator
import com.servicerca.app.ui.dashboard.moderador.RejectReasonScreen

@Composable
fun ModeratorNavigation(
    navController: NavHostController,
    padding: PaddingValues,
    onLogout: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = DashboardRoutes.HomeModerator,
        modifier = Modifier.padding(padding)
    ) {

        composable<DashboardRoutes.HomeModerator> {
            ModeratorPanelScreen(
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }
        composable("detailsServicesModerator") {
            DetailsVerificationModeratorScreen(onBack = { navController.popBackStack() }, onRejectClick = { navController.navigate("rejectReason") })

        }
        composable("rejectReason") {
            RejectReasonScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable<DashboardRoutes.ProfileModerator> {
            ProfileModerator(navController = navController,
                onLogout = {
                    // Delegar la acción de logout al callback pasado desde la pantalla raíz
                    onLogout()
                }

            )

        }

        composable<DashboardRoutes.Historial> {
            ModerationHistory()
        }
    }
}