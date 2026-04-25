package com.servicerca.app.core.navigation

import kotlinx.serialization.Serializable

sealed class DashboardRoutes {

    @Serializable
    data object HomeUser : DashboardRoutes()

    @Serializable
    data object Search : DashboardRoutes()

    @Serializable
    data object Profile : DashboardRoutes()

    @Serializable
    data object Reservation : DashboardRoutes()

    @Serializable
    data object  ChatList : DashboardRoutes()


    @Serializable
    data class UserDetail(val userId : String) : DashboardRoutes()

    // Moderator

    @Serializable
    data object HomeModerator : DashboardRoutes()

    @Serializable
    data object ProfileModerator : DashboardRoutes()


    @Serializable
    data class Historial(val initialTab: Int = 0) : DashboardRoutes()

    @Serializable
    data object ManageUser : DashboardRoutes()

    // User sub-routes
    @Serializable
    data class DetailService(val serviceId: String) : DashboardRoutes()

    @Serializable
    data object Insignias : DashboardRoutes()

    @Serializable
    data object EditProfile : DashboardRoutes()

    @Serializable
    data object UpdatePassword : DashboardRoutes()

    @Serializable
    data object DeleteProfile : DashboardRoutes()

    @Serializable
    data object ServiceList : DashboardRoutes()

    @Serializable
    data object ListInteresting : DashboardRoutes()

    @Serializable
    data class EditService(val serviceId: String) : DashboardRoutes()

    @Serializable
    data class Chat(val chatId: String) : DashboardRoutes()

    @Serializable
    data object QrScannerDashboard : DashboardRoutes()

    @Serializable
    data class QrServiceVerification(val reservationId: String) : DashboardRoutes()

    // Moderator sub-routes
    @Serializable
    data class DetailServiceModerator(val serviceId: String) : DashboardRoutes()

    @Serializable
    data class RejectReason(val serviceId: String) : DashboardRoutes()

}

