package com.servicerca.app.core.components.alertDialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ConfirmAlertDialog(
    onShowExitDialogChange: (Boolean) -> Unit,
    onConfirm: () -> Unit

) {
    AlertDialog(
        title = { Text(text = "¿Está seguro que quieres cerrar sesión?") },
        text = { Text(text = "Está a punto de cerrar sesión.") },
        onDismissRequest = { onShowExitDialogChange(false) },
        containerColor = MaterialTheme.colorScheme.background, // color de fondo del dialogo
        titleContentColor = MaterialTheme.colorScheme.onBackground, // color del titulo
        textContentColor = MaterialTheme.colorScheme.onSurface, // color del texto
        confirmButton = {
            TextButton(
                onClick = {
                    onShowExitDialogChange(false)
                    onConfirm()
                }
            ) {
                Text("Confirmar",
                    color = MaterialTheme.colorScheme.primary)
            }
        },
        dismissButton = {
            TextButton(
                onClick = { onShowExitDialogChange(false) }
            ) {
                Text("Cancelar",
                    color = MaterialTheme.colorScheme.outline)
            }
        }
    )
}

@Preview
@Composable
fun ConfirmAlertDialogPreview() {
    ConfirmAlertDialog(
        onShowExitDialogChange = {},
        onConfirm = {}
    )
}