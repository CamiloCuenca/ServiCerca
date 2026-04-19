package com.servicerca.app.ui.reservation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowCircleUp
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.key
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.servicerca.app.R
import com.servicerca.app.core.components.card.CardMakeReservation
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MakeReservation(
    serviceId: String,
    viewModel: MakeReservationViewModel = hiltViewModel(),
    onBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(serviceId) {
        viewModel.loadServiceDetails(serviceId)
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val reservationRequestedSuccess = stringResource(R.string.reservation_requested_success)
    
    // Manejar éxito de la reserva
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            snackbarHostState.showSnackbar(
                message = reservationRequestedSuccess,
                withDismissAction = true
            )
            delay(1500)
            onBack()
        }
    }

    // Estados para los pickers y el mensaje
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedTime by remember { mutableStateOf(LocalTime.now()) }
    var message by remember { mutableStateOf("") }

    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")

    Scaffold(
        snackbarHost = { 
            SnackbarHost(hostState = snackbarHostState) { data ->
                androidx.compose.material3.Snackbar(
                    snackbarData = data,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_back)
                        )
                    }
                    Text(
                        text = stringResource(R.string.new_reservation_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                val provider = uiState.provider
                CardMakeReservation(
                    serviceImageUrl = uiState.service?.photoUrl,
                    serviceTitle = uiState.service?.title,
                    professionalName = if (provider != null) "${provider.name1} ${provider.lastname1}" else null,
                    rating = if (uiState.service != null) "$${uiState.service!!.priceMin} - $${uiState.service!!.priceMax}" else null
                )

                Column(
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.service_schedule_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Campo de Fecha
                    OutlinedTextField(
                        value = selectedDate.format(dateFormatter),
                        onValueChange = { },
                        label = { Text(stringResource(R.string.desired_date_label)) },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(Icons.Default.CalendarMonth, contentDescription = stringResource(R.string.select_date_content_description))
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .clickable { showDatePicker = true }
                    )

                    // Campo de Hora
                    OutlinedTextField(
                        value = selectedTime.format(timeFormatter),
                        onValueChange = { },
                        label = { Text(stringResource(R.string.desired_time_label)) },
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { showTimePicker = true }) {
                                Icon(Icons.Default.Schedule, contentDescription = stringResource(R.string.select_time_content_description))
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                            .clickable { showTimePicker = true }
                    )

                    Text(
                        text = stringResource(R.string.optional_message_label),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        modifier = Modifier
                            .height(150.dp)
                            .padding(bottom = 25.dp)
                            .fillMaxWidth(),
                        placeholder = {
                            Text(stringResource(R.string.reservation_optional_message_placeholder))
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedPlaceholderColor = MaterialTheme.colorScheme.outline,
                            unfocusedPlaceholderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                viewModel.confirmReservation(
                                    serviceId = serviceId,
                                    providerId = uiState.service?.ownerId ?: "",
                                    serviceTitle = uiState.service?.title ?: "",
                                    serviceImageUrl = uiState.service?.photoUrl,
                                    date = selectedDate,
                                    time = selectedTime,
                                    message = message
                                )
                            },
                            enabled = uiState.service != null && !uiState.isLoading
                        ) {
                            Text(stringResource(R.string.confirm_reservation_button))
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowCircleUp,
                                contentDescription = stringResource(R.string.confirm_action_content_description)
                            )
                        }
                        
                        TextButton(onClick = onBack) {
                            Text(stringResource(R.string.delete_account_cancel_button), color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }

    // Lógica para mostrar Pickers
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        // Usamos UTC para evitar el desfase de zona horaria
                        selectedDate = Instant.ofEpochMilli(it)
                            .atZone(ZoneId.of("UTC"))
                            .toLocalDate()
                    }
                    showDatePicker = false
                }) {
                    Text(stringResource(R.string.ok_action))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.delete_account_cancel_button))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = selectedTime.hour,
            initialMinute = selectedTime.minute
        )
        Dialog(onDismissRequest = { showTimePicker = false }) {
            androidx.compose.material3.Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.select_time_title),
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(bottom = 20.dp)
                    )
                    TimePicker(state = timePickerState)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showTimePicker = false }) {
                            Text(stringResource(R.string.delete_account_cancel_button))
                        }
                        TextButton(onClick = {
                            selectedTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
                            showTimePicker = false
                        }) {
                            Text(stringResource(R.string.ok_action))
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MakeReservationPreview() {
    Text(stringResource(R.string.new_reservation_title))
}
