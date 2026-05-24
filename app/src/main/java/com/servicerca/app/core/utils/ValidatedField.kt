package com.servicerca.app.core.utils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class ValidatedField<T>(
    private val initialValue: T,
    private val validate: (T) -> String?
) {
    // Estado del valor del campo
    var value by mutableStateOf(initialValue)
        private set

    // Estado para controlar cuándo mostrar el error
    var showError by mutableStateOf(false)
        private set

    // Mensaje de error. get() para que sea de solo lectura desde el exterior
    val error: String?
        get() = if (showError) validate(value) else null

    // Indica si el campo es válido, es de solo lectura desde el exterior
    val isValid: Boolean
        get() = validate(value) == null

    // Función para actualizar el valor del campo
    fun onChange(newValue: T) {
        value = newValue
        showError = true
    }

    // Nueva función para cargar valores iniciales sin disparar errores
    fun loadInitialValue(newValue: T) {
        value = newValue
        showError = false
    }

    // Función para resetear el campo a su valor inicial
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