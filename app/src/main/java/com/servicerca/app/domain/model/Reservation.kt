package com.servicerca.app.domain.model

import java.util.Date

data class Reservation(
    val id: String,
    val serviceId: String,
    val serviceTitle: String,
    val userId: String,
    val providerId: String,
    val date: Date,
    val time: String,
    val status: ReservationStatus
)
