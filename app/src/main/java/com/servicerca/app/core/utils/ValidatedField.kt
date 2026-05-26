package com.servicerca.app.core.utils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class ValidatedField<T>(
    private val initialValue: T,
    private val validate: (T) -> String?
) {
    var value by mutableStateOf(initialValue)
        private set

    var showError by mutableStateOf(false)
        private set

    private var cachedValue: T? = null
    private var cachedResult: String? = null

    private fun validateCached(): String? {
        if (cachedValue != value) {
            cachedValue = value
            cachedResult = validate(value)
        }
        return cachedResult
    }

    val error: String?
        get() = if (showError) validateCached() else null

    val isValid: Boolean
        get() = validateCached() == null

    fun onChange(newValue: T) {
        value = newValue
        showError = true
    }

    fun loadInitialValue(newValue: T) {
        value = newValue
        showError = false
    }

    fun reset() {
        value = initialValue
        showError = false
    }

    fun touch() {
        showError = true
    }
}

fun validateSecurePassword(value: String): String? {
    if (value.isEmpty()) {
        return "La contraseña es obligatoria"
    }

    val missingRequirements = mutableListOf<String>()

    if (value.length < 8) {
        missingRequirements.add("mínimo 8 caracteres")
    }
    if (!value.any { it.isUpperCase() }) {
        missingRequirements.add("una mayúscula")
    }
    if (!value.any { it.isLowerCase() }) {
        missingRequirements.add("una minúscula")
    }
    if (!value.any { it.isDigit() }) {
        missingRequirements.add("un número")
    }
    if (!value.any { !it.isLetterOrDigit() }) {
        missingRequirements.add("un carácter especial (ej. @, $, !, %, *, ?, &)")
    }

    return if (missingRequirements.isNotEmpty()) {
        "Debe tener: " + missingRequirements.joinToString(", ")
    } else {
        null
    }
}