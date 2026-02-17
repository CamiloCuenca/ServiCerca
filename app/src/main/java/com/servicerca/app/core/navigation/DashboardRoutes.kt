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
    data class UserDetail(val userId : String) : DashboardRoutes()
}