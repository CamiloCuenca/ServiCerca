package com.servicerca.app.domain.model

import androidx.annotation.StringRes
import com.servicerca.app.R

enum class Categories(val displayName: String, @StringRes val nameRes: Int) {
    HOGAR("Hogar", R.string.category_home),
    EDUCACIÓN("Educación", R.string.category_education),
    MASCOTAS("Mascotas", R.string.category_pets),
    TECNOLOGIA("Tecnología", R.string.category_technology),
    TRANSPORTE("Transporte", R.string.category_transport)
}