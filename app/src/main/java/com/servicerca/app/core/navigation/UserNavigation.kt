package com.servicerca.app.core.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute


@Composable
fun UserNavigation(
    navController: NavHostController,
    padding: PaddingValues
){

    NavHost(
        navController = navController,
        startDestination = DashboardRoutes.HomeUser
    ) {

        composable<DashboardRoutes.HomeUser> {
            // La pantalla principal de la sección de usuarios que muestra la lista de usuarios

        }

        composable<DashboardRoutes.Search> {
        }

        composable<DashboardRoutes.Profile> {
        }

        composable<DashboardRoutes.UserDetail> {
        }
    }

}