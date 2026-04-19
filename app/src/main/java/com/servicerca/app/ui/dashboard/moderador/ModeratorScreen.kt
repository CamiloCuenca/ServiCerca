package com.servicerca.app.ui.dashboard.moderador

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.servicerca.app.core.components.navigation.AppTopAppBarModerator
import com.servicerca.app.core.components.navigation.BottomNavigationBarModerator
import com.servicerca.app.core.navigation.DashboardRoutes
import com.servicerca.app.core.navigation.ModeratorNavigation
import com.servicerca.app.ui.notifications.NotificationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModeratorScreen(
    onLogout: () -> Unit,
    onCreateService: () -> Unit,
    onNotificationClick: () -> Unit,
    onReservationDetailClick: (String) -> Unit,
    onMakeReservationClick: (String) -> Unit,
    viewModel: NotificationViewModel = hiltViewModel()
) {

    val navController = rememberNavController()
    val context = androidx.compose.ui.platform.LocalContext.current
    var title by remember { mutableStateOf(context.getString(com.servicerca.app.R.string.nav_label_home)) }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val destination = backStackEntry?.destination

    val showBars = destination?.hasRoute<DashboardRoutes.Insignias>() != true
    val showtop = destination?.hasRoute<DashboardRoutes.EditProfile>() != true
    val showBT = destination?.hasRoute<DashboardRoutes.DeleteProfile>() != true
    val showBT2 = destination?.hasRoute<DashboardRoutes.UpdatePassword>() != true
    val showBT3 = true // Notifications are handled in the outer AppNavigation NavHost, not here
    val showModerator = destination?.hasRoute<DashboardRoutes.DetailServiceModerator>() != true
    val showModerator2 = destination?.hasRoute<DashboardRoutes.RejectReason>() != true


    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val unreadCount by viewModel.unreadCount.collectAsState()
    val notificationCount = unreadCount


    Scaffold(
        topBar = {
            if (showBars && showtop && showBT && showBT2 && showBT3 && showModerator && showModerator2) {
                AppTopAppBarModerator(
                    title = title,
                    notificationCount = notificationCount,
                    onLocationClick = {
                        // TODO abrir mapa
                    },
                    onNotificationClick = {
                        onNotificationClick()
                    },
                    scrollBehavior = scrollBehavior
                )
            }
        },
        bottomBar = {
            if (showBars && showtop && showBT && showBT2 && showBT3 && showModerator && showModerator2) {
                BottomNavigationBarModerator (
                    navController = navController,
                    titleTopBar = { title = it }
                )
            }
        },

    ) { padding ->

        ModeratorNavigation(
            navController = navController,
            padding = padding,
            onLogout = onLogout
        )
    }

}
@Preview
@Composable
fun ModeratorScreenPreview() {
    ModeratorScreen(
        onLogout = {},
        onCreateService = {},
        onNotificationClick = {},
        onReservationDetailClick = {},
        onMakeReservationClick = {}
    )

}