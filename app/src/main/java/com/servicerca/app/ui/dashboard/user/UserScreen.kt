package com.servicerca.app.ui.dashboard.user


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.servicerca.app.core.components.navigation.AppTopAppBar
import com.servicerca.app.core.components.navigation.BottomNavigationBar
import com.servicerca.app.core.navigation.UserNavigation
import kotlin.Unit

@Composable
fun UserScreen(
    onLogout: () -> Unit,
    onCreateService: () -> Unit
) {

    val navController = rememberNavController()
    var title by remember { mutableStateOf("Inicio usuario") }

    Scaffold(
        topBar = {
            AppTopAppBar(
                title = title,
                logout = onLogout
            )
        },
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                titleTopBar = {
                    title = it
                }
            )
        },

        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateService
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar"
                )
            }
        }
    ) { padding ->
        UserNavigation(
            navController = navController,
            padding = padding
        )
    }
}

@Composable
fun HomeUserScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Bienvenido usuario 👋")
    }
}


@Composable
@Preview
fun userScreenPreview(){
    UserScreen(
        onLogout = {},
        onCreateService = {}
    )
}


