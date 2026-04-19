package com.servicerca.app.core.navigation

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.servicerca.app.data.model.UserSession
import com.servicerca.app.domain.model.UserRole
import com.servicerca.app.ui.Map.MapScreen
import com.servicerca.app.ui.Welcome.WelcomeScreen
import com.servicerca.app.ui.auth.login.LoginScreen
import com.servicerca.app.ui.auth.login.Recover.RecoverPasswordScreen
import com.servicerca.app.ui.auth.login.Reset.ResetPassword
import com.servicerca.app.ui.auth.register.RegisterScreen
import com.servicerca.app.ui.auth.register.VerifyEmailScreen
import com.servicerca.app.ui.dashboard.moderador.ModeratorScreen
import com.servicerca.app.ui.dashboard.user.UserScreen
import com.servicerca.app.ui.notifications.NotificationsScreen
import com.servicerca.app.ui.services.create.CreateServiceScreen
import com.servicerca.app.ui.services.detail.DetailServiceScreen
import com.servicerca.app.ui.reservation.details.DetailsReservationScreen
import com.servicerca.app.domain.model.NotificationType
import androidx.navigation.toRoute
import androidx.lifecycle.viewmodel.compose.viewModel
import com.servicerca.app.ui.auth.register.RegisterViewModel
import com.servicerca.app.ui.reservation.MakeReservation


@Composable
fun AppNavigation(
    sessionViewModel: SessionViewModel = hiltViewModel()
) {
    // Observa el estado de la sesión desde el ViewModel
    val sessionState by sessionViewModel.sessionState.collectAsState()
    // Estado local para forzar la navegación inmediatamente después del login,
    // mientras DataStore/SessionViewModel actualizan su flujo.
    var forcedSession by remember { mutableStateOf<UserSession?>(null) }

    Surface(modifier = Modifier.fillMaxSize()) {
        when (val state = sessionState) {
            is SessionState.Loading -> {
                // Se muestra un indicador de carga mientras se determina el estado de la sesión
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is SessionState.NotAuthenticated -> {
                // Si ya forzamos una sesión localmente (por ejemplo justo después del login), mostrar MainNavigation
                if (forcedSession != null) {
                    MainNavigation(session = forcedSession!!, onLogout = {
                        // Aseguramos limpiar tanto DataStore como la sesión forzada
                        sessionViewModel.logout()
                        forcedSession = null
                    })
                } else {
                    AuthNavigation(onLoginSuccess = { userId, role ->
                        Log.d("AppNavigation", "onLoginSuccess received: userId=$userId, role=$role")
                        // Guardar en DataStore y forzar navegación inmediata
                        sessionViewModel.login(userId, role)
                        forcedSession = UserSession(userId = userId, role = role)
                        Log.d("AppNavigation", "forcedSession set: $forcedSession")
                    })
                }
            }
            is SessionState.Authenticated -> MainNavigation(
                session = state.session,
                // Cuando estamos realmente autenticados, al hacer logout limpiamos también la sesión forzada
                onLogout = {
                    // Evitar que una forcedSession previa mantenga la UI del dashboard
                    forcedSession = null
                    sessionViewModel.logout()
                }
            )
        }
    }
}



@Composable
private fun AuthNavigation(onLoginSuccess: (String, UserRole) -> Unit) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = MainRoutes.Home
    ) {

        composable<MainRoutes.Home> {
            WelcomeScreen(
                onNavigateToLogin = {
                    navController.navigate(MainRoutes.Login)
                },
                onNavigateToRegister = {
                    navController.navigate(MainRoutes.Register)
                }
            )
        }

        composable<MainRoutes.Login> {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(MainRoutes.Register)
                }
                // Importante: no navegar desde el NavHost de autenticación a rutas del NavHost principal.
                // Cuando el login sea exitoso el SessionViewModel debe actualizar el estado y
                // `AppNavigation` reemplazará este NavHost por `MainNavigation`.
                , onNavigateToUsers = {
                    /* no-op: el cambio de NavHost lo hará AppNavigation al actualizar el estado de sesión */
                },
                onRecoverPassword ={
                    navController.navigate(MainRoutes.RecoverPassword)
                },
                onModeratorPanel = {
                    /* no-op: evitar navegar a rutas de Dashboard desde este NavHost */
                },
                onLoginSuccess = { userId, role -> onLoginSuccess(userId, role) }
            )
        }




            composable<MainRoutes.Register> {
                val registerViewModel: RegisterViewModel = hiltViewModel()
                RegisterScreen(
                    viewModel = registerViewModel,
                    onNavigateToLogin = {
                        navController.navigate(MainRoutes.Login)
                    },
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onVerifyEmail = {
                        navController.navigate(MainRoutes.VerifyEmail(email = registerViewModel.email.value.trim()))

                    }
                )
            }

        composable<MainRoutes.RecoverPassword> {
            RecoverPasswordScreen(
                onNavigateToLogin = {
                    navController.navigate(MainRoutes.Login)
                }
                , onBackClick = {
                    navController.popBackStack()
                },
                onNavigateToResetPassword = { email ->
                    navController.navigate(MainRoutes.Reset(email = email))
                }
            )
        }



        composable<MainRoutes.Reset> { backStackEntry ->
            val route = backStackEntry.toRoute<MainRoutes.Reset>()
            ResetPassword(
                email = route.email,
                onNavigateToLogin = {

                    navController.navigate(MainRoutes.Login) {
                        popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                    }
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

            composable<MainRoutes.VerifyEmail> { backStackEntry ->
                val route = backStackEntry.toRoute<MainRoutes.VerifyEmail>()
                VerifyEmailScreen(
                    email = route.email,
                    onNavigateToLogin = {
                        navController.navigate(MainRoutes.Login) {
                            popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                        }
                    }
                )
            }



    }


}

@Composable
private fun MainNavigation(
    session: UserSession,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()

    // Determina la pantalla de inicio según el rol del usuario
    val startDestination = when (session.role) {
        UserRole.ADMIN, UserRole.MODERATOR -> DashboardRoutes.HomeModerator
        UserRole.USER -> DashboardRoutes.HomeUser
        else -> DashboardRoutes.HomeUser
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        // --- Usuario (Dashboard) ---
        composable<DashboardRoutes.HomeUser> {
            UserScreen(
                onLogout = {
                    // Solo llamamos a onLogout(). AppNavigation detectará el cambio de estado
                    // y nos llevará de vuelta al flujo de Auth automáticamente.
                    onLogout()
                },
                onCreateService ={
                    navController.navigate(MainRoutes.CreateService)
                },

                onNotificationClick = {
                    navController.navigate(MainRoutes.Notifications)
                },
                onMapClick = {
                    navController.navigate(MainRoutes.Map)
                },
                onReservationDetailClick = { reservationId ->
                    navController.navigate(MainRoutes.ReservationDetail(reservationId))
                },
                onMakeReservationClick = { serviceId ->
                    navController.navigate(MainRoutes.MakeReservation(serviceId))
                }
            )
        }

        // --- Moderador ---
        composable<DashboardRoutes.HomeModerator> {
            ModeratorScreen(
                onLogout = {
                    onLogout()
                },
                onCreateService = {
                    navController.navigate(MainRoutes.CreateService)
                },
                onNotificationClick = {
                    navController.navigate(MainRoutes.Notifications)
                },
                onReservationDetailClick = { reservationId ->
                    navController.navigate(MainRoutes.ReservationDetail(reservationId))
                },
                onMakeReservationClick = { serviceId ->
                    navController.navigate(MainRoutes.MakeReservation(serviceId))
                }
            )
        }


        composable<MainRoutes.CreateService> {
            CreateServiceScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable <MainRoutes.Notifications>{
            NotificationsScreen(
                onBack = {
                    navController.popBackStack()
                },
                onNotificationClick = { type, id ->
                    when (type) {
                        NotificationType.RESERVATION -> {
                            navController.navigate(MainRoutes.ReservationDetail(id))
                        }
                        NotificationType.SERVICE -> {
                            navController.navigate(MainRoutes.ServiceDetail(id))
                        }
                        else -> { /* No-op for System */ }
                    }
                }
            )
        }

        composable<MainRoutes.ReservationDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<MainRoutes.ReservationDetail>()
            DetailsReservationScreen(
                reservationId = route.reservationId,
                onBackClick = {
                    navController.popBackStack()
                },
                onQr = {
                    // Si se requiere QR desde aquí, asegurar que la ruta exista o manejarla
                    navController.navigate(MainRoutes.QrService)
                }
            )
        }

        composable<MainRoutes.Map>{
            MapScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable<MainRoutes.ServiceDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<MainRoutes.ServiceDetail>()
            DetailServiceScreen(
                serviceId = route.serviceId,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<MainRoutes.MakeReservation> { backStackEntry ->
            val route = backStackEntry.toRoute<MainRoutes.MakeReservation>()
            MakeReservation(
                serviceId = route.serviceId,
                onBack = {
                    navController.popBackStack()
                }
            )
        }

    }



}
