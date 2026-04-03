package com.servicerca.app.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.servicerca.app.R
import com.servicerca.app.core.components.button.PrimaryButton
import com.servicerca.app.core.components.input.AppPasswordField
import com.servicerca.app.core.utils.RequestResult
import kotlinx.coroutines.delay

@Composable
fun UpdatePasswordScreen(
    onBack: () -> Unit,
    viewModel: UpdatePasswordViewModel = viewModel()
) {
    val updateResult by viewModel.updateResult.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    LaunchedEffect(updateResult) {
        updateResult?.let { result ->
            val message = when (result) {
                is RequestResult.Success -> result.message
                is RequestResult.Failure -> result.errorMessage
                else -> {}
            }

            keyboardController?.hide()
            focusManager.clearFocus()
            snackBarHostState.showSnackbar(
                message = message as String,
                duration = SnackbarDuration.Short
            )

            if (result is RequestResult.Success) {
                delay(300)
                viewModel.resetForm()
                onBack()
            }

            viewModel.resetUpdateResult()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(snackBarHostState) { data ->
                val isError = updateResult is RequestResult.Failure
                Snackbar(
                    containerColor = if (isError) MaterialTheme.colorScheme.error
                                     else MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ) {
                    Text(data.visuals.message)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Header: ícono a la izquierda, título centrado
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    IconButton(
                        onClick = { onBack() },
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }

                    Text(
                        text = stringResource(R.string.change_password),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.outline)
            }

            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = stringResource(R.string.message_change_password),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // Leyenda de campos obligatorios
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = MaterialTheme.colorScheme.error)) { append("*") }
                    append(" Campos obligatorios")
                },
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Column(Modifier.fillMaxWidth()) {
                AppPasswordField(
                    password = viewModel.currentPassword.value,
                    onPasswordChange = { viewModel.currentPassword.onChange(it) },
                    label = stringResource(R.string.current_password),
                    modifier = Modifier.padding(bottom = 4.dp),
                    required = true
                )
                viewModel.currentPassword.error?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, bottom = 12.dp)
                    )
                } ?: Box(Modifier.padding(bottom = 16.dp))

                AppPasswordField(
                    password = viewModel.newPassword.value,
                    onPasswordChange = { viewModel.newPassword.onChange(it) },
                    label = stringResource(R.string.new_password),
                    modifier = Modifier.padding(bottom = 4.dp),
                    required = true
                )
                viewModel.newPassword.error?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, bottom = 12.dp)
                    )
                } ?: Box(Modifier.padding(bottom = 16.dp))

                AppPasswordField(
                    password = viewModel.confirmNewPassword.value,
                    onPasswordChange = { viewModel.confirmNewPassword.onChange(it) },
                    label = stringResource(R.string.confirm_new_password),
                    modifier = Modifier.padding(bottom = 4.dp),
                    required = true
                )
                viewModel.confirmNewPassword.error?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, bottom = 12.dp)
                    )
                } ?: Box(Modifier.padding(bottom = 16.dp))
            }

            Box(
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                PrimaryButton(
                    text = stringResource(R.string.btn_update_password),
                    onClick = { viewModel.updatePassword() }
                )
            }
        }
    }
}

@Preview
@Composable
fun UpdatePasswordScreenPreview() {
    UpdatePasswordScreen(
        onBack = {}
    )
}