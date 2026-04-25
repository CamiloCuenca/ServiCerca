package com.servicerca.app.domain.model

data class ModerationItem (
    val serviceId: String,
    val title: String,
    val resultado: String,
    val userName: String,
    val actionPerformed: String,
    val reason: String,
    val date: String,
    val time: String,
    val status: ModerationStatus
)