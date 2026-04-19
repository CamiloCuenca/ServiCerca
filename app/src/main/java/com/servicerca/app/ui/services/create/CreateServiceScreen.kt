package com.servicerca.app.ui.services.create

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.ArrowCircleRight
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.servicerca.app.R
import com.servicerca.app.core.components.Map.MapBox
import com.servicerca.app.core.components.button.ButtonCreateService
import com.servicerca.app.core.components.images.ImagesHorizontalScroller
import com.servicerca.app.core.components.input.AppTextField
import com.servicerca.app.core.utils.RequestResult
import com.servicerca.app.ui.theme.Error
import com.servicerca.app.ui.theme.PrimaryLight
import java.io.InputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateServiceScreen(
    onBack: () -> Unit,
    onServiceCreated: () -> Unit = {},
    viewModel: CreateServiceViewModel = hiltViewModel(),
) {
    val createResult by viewModel.createResult.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var isError by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    // Reacciona al resultado de publicar el servicio
    LaunchedEffect(createResult) {
        when (val result = createResult) {
            is RequestResult.Success -> {
                isError = false
                keyboardController?.hide()
                focusManager.clearFocus()
                snackbarHostState.showSnackbar(result.message)
                viewModel.resetCreateResult()
                // Notificar al caller y regresar a la pantalla anterior
                onServiceCreated()
                onBack()
            }
            is RequestResult.SuccessLogin -> Unit // No aplica en esta pantalla
            is RequestResult.Failure -> {
                isError = true
                keyboardController?.hide()
                focusManager.clearFocus()
                snackbarHostState.showSnackbar(result.errorMessage)
                viewModel.resetCreateResult()
            }
            null -> Unit
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(15.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Column{
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
                        text = stringResource(R.string.title_create_service),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                // ── Imágenes ─────────────────────────────────────────────
                Text(
                    text = stringResource(R.string.create_service_add_image_label),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(2.dp))

                val images by viewModel.images.collectAsState()

                // Launcher para pickear imágenes desde el almacenamiento
                val context = LocalContext.current
                val launcher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.GetContent(),
                    onResult = { uri ->
                        if (uri != null) {
                            // Convertir Uri a ByteArray
                            try {
                                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                                val bytes = inputStream?.readBytes()
                                inputStream?.close()
                                if (bytes != null) {
                                    viewModel.addImage(bytes)
                                }
                                            } catch (e: Exception) {
                                                // Log para depuración si la conversión falla
                                                Log.w("CreateService", "failed to read image uri", e)
                                            }
                        }
                    }
                )

                ImagesHorizontalScroller(
                    images = images,
                    onAddImage = { launcher.launch("image/*") },
                    onRemoveAt = { idx: Int -> viewModel.removeImageAt(idx) }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // ── Título del servicio ───────────────────────────────────
                AppTextField(
                    value = viewModel.title.value,
                    onValueChange = { viewModel.title.onChange(it) },
                    label = stringResource(R.string.title_service_label),
                    isError = viewModel.title.error != null,
                    supportingText = viewModel.title.error?.let { msg -> { Text(msg) } }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // ── Categoría (dropdown con colores del tema) ─────────────
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = viewModel.category.value.ifBlank { "" },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.create_service_category_label)) },
                        placeholder = { Text(stringResource(R.string.create_service_select_category_placeholder)) },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            errorBorderColor = MaterialTheme.colorScheme.error,
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                        isError = viewModel.category.error != null,
                        supportingText = viewModel.category.error?.let { msg -> { Text(msg) } },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        containerColor = MaterialTheme.colorScheme.surface,
                        shadowElevation = 10.dp,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        viewModel.categories.forEach { option ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = option,
                                        fontWeight = FontWeight.Medium
                                    )
                                },
                                onClick = {
                                    viewModel.category.onChange(option)
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // ── Descripción ───────────────────────────────────────────
                AppTextField(
                    value = viewModel.description.value,
                    onValueChange = { viewModel.description.onChange(it) },
                    modifier = Modifier.height(150.dp),
                    label = stringResource(R.string.detailed_description_label),
                    singleLine = false,
                    isError = viewModel.description.error != null,
                    supportingText = viewModel.description.error?.let { msg -> { Text(msg) } }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Precios
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppTextField(
                        value = viewModel.minValue.value,
                        onValueChange = { viewModel.minValue.onChange(it) },
                        modifier = Modifier.weight(1f),
                        label = stringResource(R.string.price_min_label),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = viewModel.minValue.error != null,
                        supportingText = viewModel.minValue.error?.let { msg -> { Text(msg) } }
                    )

                    AppTextField(
                        value = viewModel.maxValue.value,
                        onValueChange = { viewModel.maxValue.onChange(it) },
                        modifier = Modifier.weight(1f),
                        label = stringResource(R.string.price_max_label),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = viewModel.maxValue.error != null,
                        supportingText = viewModel.maxValue.error?.let { msg -> { Text(msg) } }
                    )

                }

                Spacer(modifier = Modifier.height(10.dp))

                // ── Botón agregar ubicación ───────────────────────────────
                androidx.compose.material3.Button(
                    onClick = { /* TODO: abrir selector de mapa */ },
                    modifier = Modifier
                        .padding(vertical = 8.dp, horizontal = 4.dp)
                        .size(DpSize(width = androidx.compose.ui.unit.Dp.Infinity, height = 70.dp))
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    elevation = androidx.compose.material3.ButtonDefaults.buttonElevation(
                        pressedElevation = 8.dp,
                        defaultElevation = 4.dp,
                        hoveredElevation = 6.dp,
                        focusedElevation = 6.dp
                    )
                ) {
                    Row {
                        Icon(
                            imageVector = Icons.Default.AddLocationAlt,
                            contentDescription = stringResource(R.string.add_location_content_description),
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                        Column {
                            Text(
                                text = stringResource(R.string.title_location_service),
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.align(Alignment.Start)
                            )
                            Text(
                                text = stringResource(R.string.description_location_service),
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.align(Alignment.Start)
                            )
                        }
                        Spacer(modifier = Modifier.width(30.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowCircleRight,
                            contentDescription = stringResource(R.string.go_to_location_content_description),
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                }

                // ── Mapa ──────────────────────────────────────────────────
                MapBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // ── Botón publicar → conectado al ViewModel ───────────────
                ButtonCreateService(
                    text = stringResource(R.string.publish_service_button),
                    onClick = { viewModel.createService() },
                    modifier = Modifier.padding(vertical = 8.dp),
                    icon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = stringResource(R.string.publish_service_content_description),
                            modifier = Modifier.align(Alignment.End)
                        )
                    }

                )


            }

        }
    }

}
