package com.servicerca.app.core.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.servicerca.app.ui.auth.login.LoginScreen
import com.servicerca.app.ui.auth.register.RegisterScreen
import com.servicerca.app.ui.dashboard.user.UserScreen
import com.servicerca.app.ui.Welcome.HomeScreen

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
                HomeScreen(
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
                    }
                )
            }

            composable<MainRoutes.Register> {
                RegisterScreen( onNavigateToLogin = {
                    navController.navigate(MainRoutes.Login)
                },)
            }

            composable<DashboardRoutes.HomeUser> {
                UserScreen(
                    onLogout = {
                        // Lógica para cerrar sesión y regresar a la pantalla de login
                        navController.navigate(MainRoutes.Login) {
                            popUpTo(MainRoutes.Login) { inclusive = true } // Evitar regresar a la pantalla anterior
                        }
                    },
                    onCreateService ={
                        navController.navigate(MainRoutes.CreateService)
                    }
                )
            }

        }
    }
}