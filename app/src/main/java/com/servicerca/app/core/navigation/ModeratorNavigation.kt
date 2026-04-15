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
import com.servicerca.app.ui.dashboard.moderador.userProfile.ManageUserScreen
import com.servicerca.app.ui.dashboard.moderador.userProfile.UserProfileManage

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
        composable("detailsServicesModerator/{serviceId}") { backStackEntry ->
            val serviceId = backStackEntry.arguments?.getString("serviceId")
            DetailsVerificationModeratorScreen(
                serviceId = serviceId, // Pasa el ID a la pantalla
                onBack = { navController.popBackStack() },
                onRejectClick = { navController.navigate("rejectReason/$serviceId") },
                onApproveSuccess = {
                    navController.navigate(DashboardRoutes.HomeModerator) {
                        popUpTo(DashboardRoutes.HomeModerator) { inclusive = true }
                    }
                }
            )
        }

        composable("rejectReason/{serviceId}") { backStackEntry ->
            val serviceId = backStackEntry.arguments?.getString("serviceId")
            RejectReasonScreen(
                serviceId = serviceId, // Pasa el ID a la pantalla
                onBack = { navController.popBackStack() },
                onRejectSuccess = {
                    navController.navigate(DashboardRoutes.HomeModerator) {
                        popUpTo(DashboardRoutes.HomeModerator) { inclusive = true }
                    }
                }
            )
        }

        composable<DashboardRoutes.ProfileModerator> {
            ProfileModerator(
                navController = navController,
                onLogout = { onLogout() },
                onUpdatePassword = { navController.navigate("updatePassword") }
            )
        }

        composable("updatePassword") {
            UpdatePasswordScreen(
                onBack = { navController.popBackStack() },
                onLogout = { onLogout() }
            )
        }

        composable<DashboardRoutes.Historial> {
            ModerationHistory()
        }

        composable<DashboardRoutes.ManageUser> {
            ManageUserScreen(
                onSeeProfile = { userId ->
                    navController.navigate(DashboardRoutes.UserDetail(userId))
                }
            )
        }

        composable<DashboardRoutes.UserDetail> {
            UserProfileManage()
        }
    }
}
