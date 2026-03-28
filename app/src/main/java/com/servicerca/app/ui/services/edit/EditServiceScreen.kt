package com.servicerca.app.ui.services.edit

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.servicerca.app.R
import com.servicerca.app.core.components.button.DeleteButton
import com.servicerca.app.core.components.button.PrimaryButton
import com.servicerca.app.core.components.card.CardServiceImage
import com.servicerca.app.core.components.input.AppTextField
import com.servicerca.app.core.utils.RequestResult
import com.servicerca.app.ui.theme.Error
import com.servicerca.app.ui.theme.PrimaryLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditServiceScreen(
    onBack: () -> Unit,
    onSaveSuccess: () -> Unit = {},
    onDeleteSuccess: () -> Unit = {},
    viewModel: EditServiceViewModel = viewModel()
) {
    val saveResult by viewModel.saveResult.collectAsState()
    val deleteResult by viewModel.deleteResult.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var isError by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Resultado de guardar cambios
    LaunchedEffect(saveResult) {
        when (val result = saveResult) {
            is RequestResult.Success -> {
                isError = false
                snackbarHostState.showSnackbar(result.message)
                viewModel.resetSaveResult()
                onSaveSuccess()
            }
            is RequestResult.SuccessLogin -> Unit
            is RequestResult.Failure -> {
                isError = true
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
                snackbarHostState.showSnackbar(result.message)
                viewModel.resetDeleteResult()
                onDeleteSuccess()
            }
            is RequestResult.SuccessLogin -> Unit
            is RequestResult.Failure -> {
                isError = true
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
            title = { Text("Eliminar servicio") },
            text = { Text("¿Estás seguro de que deseas eliminar este servicio? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteService()
                    }
                ) {
                    Text(
                        text = "Eliminar",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
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
                    text = "Editar Servicio",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // ── Fotos del servicio ───────────────────────────────────────
            Text(
                text = "Fotos del servicio",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 7.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CardServiceImage()
                    CardServiceImage()
                    CardServiceImage()
                }
            }

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
                    label = { Text("Categoría") },
                    placeholder = { Text("Selecciona una categoría") },
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
                    label = "Precio mín.",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = viewModel.minValue.error != null,
                    supportingText = viewModel.minValue.error?.let { msg -> { Text(msg) } }
                )

                AppTextField(
                    value = viewModel.maxValue.value,
                    onValueChange = { viewModel.maxValue.onChange(it) },
                    modifier = Modifier.weight(1f),
                    label = "Precio máx.",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = viewModel.maxValue.error != null,
                    supportingText = viewModel.maxValue.error?.let { msg -> { Text(msg) } }
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // ── Guardar cambios ──────────────────────────────────────────
            PrimaryButton(
                text = "Guardar Cambios",
                onClick = { viewModel.saveService() }
            )

            // ── Eliminar servicio (con confirmación) ─────────────────────
            DeleteButton(
                text = "Eliminar Servicio",
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
    EditServiceScreen(onBack = {})
}
