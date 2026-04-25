package com.servicerca.app.ui.profile

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.servicerca.app.R
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.servicerca.app.core.components.alertDialog.SuccessAlertDialog
import com.servicerca.app.core.components.button.PrimaryButton
import com.servicerca.app.core.components.card.CardInfoprofile
import com.servicerca.app.core.components.input.AppTextField
import com.servicerca.app.core.utils.RequestResult

import java.io.InputStream

@Composable
fun EditProfileScreen(
    onBack: () -> Unit,
    onSaveSuccess: () -> Unit = {},
    viewModel: EditProfileViewModel = hiltViewModel()
) {
    val saveResult by viewModel.saveResult.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val profilePictureUrl by viewModel.profilePictureUrl.collectAsStateWithLifecycle()
    val email by viewModel.email.collectAsStateWithLifecycle()
    val role by viewModel.role.collectAsStateWithLifecycle()
    var showSuccessDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    // Reacciona al resultado de guardar
    LaunchedEffect(saveResult) {
        val result = saveResult ?: return@LaunchedEffect
        when (result) {
            is RequestResult.Success -> {
                keyboardController?.hide()
                focusManager.clearFocus()
                showSuccessDialog = true
                viewModel.resetSaveResult()
            }
            is RequestResult.Failure -> {
                keyboardController?.hide()
                focusManager.clearFocus()
                snackbarHostState.showSnackbar(result.errorMessage)
                viewModel.resetSaveResult()
            }
            is RequestResult.SuccessLogin -> {
                viewModel.resetSaveResult()
            }
        }
    }

    if (showSuccessDialog) {
        SuccessAlertDialog(
            onDismissRequest = { 
                showSuccessDialog = false
                onSaveSuccess()
            },
            onConfirm = {
                showSuccessDialog = false
                onSaveSuccess()
            },
            title = stringResource(R.string.title_update_profile),
            message = stringResource(R.string.message_update_profile)
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                val isError = saveResult is RequestResult.Failure
                Snackbar(
                    snackbarData = data,
                    containerColor = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { paddingValues ->
        if (isLoading && viewModel.firstName.value.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                Column(modifier = Modifier.fillMaxWidth()) {

                    // ── Header ──
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
                            text = stringResource(R.string.update_profile),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    // ── Avatar ──
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 30.dp)
                    ) {
                        val context = LocalContext.current
                        val launcher = rememberLauncherForActivityResult(
                            contract = ActivityResultContracts.GetContent(),
                            onResult = { uri ->
                                if (uri != null) {
                                    try {
                                        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                                        val bytes = inputStream?.readBytes()
                                        inputStream?.close()
                                        if (bytes != null) {
                                            viewModel.onPictureChanged(bytes)
                                        }
                                    } catch (e: Exception) {
                                        Log.e("EditProfile", "Error loading image", e)
                                    }
                                }
                            }
                        )

                        Box(
                            contentAlignment = Alignment.BottomEnd,
                            modifier = Modifier.size(150.dp)
                        ) {
                            Card(
                                shape = CircleShape,
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                modifier = Modifier
                                    .size(150.dp)
                                    .shadow(elevation = 2.dp, shape = CircleShape)
                                    .clickable { launcher.launch("image/*") }
                            ) {
                                AsyncImage(
                                    model = profilePictureUrl,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    placeholder = painterResource(id = R.drawable.logo_profile),
                                    error = painterResource(id = R.drawable.logo_profile),
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            // Botón cámara
                            IconButton(
                                onClick = { launcher.launch("image/*") },
                                modifier = Modifier
                                    .size(45.dp)
                                    .offset(x = 5.dp, y = 5.dp)
                                    .shadow(8.dp, CircleShape)
                                    .background(
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = CircleShape
                                    )
                                    .border(
                                        width = 3.dp,
                                        color = MaterialTheme.colorScheme.surface,
                                        shape = CircleShape
                                    )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = "Cambiar foto",
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = stringResource(R.string.update_image),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                    )

                    // ── Formulario ──
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 8.dp)
                    ) {
                        // Nombres
                        Row(
                            modifier = Modifier.padding(bottom = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Column(modifier = Modifier.weight(1F)) {
                                AppTextField(
                                    value = viewModel.firstName.value,
                                    onValueChange = { viewModel.firstName.onChange(it) },
                                    label = stringResource(R.string.first_name),
                                    isError = viewModel.firstName.error != null,
                                    supportingText = viewModel.firstName.error?.let { msg -> { Text(msg) } }
                                )
                            }
                            Column(modifier = Modifier.weight(1F)) {
                                AppTextField(
                                    value = viewModel.middleName.value,
                                    onValueChange = { viewModel.middleName.onChange(it) },
                                    label = stringResource(R.string.middle_name),
                                    isError = viewModel.middleName.error != null,
                                    supportingText = viewModel.middleName.error?.let { msg -> { Text(msg) } }
                                )
                            }
                        }

                        // Apellidos
                        Row(
                            modifier = Modifier.padding(bottom = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Column(modifier = Modifier.weight(1F)) {
                                AppTextField(
                                    value = viewModel.firstLastName.value,
                                    onValueChange = { viewModel.firstLastName.onChange(it) },
                                    label = stringResource(R.string.first_last_name),
                                    isError = viewModel.firstLastName.error != null,
                                    supportingText = viewModel.firstLastName.error?.let { msg -> { Text(msg) } }
                                )
                            }
                            Column(modifier = Modifier.weight(1F)) {
                                AppTextField(
                                    value = viewModel.secondLastName.value,
                                    onValueChange = { viewModel.secondLastName.onChange(it) },
                                    label = stringResource(R.string.second_last_name),
                                    isError = viewModel.secondLastName.error != null,
                                    supportingText = viewModel.secondLastName.error?.let { msg -> { Text(msg) } }
                                )
                            }
                        }

                        // Dirección
                        Column(modifier = Modifier.padding(bottom = 20.dp)) {
                            AppTextField(
                                value = viewModel.address.value,
                                onValueChange = { viewModel.address.onChange(it) },
                                label = stringResource(R.string.address),
                                isError = viewModel.address.error != null,
                                supportingText = viewModel.address.error?.let { msg -> { Text(msg) } }
                            )
                        }

                        // Ciudad
                        Column(modifier = Modifier.padding(bottom = 20.dp)) {
                            AppTextField(
                                value = viewModel.city.value,
                                onValueChange = { viewModel.city.onChange(it) },
                                label = "Ciudad",
                                isError = viewModel.city.error != null,
                                supportingText = viewModel.city.error?.let { msg -> { Text(msg) } }
                            )
                        }
                    }

                    CardInfoprofile(email = email, role = role)

                    Box(modifier = Modifier.padding(vertical = 20.dp)) {
                        PrimaryButton(
                            text = stringResource(R.string.btn_edit_profile),
                            onClick = { viewModel.saveProfile() }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EditProfileScreenPreview() {
    Text("Vista previa de Edición de Perfil")
}
