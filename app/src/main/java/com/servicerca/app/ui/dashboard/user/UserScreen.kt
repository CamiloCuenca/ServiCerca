package com.servicerca.app.ui.dashboard.user

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.servicerca.app.core.components.navigation.AppTopAppBar
import com.servicerca.app.core.components.navigation.BottomNavigationBar
import com.servicerca.app.core.navigation.DashboardRoutes
import com.servicerca.app.core.navigation.MainRoutes
import com.servicerca.app.core.navigation.UserNavigation
import androidx.hilt.navigation.compose.hiltViewModel
import com.servicerca.app.ui.notifications.NotificationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(
    onLogout: () -> Unit,
    onCreateService: () -> Unit,
    onNotificationClick: () -> Unit,
    onMapClick: () -> Unit,
    onReservationDetailClick: (String) -> Unit,
    onMakeReservationClick: (String) -> Unit,
    viewModel: NotificationViewModel = hiltViewModel()
) {

    val navController = rememberNavController()
    val context = androidx.compose.ui.platform.LocalContext.current
    var title by remember { mutableStateOf(context.getString(com.servicerca.app.R.string.nav_label_home)) }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val destination = backStackEntry?.destination

    val showTopBar = destination?.let {
        !it.hasRoute<DashboardRoutes.EditProfile>() &&
        !it.hasRoute<DashboardRoutes.DeleteProfile>() &&
        !it.hasRoute<DashboardRoutes.UpdatePassword>() &&
        !it.hasRoute<DashboardRoutes.QrScannerDashboard>() &&
        !it.hasRoute<DashboardRoutes.QrServiceVerification>() &&
        !it.hasRoute<DashboardRoutes.ListInteresting>() &&
        !it.hasRoute<DashboardRoutes.DetailService>() &&
        !it.hasRoute<DashboardRoutes.Chat>() &&
        !it.hasRoute<MainRoutes.MakeReservation>()
    } ?: true

    val showBottomBar = destination?.let {
        !it.hasRoute<DashboardRoutes.Insignias>() &&
        !it.hasRoute<DashboardRoutes.ServiceList>() &&
        !it.hasRoute<DashboardRoutes.QrScannerDashboard>() &&
        !it.hasRoute<DashboardRoutes.QrServiceVerification>() &&
        !it.hasRoute<DashboardRoutes.ListInteresting>() &&
        !it.hasRoute<DashboardRoutes.DetailService>() &&
        !it.hasRoute<DashboardRoutes.Chat>() &&
        !it.hasRoute<MainRoutes.MakeReservation>()
    } ?: true

    val showFab = destination?.let {
        !it.hasRoute<DashboardRoutes.Insignias>() &&
        !it.hasRoute<DashboardRoutes.EditProfile>() &&
        !it.hasRoute<DashboardRoutes.DeleteProfile>() &&
        !it.hasRoute<DashboardRoutes.UpdatePassword>() &&
        !it.hasRoute<DashboardRoutes.QrScannerDashboard>() &&
        !it.hasRoute<DashboardRoutes.QrServiceVerification>() &&
        !it.hasRoute<DashboardRoutes.ListInteresting>() &&
        !it.hasRoute<DashboardRoutes.DetailService>() &&
        !it.hasRoute<DashboardRoutes.Chat>() &&
        !it.hasRoute<MainRoutes.MakeReservation>()
    } ?: true


    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val unreadCount by viewModel.unreadCount.collectAsState()
    val notificationCount = unreadCount


    Scaffold(
        topBar = {
            if (showTopBar) {
                AppTopAppBar(
                    title = title,
                    notificationCount = notificationCount ,
                    onLocationClick = {
                        onMapClick()
                    },
                    onNotificationClick = {
                        onNotificationClick()
                    },
                    scrollBehavior = scrollBehavior
                )
            }
        },
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(
                    navController = navController,
                    titleTopBar = { title = it }
                )
            }
        },
        floatingActionButton = {
            if (showFab) {
                FloatingActionButton(
                    onClick = onCreateService,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                }
            }
        }
    ) { padding ->

        UserNavigation(
            navController = navController,
            _padding = padding,
            onLogout = onLogout,
            onReservationDetailClick = onReservationDetailClick,
            onMakeReservationClick = onMakeReservationClick
        )
    }
}



@Preview(showBackground = true)
@Composable
fun UserScreenPreview() {
    UserScreen(
        onLogout = {},
        onCreateService = {},
        onNotificationClick = {},
        onMapClick = {},
        onReservationDetailClick = {},
        onMakeReservationClick = {}
    )
}
