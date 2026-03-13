package com.servicerca.app.ui.services.create

import androidx.lifecycle.ViewModel
import com.servicerca.app.core.utils.ValidatedField

class CreateServiceViewModel  : ViewModel(){

    val title = ValidatedField("") { value ->
        when {
            value.isEmpty() -> "El título es obligatorio"
            else -> null
        }
    }

    val description = ValidatedField("") { value ->
        when{
            value.isEmpty() -> "La descripción es obligatoria"
            else -> null

        }

    }

    val maxValue = ValidatedField(""){ value ->
        when{
            value.isEmpty() -> "El precio maximo es obligatoria"
            else -> null

        }
    }

    val minValue = ValidatedField(""){ value ->
        when{
            value.isEmpty() -> "El precio minimo es obligatoria"
            value.toInt() < 0 -> "El precio minimo no puede ser negativo"
            value > maxValue.value -> "El precio minimo no puede ser mayor al maximo"
            else -> null
        }
    }
}