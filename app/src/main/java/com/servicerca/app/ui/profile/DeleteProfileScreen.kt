package com.servicerca.app.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.servicerca.app.R
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.delay
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.hilt.navigation.compose.hiltViewModel
import com.servicerca.app.core.components.button.ButtonIcon
import com.servicerca.app.core.components.button.DeleteButton
import com.servicerca.app.core.components.card.CardInfoDeleteProfile

@Composable
fun DeleteProfileScreen (
    onBack: () -> Unit,
    onDeleteSuccess: () -> Unit,
    viewModel: DeleteProfileViewModel = hiltViewModel()
){
    val uiState by viewModel.uiState.collectAsState()
    var password by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val successMessage = stringResource(R.string.account_deleted_success)

    // Redirigir si la eliminación fue exitosa con retraso y mensaje
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            snackbarHostState.showSnackbar(successMessage)
            delay(2000) // Esperar 2 segundos
            onDeleteSuccess()
        }
    }

    // Diálogo de confirmación inicial
    if (uiState.showConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onDismissConfirmationDialog() },
            title = { Text(text = stringResource(R.string.delete_account_confirmation_title)) },
            text = { Text(text = stringResource(R.string.question_confirm_delete_profile)) },
            confirmButton = {
                TextButton(onClick = { viewModel.onConfirmDelete() }) {
                    Text(text = stringResource(R.string.delete_button))
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onDismissConfirmationDialog() }) {
                    Text(text = stringResource(R.string.delete_account_cancel_button))
                }
            }
        )
    }

    // Diálogo de re-autenticación (Contraseña)
    if (uiState.showReAuthDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onDismissReAuthDialog() },
            title = { Text(text = stringResource(R.string.delete_account_password_label)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = stringResource(R.string.delete_account_reauth_message))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text(stringResource(R.string.passwordLabel)) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        isError = uiState.error != null
                    )
                    if (uiState.error != null) {
                        Text(
                            text = uiState.error ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.deleteAccount(password) },
                    enabled = !uiState.isLoading && password.isNotBlank()
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    } else {
                        Text(text = stringResource(R.string.delete_account_confirm_button))
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onDismissReAuthDialog() }) {
                    Text(text = stringResource(R.string.delete_account_cancel_button))
                }
            }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
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
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center

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
                        text = stringResource(R.string.delete_profile),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.Center)
                    )
                }
                HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.outline)

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 30.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.BottomEnd,
                        modifier = Modifier.size(150.dp)
                    ) {

                        Image(
                                painter = painterResource(id = R.drawable.eliminar_perfil),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                    }
                }
            Row(
                Modifier.fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Surface(
                color = Color(0xFFFDDFDF),
                shape = RoundedCornerShape(12.dp), //  cuadrado con esquinas redondeadas
                tonalElevation = 2.dp,
            ){
                Text(
                    text = stringResource(R.string.important_notice),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 6.dp)

                    )
                }
            }
            Row(
            ) {
                Text(
                    text = stringResource(R.string.question_confirm_delete_profile),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .padding(horizontal = 12.dp)

                )
            }
            Row() {
                Text(
                    text = stringResource(R.string.information_delete_prefil),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .padding(horizontal = 12.dp)

                )
            }

            CardInfoDeleteProfile()

            ButtonIcon(
                text = stringResource(R.string.no_delete_account),
                onClick = { onBack()},
                icon = {
                    Icon(
                        imageVector = Icons.Default.Favorite, // Icono de Material
                        contentDescription = null
                    )
                }
            )
            DeleteButton(
                text = stringResource(R.string.delete_account_permanently),
                onClick = { viewModel.onShowConfirmationDialog() },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Delete, // Icono de Material
                        contentDescription = null
                    )
                }
            )

        }
    }
}


@Preview
@Composable
fun DeleteProfileScreenPreview(){
    DeleteProfileScreen(
        onBack = {},
        onDeleteSuccess = {}
    )
}
