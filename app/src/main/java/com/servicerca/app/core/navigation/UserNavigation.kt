package com.servicerca.app.core.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.servicerca.app.ui.chat.ChatListScreen
import com.servicerca.app.ui.chat.ChatScreen
import com.servicerca.app.ui.dashboard.user.HomeUserScreen
import com.servicerca.app.ui.profile.DeleteProfileScreen
import com.servicerca.app.ui.profile.EditProfileScreen
import com.servicerca.app.ui.profile.InsigniasScreen
import com.servicerca.app.ui.profile.ProfileScreen
import com.servicerca.app.ui.profile.UpdatePasswordScreen
import com.servicerca.app.ui.qr.ProviderVerificationScreen
import com.servicerca.app.ui.qr.ServiceVerificationScreen
import com.servicerca.app.ui.reservation.ReservationScreen
import com.servicerca.app.ui.search.SearchScreen
import com.servicerca.app.ui.services.ListInteresting.ListInteresting
import com.servicerca.app.ui.services.ListService.ListServiceScreen
import com.servicerca.app.ui.services.detail.DetailServiceScreen
import com.servicerca.app.ui.services.edit.EditServiceScreen


@Composable
fun UserNavigation(
    navController: NavHostController,
    _padding: PaddingValues,
    onLogout: () -> Unit,
    onReservationDetailClick: (String) -> Unit,
    onMakeReservationClick: (String) -> Unit
){

    NavHost(
        navController = navController,
        startDestination = DashboardRoutes.HomeUser,
        modifier = Modifier.padding(_padding)
    ) {

        composable<DashboardRoutes.HomeUser> {
            HomeUserScreen(
                onDetailClick = { serviceId ->
                    navController.navigate("DetailService/$serviceId")
                }
            )
        }

        composable("DetailService/{serviceId}") { backStackEntry ->
            val serviceId = backStackEntry.arguments?.getString("serviceId") ?: "1"
            DetailServiceScreen(
                serviceId = serviceId,
                onBack = { navController.popBackStack() },
                onMakeReservation = { id ->
                    onMakeReservationClick(id)
                }
            )
        }


        composable<DashboardRoutes.Search> {
            SearchScreen(
                onServiceClick = { serviceId ->
                    navController.navigate("DetailService/$serviceId")
                }
            )
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
                onListInteresting = {
                    navController.navigate("ListInteresting")
                },
                onLogout = {
                    // Delegar la acción de logout al callback pasado desde la pantalla raíz
                    onLogout()
                }
            )
        }

        composable("ListInteresting" ){
            ListInteresting(
                onBack = { navController.popBackStack() },
                onServiceClick = { serviceId ->
                    navController.navigate("DetailService/$serviceId")
                }
            )
        }

        composable("serviceList") {
            ListServiceScreen(
                onBackClick = { navController.popBackStack() },
                onEditService = { serviceId ->
                    navController.navigate("editService/$serviceId")
                }
            )
        }




        composable<DashboardRoutes.UserDetail> {
        }

        composable<DashboardRoutes.ChatList> {
            ChatListScreen(
                onChatClick = { chatId ->
                    navController.navigate("Chat/$chatId")
                }
            )
        }

        composable<DashboardRoutes.Reservation> {
            ReservationScreen(
                onResevationDetails = { reservationId ->
                    onReservationDetailClick(reservationId)
                },
                onQrScanner = {
                    navController.navigate("QrScanner")
                }

            )
        }

        // Se eliminó MakeReservation y ReservationDetail de aquí, ahora son globales


        composable("QrService/{reservationId}") { backStackEntry ->
            val reservationId = backStackEntry.arguments?.getString("reservationId") ?: return@composable
            ServiceVerificationScreen(
                reservationId = reservationId,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable("QrScanner"){
            ProviderVerificationScreen(
                onBackClick = {
                    navController.popBackStack()
                }

            )
        }





        composable("insignias") {
            InsigniasScreen(onBack = { navController.popBackStack() })

        }

        composable("editProfile") {
            EditProfileScreen(
                onBack = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() }
            )
        }

        composable("updatePassword") {
            UpdatePasswordScreen(
                onBack = { navController.popBackStack() },
                onLogout = { onLogout() }
            )
        }
        composable("deleteProfile") {
            DeleteProfileScreen(
                onBack = { navController.popBackStack() },
                onDeleteSuccess = { onLogout() }
            )
        }


        composable("editService/{serviceId}") { backStackEntry ->
            val serviceId = backStackEntry.arguments?.getString("serviceId") ?: ""
            EditServiceScreen(
                serviceId = serviceId,
                onBack = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() }
            )
        }

        composable("Chat/{chatId}") {
            ChatScreen(onBack = { navController.popBackStack()})
        }

    }

}
