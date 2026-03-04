package com.servicerca.app.ui.dashboard.moderador

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.servicerca.app.core.components.navigation.AppTopAppBar
import com.servicerca.app.core.components.navigation.AppTopAppBarModerator
import com.servicerca.app.core.components.navigation.BottomNavigationBar
import com.servicerca.app.core.components.navigation.BottomNavigationBarModerator
import com.servicerca.app.core.navigation.ModeratorNavigation
import com.servicerca.app.core.navigation.UserNavigation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModeratorScreen(
                    onLogout: () -> Unit,
                    onCreateService: () -> Unit,
                    onNotificationClick: () -> Unit
) {

    val navController = rememberNavController()
    var title by remember { mutableStateOf("Inicio") }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val showBars = currentRoute != "insignias"
    val showtop = currentRoute != "editProfile"
    val showBT = currentRoute != "deleteProfile"
    val showBT2 = currentRoute != "updatePassword"
    val showBT3 = currentRoute != "notifications"
    val showModerator = currentRoute != "detailsServicesModerator"


    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    var notificationCount by remember { mutableStateOf(5) }


    Scaffold(
        topBar = {
            if (showBars && showtop && showBT && showBT2 && showBT3 && showModerator) {
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
            if (showBars && showtop && showBT && showBT2 && showBT3 && showModerator) {
                BottomNavigationBarModerator (
                    navController = navController,
                    titleTopBar = { title = it }
                )
            }
        },

    ) { padding ->

        ModeratorNavigation(
            navController = navController,
            padding = padding
        )
    }

}