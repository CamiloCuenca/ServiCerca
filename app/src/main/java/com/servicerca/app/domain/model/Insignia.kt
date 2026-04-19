package com.servicerca.app.domain.model

/**
 * Pure domain model for an Insignia/Badge.
 * Agnostic of Android resources and UI details.
 */
data class Insignia(
    val id: String, // Unique identifier like "verified", "trusted", etc.
    val isEarned: Boolean = false
)
