package com.servicerca.app.ui.profile

import androidx.compose.ui.graphics.Color
import com.servicerca.app.R
import com.servicerca.app.domain.model.Insignia

/**
 * UI-specific model for an Insignia/Badge.
 * Contains strings, icons, and colors for rendering.
 */
data class InsigniaUiModel(
    val id: String,
    val nameRes: Int,
    val descriptionRes: Int,
    val iconRes: Int,
    val shadowColor: Color,
    val isEarned: Boolean
)

/**
 * Mapper to transform Domain model to UI model.
 */
fun Insignia.toUiModel(): InsigniaUiModel {
    return when (id) {
        "verified" -> InsigniaUiModel(
            id = id,
            nameRes = R.string.insignia_verified,
            descriptionRes = R.string.desc_insignia_verified,
            iconRes = R.drawable.insignia_verificado,
            shadowColor = Color.Cyan,
            isEarned = isEarned
        )
        "trusted" -> InsigniaUiModel(
            id = id,
            nameRes = R.string.insignia_trusted,
            descriptionRes = R.string.desc_insignia_trusted,
            iconRes = R.drawable.insignia_confiable,
            shadowColor = Color(0xFF1ABC9C),
            isEarned = isEarned
        )
        "50_services" -> InsigniaUiModel(
            id = id,
            nameRes = R.string.insignia_services,
            descriptionRes = R.string.desc_insignia_50_services,
            iconRes = R.drawable.insignia_servicios,
            shadowColor = Color(0xFF5B9BD5),
            isEarned = isEarned
        )
        "fast" -> InsigniaUiModel(
            id = id,
            nameRes = R.string.insignia_fast,
            descriptionRes = R.string.desc_insignia_fast,
            iconRes = R.drawable.insignia_rapido,
            shadowColor = Color(0xFF9C27B0),
            isEarned = isEarned
        )
        "top_5" -> InsigniaUiModel(
            id = id,
            nameRes = R.string.insignia_top5,
            descriptionRes = R.string.desc_insignia_top5,
            iconRes = R.drawable.insignia_top5,
            shadowColor = Color(0xFFFFEB3B),
            isEarned = isEarned
        )
        "favorite" -> InsigniaUiModel(
            id = id,
            nameRes = R.string.insignia_favorite,
            descriptionRes = R.string.desc_insignia_favorite,
            iconRes = R.drawable.insignia_favorita,
            shadowColor = Color.Red,
            isEarned = isEarned
        )
        "expert" -> InsigniaUiModel(
            id = id,
            nameRes = R.string.insignia_expert,
            descriptionRes = R.string.desc_insignia_expert,
            iconRes = R.drawable.insignia_ubicacion,
            shadowColor = Color(0xFFF5A623),
            isEarned = isEarned
        )
        "chat" -> InsigniaUiModel(
            id = id,
            nameRes = R.string.insignia_chat,
            descriptionRes = R.string.desc_insignia_chat,
            iconRes = R.drawable.insignia_chat,
            shadowColor = Color(0xFFE91E8C),
            isEarned = isEarned
        )
        "eco" -> InsigniaUiModel(
            id = id,
            nameRes = R.string.insignia_eco,
            descriptionRes = R.string.desc_insignia_eco,
            iconRes = R.drawable.insignia_eco,
            shadowColor = Color(0xFF34A853),
            isEarned = isEarned
        )
        else -> InsigniaUiModel(
            id = id,
            nameRes = R.string.ver_todas,
            descriptionRes = R.string.desc_insignia_locked,
            iconRes = R.drawable.insignia_verificado,
            shadowColor = Color.Gray,
            isEarned = isEarned
        )
    }
}
