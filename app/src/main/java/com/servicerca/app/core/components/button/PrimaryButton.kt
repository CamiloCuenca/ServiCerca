package com.servicerca.app.core.components.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 *  Boton primario utilizado para acciones principales
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor : Color = MaterialTheme.colorScheme.onPrimary,
    // alpha permite ajustar la intensidad (0f = totalmente transparente, 1f = opaco)
    alpha: Float = 1f


) {
    val resolvedContainer = if (alpha in 0f..1f && alpha != 1f) containerColor.copy(alpha = alpha) else containerColor

    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth(),
        enabled = enabled,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = resolvedContainer,
            contentColor = contentColor
        )
    ) {
        // Usar LocalContentColor para que respete contentColor provisto por Button
        Text(text = text, color = LocalContentColor.current)
    }
}

// TODO ver como implemntar los iconos de las redes sociales
/**
 * Boton para acciones secundarias  (Login con redes sociales en este caso)
 */
@Composable
fun SocialButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Text(text = text)
    }
}

/**
 * Boton de texto Outline para acciones secundarias
 */
@Composable
fun OutlineButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    borderColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.primary,
    alpha: Float = 1f
) {

    val resolvedBorder = if (alpha in 0f..1f && alpha != 1f)
        borderColor.copy(alpha = alpha)
    else
        borderColor

    val resolvedContent = if (alpha in 0f..1f && alpha != 1f)
        contentColor.copy(alpha = alpha)
    else
        contentColor

    androidx.compose.material3.OutlinedButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.5.dp, resolvedBorder),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = resolvedContent
        )
    ) {
        Text(
            text = text,
            color = LocalContentColor.current
        )
    }
}