package com.servicerca.app.core.components.alertDialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun SuccessAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    title: String,
    message: String
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { 
            Text(
                text = title, 
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold 
            ) 
        },
        text = { 
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            ) 
        },
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text(
                    text = "Aceptar",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    )
}

@Preview
@Composable
fun SuccessAlertDialogPreview() {
    SuccessAlertDialog(
        onDismissRequest = {},
        onConfirm = {},
        title = "Éxito",
        message = "Operación realizada correctamente"
    )
}