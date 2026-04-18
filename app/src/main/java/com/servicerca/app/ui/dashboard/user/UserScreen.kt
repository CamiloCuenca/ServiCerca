package com.servicerca.app.ui.dashboard.user

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.servicerca.app.core.components.navigation.AppTopAppBar
import com.servicerca.app.core.components.navigation.BottomNavigationBar
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
    var title by remember { mutableStateOf("Inicio") }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val hideTopBarRoutes = setOf(
        "editProfile",
        "deleteProfile",
        "updatePassword",
        "QrScanner",
        "QrService",
        "ListInteresting",
        "DetailService",
        "Chat/{chatId}",
        "DetailService",
        "com.servicerca.app.core.navigation.MainRoutes.MakeReservation"
    )

    val hideBottomBarRoutes = setOf(
        "insignias",
        "serviceList",
        "QrScanner",
        "QrService",
        "ListInteresting",
        "DetailService",
        "Chat/{chatId}",
        "DetailService",
        "com.servicerca.app.core.navigation.MainRoutes.MakeReservation"
    )

    val hideFabRoutes = setOf(
        "insignias",
        "editProfile",
        "deleteProfile",
        "updatePassword",
        "notifications",
        "QrScanner",
        "QrService",
        "ListInteresting",
        "DetailService",
        "com.servicerca.app.core.navigation.MainRoutes.MakeReservation",
        "DetailService",
        "Chat/{chatId}"
    )

    // Lógica para detectar si es una ruta con parámetros (como DetailService/{id})
    val isDetailService = currentRoute?.startsWith("DetailService") ?: false
    val isMakeReservation = currentRoute?.contains("MakeReservation") ?: false

    val showTopBar = currentRoute !in hideTopBarRoutes && !isDetailService && !isMakeReservation
    val showBottomBar = currentRoute !in hideBottomBarRoutes && !isDetailService && !isMakeReservation
    val showFab = currentRoute !in hideFabRoutes && !isDetailService && !isMakeReservation


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
