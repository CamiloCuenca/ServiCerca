package com.servicerca.app.ui.reservation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.servicerca.app.R
import com.servicerca.app.core.components.button.PrimaryButton
import com.servicerca.app.core.components.card.CalendarCard
import com.servicerca.app.core.components.card.ReservationItem
import com.servicerca.app.core.components.navigation.TabItemApp
import com.servicerca.app.ui.theme.ServiCercaTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ReservationScreen(
    modifier: Modifier = Modifier,
    viewModel: ReservationViewModel = hiltViewModel(),
    onResevationDetails: (String) -> Unit = {},
    onQrScanner: () -> Unit = {}
) {
    val reservations by viewModel.reservations.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()

    // Formatear la fecha seleccionada para el título
    val dateTitle = remember(selectedDate) {
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd 'de' MMMM", Locale("es", "ES"))
        if (selectedDate == today) {
            "Hoy, ${selectedDate.format(formatter)}"
        } else {
            selectedDate.format(formatter).replaceFirstChar { it.uppercase() }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {

        item {
            ReservationTabRow(
                selectedTabIndex = selectedTab,
                onTabSelected = { viewModel.onTabSelected(it) }
            )
        }

        item {
            CalendarCard(
                selectedDate = selectedDate,
                onDateSelected = { viewModel.onDateSelected(it) }
            )
        }

        item {
            PrimaryButton(
                text = "Escanear QR",
                onClick = { onQrScanner() }
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dateTitle,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Surface(
                    color = Color(0xFFF1F3F5),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = stringResource(id = R.string.reservation_num_services_format, reservations.size),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
            }
        }

        if (reservations.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay reservas para este día",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
        } else {
            items(reservations) { reservation ->
                ReservationItem(
                    reservation = reservation,
                    onDetailClick = { onResevationDetails(reservation.id) }
                )
            }
        }
    }
}

@Composable
fun ReservationTabRow(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF1F3F5)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            TabItemApp(
                text = stringResource(id = R.string.reservation_tab_reserved),
                isSelected = selectedTabIndex == 0,
                onClick = { onTabSelected(0) },
                modifier = Modifier.weight(1f)
            )
            TabItemApp(
                text = stringResource(id = R.string.reservation_tab_offered),
                isSelected = selectedTabIndex == 1,
                onClick = { onTabSelected(1) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReservationScreenPreview() {
    ServiCercaTheme {
        Text("Vista previa de Reservas (Requiere ViewModel)")
    }
}
