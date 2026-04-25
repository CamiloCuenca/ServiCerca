package com.servicerca.app.domain.model

import java.util.Date
import androidx.compose.runtime.Immutable

@Immutable
data class Reservation(
    val id: String,
    val serviceId: String,
    val serviceTitle: String,
    val serviceImageUrl: String? = null,
    val userId: String,
    val providerId: String,
    val date: Date,
    val time: String,
    val status: ReservationStatus,
    val message: String = ""
)
