package com.servicerca.app.core.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.servicerca.app.ui.dashboard.moderador.DetailsVerificationModeratorScreen
import com.servicerca.app.ui.dashboard.moderador.ModerationHistory
import com.servicerca.app.ui.dashboard.moderador.ModeratorPanelScreen
import com.servicerca.app.ui.dashboard.moderador.ProfileModerator
import com.servicerca.app.ui.dashboard.moderador.RejectReasonScreen
import com.servicerca.app.ui.dashboard.moderador.userProfile.ManageUserScreen
import com.servicerca.app.ui.dashboard.moderador.userProfile.UserProfileManage
import com.servicerca.app.ui.profile.UpdatePasswordScreen

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
        composable<DashboardRoutes.DetailServiceModerator> { backStackEntry ->
            val route = backStackEntry.toRoute<DashboardRoutes.DetailServiceModerator>()
            DetailsVerificationModeratorScreen(
                serviceId = route.serviceId,
                onBack = { navController.popBackStack() },
                onRejectClick = { navController.navigate(DashboardRoutes.RejectReason(route.serviceId)) },
                onApproveSuccess = {
                    navController.navigate(DashboardRoutes.HomeModerator) {
                        popUpTo(DashboardRoutes.HomeModerator) { inclusive = true }
                    }
                }
            )
        }

        composable<DashboardRoutes.RejectReason> { backStackEntry ->
            val route = backStackEntry.toRoute<DashboardRoutes.RejectReason>()
            RejectReasonScreen(
                serviceId = route.serviceId,
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
                onUpdatePassword = { navController.navigate(DashboardRoutes.UpdatePassword) }
            )
        }

        composable<DashboardRoutes.UpdatePassword> {
            UpdatePasswordScreen(
                onBack = { navController.popBackStack() },
                onLogout = { onLogout() }
            )
        }

        composable<DashboardRoutes.Historial> { backStackEntry ->
            val route = backStackEntry.toRoute<DashboardRoutes.Historial>()
            ModerationHistory(
                navController = navController,
                initialTab = route.initialTab
            )
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
