package com.servicerca.app.domain.model

import androidx.annotation.StringRes
import com.servicerca.app.R

enum class Categories(
    val displayName: String,  // Valor en inglés — lo que se guarda en Firebase
    val spanishName: String,  // Valor en español — compatibilidad con datos viejos
    @StringRes val nameRes: Int
) {
    PLOMERIA("Plumbing", "Plomería", R.string.category_plumbing),
    ELECTRICIDAD("Electricity", "Electricidad", R.string.category_electricity),
    CARPINTERIA("Carpentry", "Carpintería", R.string.category_carpentry),
    PINTURA("Painting", "Pintura", R.string.category_painting),
    JARDINERIA("Gardening", "Jardinería", R.string.category_gardening),
    LIMPIEZA("Cleaning", "Limpieza", R.string.category_cleaning),
    MUDANZAS("Moving", "Mudanzas", R.string.category_moving),
    CERRAJERIA("Locksmith", "Cerrajería", R.string.category_locksmith),
    OTRO("Other", "Otro", R.string.category_other);

    // Compara contra el type guardado en Firestore (inglés o español, sin importar mayúsculas)
    fun matchesType(type: String): Boolean =
        type.equals(displayName, ignoreCase = true) ||
        type.equals(spanishName, ignoreCase = true)
}
