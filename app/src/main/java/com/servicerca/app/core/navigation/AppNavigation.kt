package com.servicerca.app.core.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.servicerca.app.ui.Map.MapScreen
import com.servicerca.app.ui.auth.login.LoginScreen
import com.servicerca.app.ui.auth.register.RegisterScreen
import com.servicerca.app.ui.dashboard.user.UserScreen
import com.servicerca.app.ui.Welcome.WelcomeScreen
import com.servicerca.app.ui.auth.login.Recover.RecoverPasswordScreen
import com.servicerca.app.ui.auth.login.Reset.ResetPassword
import com.servicerca.app.ui.auth.register.VerifyEmailScreen
import com.servicerca.app.ui.dashboard.moderador.ModeratorScreen
import com.servicerca.app.ui.notifications.NotificationsScreen
import com.servicerca.app.ui.reservation.ReservationScreen
import com.servicerca.app.ui.reservation.details.DetailsReservationScreen
import com.servicerca.app.ui.services.create.CreateServiceScreen
import androidx.navigation.navOptions
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.toRoute
import androidx.lifecycle.viewmodel.compose.viewModel
import com.servicerca.app.ui.auth.register.RegisterViewModel

@Composable
fun AppNavigation() {
    // Estado de la navegación, permite controlar la navegación entre pantallas
    val navController = rememberNavController()

    // Un Surface que ocupa toda la pantalla y se adapta al tema de la aplicación
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        NavHost(
            navController = navController, // Controlador de navegación
            startDestination = MainRoutes.Home // Pantalla de inicio, esta es la primer pantalla que se muestra al iniciar la aplicación
        ) {

            // Definición de las rutas y sus composables asociados (se puede agregar más rutas según sea necesario)

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
                    , onNavigateToUsers = {
                        navController.navigate(DashboardRoutes.HomeUser)
                    },
                    onRecoverPassword ={
                        navController.navigate(MainRoutes.RecoverPassword)
                    },
                    onModeratorPanel = {
                        navController.navigate(DashboardRoutes.HomeModerator)
                    }
                )
            }




            composable<MainRoutes.Register> {
                val registerViewModel: RegisterViewModel = viewModel()
                RegisterScreen(
                    viewModel = registerViewModel,
                    onNavigateToLogin = {
                        navController.navigate(MainRoutes.Login)
                    },
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onVerifyEmail = {
                        navController.navigate(MainRoutes.VerifyEmail(email = registerViewModel.email.value))
                    }
                )
            }

            // --- Usuario (Dashboard) ---
            composable<DashboardRoutes.HomeUser> {
                UserScreen(
                    onLogout = {
                        // Navegar al Login desde el NavHost raíz y limpiar el back stack de manera segura
                        navController.navigate(MainRoutes.Login) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    },
                    onCreateService ={
                        navController.navigate(MainRoutes.CreateService)
                    },

                    onNotificationClick = {
                        navController.navigate(MainRoutes.Notifications)
                    },
                    onMapClick = {
                        navController.navigate(MainRoutes.Map)
                    }

                )
            }

            // --- Moderador: registrar la pantalla principal del moderador en el NavHost raíz ---
            composable<DashboardRoutes.HomeModerator> {
                ModeratorScreen(
                    onLogout = {
                        navController.navigate(MainRoutes.Login) {
                            popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                        }
                    },
                    onCreateService = {
                        navController.navigate(MainRoutes.CreateService)
                    },
                    onNotificationClick = {
                        navController.navigate(MainRoutes.Notifications)
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
                    onNavigateToResetPassword = {
                        navController.navigate(MainRoutes.Reset)


                    }
                )
            }

            composable<MainRoutes.Reset> {
                ResetPassword(
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







    } }}
