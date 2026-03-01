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
    data object  Chat : DashboardRoutes()


    @Serializable
    data class UserDetail(val userId : String) : DashboardRoutes()

    // Moderator

    @Serializable
    data object HomeModerator : DashboardRoutes()

    @Serializable
    data object ProfileModerator : DashboardRoutes()


    @Serializable
    data object Historial : DashboardRoutes()




}

