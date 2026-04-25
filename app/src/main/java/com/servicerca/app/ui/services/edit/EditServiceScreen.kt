package com.servicerca.app.ui.services.edit

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.servicerca.app.R
import com.servicerca.app.core.components.button.DeleteButton
import com.servicerca.app.core.components.button.PrimaryButton
import com.servicerca.app.core.components.card.CardServiceImage
import com.servicerca.app.core.components.input.AppTextField
import com.servicerca.app.core.components.images.ImagesHorizontalScroller
import com.servicerca.app.core.utils.RequestResult
import com.servicerca.app.domain.repository.ServiceRepository
import com.servicerca.app.ui.services.ListService.ListServiceViewModel
import com.servicerca.app.ui.theme.Error
import com.servicerca.app.ui.theme.PrimaryLight
import java.io.InputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditServiceScreen(
    serviceId: String,
    onBack: () -> Unit,
    onSaveSuccess: () -> Unit = {},
    onDeleteSuccess: () -> Unit = {},
    viewModel: EditServiceViewModel = hiltViewModel(),
    listViewModel: ListServiceViewModel = hiltViewModel()
) {
    val saveResult by viewModel.saveResult.collectAsStateWithLifecycle()
    val deleteResult by viewModel.deleteResult.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var isError by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val services by listViewModel.services.collectAsStateWithLifecycle()
    
    LaunchedEffect(serviceId, services) {
        services.find { it.id == serviceId }?.let { service ->
            viewModel.loadService(service)
        }
    }

    // Resultado de guardar cambios
    LaunchedEffect(saveResult) {
        when (val result = saveResult) {
            is RequestResult.Success -> {
                isError = false
                keyboardController?.hide()
                focusManager.clearFocus()
                snackbarHostState.showSnackbar(result.message)
                viewModel.resetSaveResult()
                onSaveSuccess()
            }
            is RequestResult.SuccessLogin -> Unit
            is RequestResult.Failure -> {
                isError = true
                keyboardController?.hide()
                focusManager.clearFocus()
                snackbarHostState.showSnackbar(result.errorMessage)
                viewModel.resetSaveResult()
            }
            null -> Unit
        }
    }

    // Resultado de eliminar el servicio
    LaunchedEffect(deleteResult) {
        when (val result = deleteResult) {
            is RequestResult.Success -> {
                isError = false
                keyboardController?.hide()
                focusManager.clearFocus()
                snackbarHostState.showSnackbar(result.message)
                viewModel.resetDeleteResult()
                onDeleteSuccess()
            }
            is RequestResult.SuccessLogin -> Unit
            is RequestResult.Failure -> {
                isError = true
                keyboardController?.hide()
                focusManager.clearFocus()
                snackbarHostState.showSnackbar(result.errorMessage)
                viewModel.resetDeleteResult()
            }
            null -> Unit
        }
    }

    // Diálogo de confirmación de eliminación
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.delete_service_title)) },
            text = { Text(stringResource(R.string.delete_service_confirmation_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteService()
                    }
                ) {
                    Text(
                        text = stringResource(R.string.delete_button),
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.delete_account_cancel_button))
                }
            }
        )
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // ── Header ──────────────────────────────────────────────────
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
                    text = stringResource(R.string.edit_service_screen_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // ── Fotos del servicio ───────────────────────────────────────
            Text(
                text = stringResource(R.string.create_service_add_image_label),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            val images by viewModel.images.collectAsStateWithLifecycle()
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
                                viewModel.addImage(bytes)
                            }
                        } catch (e: Exception) {
                            Log.w("EditService", "failed to read image uri", e)
                        }
                    }
                }
            )

            ImagesHorizontalScroller(
                images = images,
                onAddImage = { launcher.launch("image/*") },
                onRemoveAt = { idx: Int -> viewModel.removeImageAt(idx) }
            )

            // ── Título ───────────────────────────────────────────────────
            AppTextField(
                value = viewModel.title.value,
                onValueChange = { viewModel.title.onChange(it) },
                label = stringResource(R.string.title_service_label),
                isError = viewModel.title.error != null,
                supportingText = viewModel.title.error?.let { msg -> { Text(msg) } }
            )

            // ── Categoría ────────────────────────────────────────────────
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
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
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

            // ── Descripción ──────────────────────────────────────────────
            AppTextField(
                value = viewModel.description.value,
                onValueChange = { viewModel.description.onChange(it) },
                modifier = Modifier.height(150.dp),
                label = stringResource(R.string.detailed_description_label),
                singleLine = false,
                isError = viewModel.description.error != null,
                supportingText = viewModel.description.error?.let { msg -> { Text(msg) } }
            )

            // ── Precios ──────────────────────────────────────────────────
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

            Spacer(modifier = Modifier.height(4.dp))

            // ── Guardar cambios ──────────────────────────────────────────
            PrimaryButton(
                text = stringResource(R.string.save_changes_button),
                onClick = { viewModel.saveService() }
            )

            // ── Eliminar servicio (con confirmación) ─────────────────────
            DeleteButton(
                text = stringResource(R.string.delete_service_title),
                onClick = { showDeleteDialog = true },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null
                    )
                }
            )
        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun EditServiceScreenPreview() {
    EditServiceScreen(serviceId = "1", onBack = {})
}
