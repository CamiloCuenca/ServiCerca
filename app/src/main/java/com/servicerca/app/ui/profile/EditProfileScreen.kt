package com.servicerca.app.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.servicerca.app.R
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.hilt.navigation.compose.hiltViewModel
import com.servicerca.app.core.components.button.PrimaryButton
import com.servicerca.app.core.components.card.CardInfoprofile
import com.servicerca.app.core.components.input.AppTextField
import com.servicerca.app.core.utils.RequestResult
import com.servicerca.app.ui.theme.Error
import com.servicerca.app.ui.theme.PrimaryLight

@Composable
fun EditProfileScreen(
    onBack: () -> Unit,
    onSaveSuccess: () -> Unit = {},
    viewModel: EditProfileViewModel = hiltViewModel()
) {
    val saveResult by viewModel.saveResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var isError by remember { mutableStateOf(false) }

    // Reacciona al resultado de guardar
    LaunchedEffect(saveResult) {
        val result = saveResult ?: return@LaunchedEffect
        when (result) {
            is RequestResult.Success -> {
                isError = false
                snackbarHostState.showSnackbar(result.message)
                viewModel.resetSaveResult()
                onSaveSuccess()
            }
            is RequestResult.Failure -> {
                isError = true
                snackbarHostState.showSnackbar(result.errorMessage)
                viewModel.resetSaveResult()
            }
            is RequestResult.SuccessLogin -> {
                // No debería ocurrir aquí, pero manejamos para exhaustividad
                viewModel.resetSaveResult()
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = if (isError) Error else PrimaryLight,
                    contentColor = Color.White
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

                    HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.outline)

                    // ── Avatar ──
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 30.dp)
                    ) {
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
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.logo_profile),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            // Botón cámara
                            IconButton(
                                onClick = { /* TODO: abrir galería / cámara */ },
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

                        // Teléfono
                        Column(modifier = Modifier.padding(bottom = 20.dp)) {
                            AppTextField(
                                value = viewModel.phone.value,
                                onValueChange = { input ->
                                    viewModel.phone.onChange(input.filter { it.isDigit() })
                                },
                                label = stringResource(R.string.number_tel),
                                isError = viewModel.phone.error != null,
                                supportingText = viewModel.phone.error?.let { msg -> { Text(msg) } },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Phone,
                                        contentDescription = "Teléfono"
                                    )
                                }
                            )
                        }
                    }

                    CardInfoprofile()

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
