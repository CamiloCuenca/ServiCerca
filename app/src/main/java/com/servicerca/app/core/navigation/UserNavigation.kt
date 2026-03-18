package com.servicerca.app.core.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.servicerca.app.ui.search.SearchScreen
import com.servicerca.app.ui.chat.MessageListScreen
import com.servicerca.app.ui.dashboard.user.HomeUserScreen
import com.servicerca.app.ui.dashboard.moderador.ModeratorPanelScreen
import com.servicerca.app.ui.profile.DeleteProfileScreen
import com.servicerca.app.ui.profile.EditProfileScreen
import com.servicerca.app.ui.profile.InsigniasScreen
import com.servicerca.app.ui.profile.ProfileScreen
import com.servicerca.app.ui.profile.UpdatePasswordScreen
import com.servicerca.app.ui.qr.ProviderVerificationScreen
import com.servicerca.app.ui.qr.ServiceVerificationScreen
import com.servicerca.app.ui.reservation.DeleteReservationScreen
import com.servicerca.app.ui.reservation.ReservationScreen
import com.servicerca.app.ui.reservation.details.DetailsReservationScreen
import com.servicerca.app.ui.services.ListService.ListServiceScreen


@Composable
fun UserNavigation(
    navController: NavHostController,
    _padding: PaddingValues,
    onLogout: () -> Unit // nuevo parámetro para delegar logout al NavController raíz
){

    NavHost(
        navController = navController,
        startDestination = DashboardRoutes.HomeUser,
        modifier = Modifier.padding(_padding)
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
                    navController.navigate("insignias")
                },
                onEditProflie = {
                    navController.navigate("editProfile")
                },
                onUpdatePassword = {
                    navController.navigate("updatePassword")
                },
                onDeleteProfile = {
                    navController.navigate("deleteProfile")
                },
                onListService = {
                    navController.navigate("serviceList")
                },
                onLogout = {
                    // Delegar la acción de logout al callback pasado desde la pantalla raíz
                    onLogout()
                }
            )
        }

        composable("serviceList" ){
            ListServiceScreen(
                onBackClick = { navController.popBackStack() } ,
                onEditService = { navController.navigate("editService") },

            )

        }




        composable<DashboardRoutes.UserDetail> {
        }

        composable<DashboardRoutes.MessageList> {
            MessageListScreen()
        }

        composable<DashboardRoutes.Reservation> {
            ReservationScreen(
                onResevationDetails = {
                    navController.navigate(MainRoutes.ReservationDetail)
                },
                onQrScanner = {
                    navController.navigate("QrScanner")
                }

            )
        }

        composable<MainRoutes.ReservationDetail> {

            DetailsReservationScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onQr = {
                    navController.navigate("QrService")
                }

            )

        }
        composable("QrService"){
            ServiceVerificationScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable("QrScanner"){
            ProviderVerificationScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onScanClick = {
                    navController.navigate(MainRoutes.QrService)
                }

            )
        }





        composable("insignias") {
            InsigniasScreen(onBack = { navController.popBackStack() })

        }

        composable("editProfile") {
            EditProfileScreen(onBack = { navController.popBackStack() })

        }

        composable("updatePassword") {
            UpdatePasswordScreen(onBack = { navController.popBackStack() })

        }
        composable("deleteProfile") {
            DeleteProfileScreen(onBack = { navController.popBackStack() })

        }






    }

}