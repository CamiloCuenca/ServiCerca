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
    data class VerifyEmail(val email: String) : MainRoutes()


    @Serializable
    data object Reset : MainRoutes()

    @Serializable
    data object Perfil : MainRoutes()

    @Serializable
    data object Notifications : MainRoutes()

    @Serializable
    data object Map : MainRoutes()

    @Serializable
    data class ReservationDetail(val reservationId: String) : MainRoutes()

    @Serializable
    data class MakeReservation(val serviceId: String) : MainRoutes()


    @Serializable
    data object ServiceList : MainRoutes()

    @Serializable
    data object QrService : MainRoutes()

    @Serializable
    data object QrScanner : MainRoutes()



    @Serializable
    data object Insignias : MainRoutes()

    @Serializable
    data class ServiceDetail(val serviceId: String) : MainRoutes()

}
