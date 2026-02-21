package com.servicerca.app.ui.dashboard.user

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.servicerca.app.core.components.navigation.AppTopAppBar
import com.servicerca.app.core.components.navigation.BottomNavigationBar
import com.servicerca.app.core.navigation.UserNavigation

@Composable
fun UserScreen(
    onLogout: () -> Unit,
    onCreateService: () -> Unit
) {

    val navController = rememberNavController()
    var title by remember { mutableStateOf("Inicio") }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val showBars = currentRoute != "insignias"

    Scaffold(
        topBar = {
            if (showBars) {
                AppTopAppBar(
                    title = title,
                    logout = onLogout
                )
            }
        },
        bottomBar = {
            if (showBars) {
                BottomNavigationBar(
                    navController = navController,
                    titleTopBar = { title = it }
                )
            }
        },
        floatingActionButton = {
            if (showBars) {
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
            _padding = padding
        )
    }
}

@Composable
fun HomeUserScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Bienvenido usuario 👋",
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UserScreenPreview() {
    UserScreen(
        onLogout = {},
        onCreateService = {}
    )
}