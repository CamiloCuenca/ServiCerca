package com.servicerca.app.core.navigation

import kotlinx.serialization.Serializable

sealed class MainRoutes {

    @Serializable
    data object Home : MainRoutes()

    @Serializable
    data object Login : MainRoutes()

    @Serializable
    data object Register : MainRoutes()

    @Serializable
    data object CreateService : MainRoutes()

    @Serializable
    data object RecoverPassword : MainRoutes()

    @Serializable
    data object VerifyEmail : MainRoutes()

    @Serializable
    data object Perfil : MainRoutes()




    @Serializable
    data object Insignias : MainRoutes()






}