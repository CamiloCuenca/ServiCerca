package com.servicerca.app.core.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.servicerca.app.ui.SearchScreen
import com.servicerca.app.ui.chat.ChatScreen
import com.servicerca.app.ui.dashboard.user.HomeUserScreen
import com.servicerca.app.ui.dashboard.user.UserScreen
import com.servicerca.app.ui.profile.InsigniasScreen
import com.servicerca.app.ui.profile.ProfileScreen
import com.servicerca.app.ui.reservation.ReservationScreen


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
            HomeUserScreen()
        }

        composable<DashboardRoutes.Search> {
            SearchScreen()
        }

        composable<DashboardRoutes.Profile> {
            ProfileScreen(
                onInsignias = {
                    navController.navigate(MainRoutes.Insignias)
                }


            )
        }

        composable<DashboardRoutes.UserDetail> {
        }

        composable<DashboardRoutes.Chat> {
            ChatScreen()
        }

        composable<DashboardRoutes.Reservation> {
            ReservationScreen()
        }

        composable<MainRoutes.Insignias> {
            InsigniasScreen()

        }


    }

}