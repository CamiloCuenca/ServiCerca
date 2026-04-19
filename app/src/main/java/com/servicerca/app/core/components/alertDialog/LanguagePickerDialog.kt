package com.servicerca.app.core.components.alertDialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.servicerca.app.R
import com.servicerca.app.data.datastore.SettingsDataStore

@Composable
fun LanguagePickerDialog(
    selectedLanguageTag: String,
    onDismiss: () -> Unit,
    onLanguageSelected: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.language_dialog_title)) },
        text = {
            Column {
                LanguageOption(
                    label = stringResource(R.string.language_option_system),
                    isSelected = selectedLanguageTag == SettingsDataStore.SYSTEM_LANGUAGE_TAG,
                    onClick = { onLanguageSelected(SettingsDataStore.SYSTEM_LANGUAGE_TAG) }
                )
                LanguageOption(
                    label = stringResource(R.string.language_option_spanish),
                    isSelected = selectedLanguageTag == "es",
                    onClick = { onLanguageSelected("es") }
                )
                LanguageOption(
                    label = stringResource(R.string.language_option_english),
                    isSelected = selectedLanguageTag == "en",
                    onClick = { onLanguageSelected("en") }
                )
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.ok_action))
            }
        }
    )
}

@Composable
private fun LanguageOption(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val suffix = if (isSelected) " \u2713" else ""
    TextButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = label + suffix)
    }
}

