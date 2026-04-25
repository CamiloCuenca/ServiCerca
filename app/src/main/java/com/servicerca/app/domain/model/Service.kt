package com.servicerca.app.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class Service(
    val id: String,
    val title: String,
    val description: String,
    val location: Location,
    val priceMin: Double,
    val priceMax: Double,
    val status: ServiceStatus,
    val type: String,
    val photoUrl: String,
    val ownerId: String,
    val likedBy: List<String> = emptyList()
)
