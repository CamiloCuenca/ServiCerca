package com.servicerca.app.domain.model

data class ModerationItem (
    val title: String,
    val resultado: String,
    val userName: String,
    val actionPerformed: String,
    val reason: String,
    val date: String,
    val time: String,
    val status: ModerationStatus
)