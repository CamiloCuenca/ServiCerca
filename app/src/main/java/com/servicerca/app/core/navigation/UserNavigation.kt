package com.servicerca.app.core.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.servicerca.app.ui.chat.ChatListScreen
import com.servicerca.app.ui.chat.ChatScreen
import com.servicerca.app.ui.dashboard.user.HomeUserScreen
import com.servicerca.app.ui.dashboard.moderador.userProfile.ManageUserScreen
import com.servicerca.app.ui.profile.DeleteProfileScreen
import com.servicerca.app.ui.profile.EditProfileScreen
import com.servicerca.app.ui.profile.InsigniasScreen
import com.servicerca.app.ui.profile.ProfileScreen
import com.servicerca.app.ui.profile.UpdatePasswordScreen
import com.servicerca.app.ui.qr.ProviderVerificationScreen
import com.servicerca.app.ui.qr.ServiceVerificationScreen
import com.servicerca.app.ui.reservation.MakeReservation
import com.servicerca.app.ui.reservation.ReservationScreen
import com.servicerca.app.ui.reservation.details.DetailsReservationScreen
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
                    navController.navigate(DashboardRoutes.DetailService(serviceId))
                }
            )
        }

        composable<DashboardRoutes.DetailService> { backStackEntry ->
            val route = backStackEntry.toRoute<DashboardRoutes.DetailService>()
            DetailServiceScreen(
                serviceId = route.serviceId,
                onBack = { navController.popBackStack() },
                onMakeReservation = { id ->
                    onMakeReservationClick(id)
                }
            )
        }


        composable<DashboardRoutes.Search> {
            SearchScreen(
                onServiceClick = { serviceId ->
                    navController.navigate(DashboardRoutes.DetailService(serviceId))
                }
            )
        }

        composable<DashboardRoutes.Profile> {
            ProfileScreen(
                onInsignias = {
                    navController.navigate(DashboardRoutes.Insignias)
                },
                onEditProflie = {
                    navController.navigate(DashboardRoutes.EditProfile)
                },
                onUpdatePassword = {
                    navController.navigate(DashboardRoutes.UpdatePassword)
                },
                onDeleteProfile = {
                    navController.navigate(DashboardRoutes.DeleteProfile)
                },
                onListService = {
                    navController.navigate(DashboardRoutes.ServiceList)
                },
                onListInteresting = {
                    navController.navigate(DashboardRoutes.ListInteresting)
                },
                onLogout = {
                    // Delegar la acción de logout al callback pasado desde la pantalla raíz
                    onLogout()
                }
            )
        }

        composable<DashboardRoutes.ListInteresting> {
            ListInteresting(
                onBack = { navController.popBackStack() },
                onServiceClick = { serviceId ->
                    navController.navigate(DashboardRoutes.DetailService(serviceId))
                }
            )
        }

        composable<DashboardRoutes.ServiceList> {
            ListServiceScreen(
                onBackClick = { navController.popBackStack() },
                onEditService = { serviceId ->
                    navController.navigate(DashboardRoutes.EditService(serviceId))
                }
            )
        }




        composable<DashboardRoutes.UserDetail> {
        }

        composable<DashboardRoutes.ChatList> {
            ChatListScreen(
                onChatClick = { chatId ->
                    navController.navigate(DashboardRoutes.Chat(chatId))
                }
            )
        }

        composable<DashboardRoutes.Reservation> {
            ReservationScreen(
                onResevationDetails = { reservationId ->
                    onReservationDetailClick(reservationId)
                },
                onQrScanner = {
                    navController.navigate(DashboardRoutes.QrScannerDashboard)
                }

            )
        }

        // Se eliminó MakeReservation y ReservationDetail de aquí, ahora son globales


        composable<DashboardRoutes.QrServiceVerification> { backStackEntry ->
            val route = backStackEntry.toRoute<DashboardRoutes.QrServiceVerification>()
            ServiceVerificationScreen(
                reservationId = route.reservationId,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable<DashboardRoutes.QrScannerDashboard> {
            ProviderVerificationScreen(
                onBackClick = {
                    navController.popBackStack()
                }

            )
        }





        composable<DashboardRoutes.Insignias> {
            InsigniasScreen(onBack = { navController.popBackStack() })
        }

        composable<DashboardRoutes.EditProfile> {
            EditProfileScreen(
                onBack = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() }
            )
        }

        composable<DashboardRoutes.UpdatePassword> {
            UpdatePasswordScreen(
                onBack = { navController.popBackStack() },
                onLogout = { onLogout() }
            )
        }
        composable<DashboardRoutes.DeleteProfile> {
            DeleteProfileScreen(
                onBack = { navController.popBackStack() },
                onDeleteSuccess = { onLogout() }
            )
        }


        composable<DashboardRoutes.EditService> { backStackEntry ->
            val route = backStackEntry.toRoute<DashboardRoutes.EditService>()
            EditServiceScreen(
                serviceId = route.serviceId,
                onBack = { navController.popBackStack() },
                onSaveSuccess = { navController.popBackStack() }
            )
        }

        composable<DashboardRoutes.Chat> {
            ChatScreen(onBack = { navController.popBackStack() })
        }

    }

}
