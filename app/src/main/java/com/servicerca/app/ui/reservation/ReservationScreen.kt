package com.servicerca.app.ui.reservation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.servicerca.app.R
import com.servicerca.app.core.components.card.CalendarCard
import com.servicerca.app.core.components.card.ReservationItem
import com.servicerca.app.core.components.navigation.TabItemApp
import com.servicerca.app.ui.theme.ServiCercaTheme

@Composable
fun ReservationScreen(modifier: Modifier = Modifier) {
    var selectedTab by remember { mutableStateOf(0) }

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
                onTabSelected = { selectedTab = it }
            )
        }

        item {
            CalendarCard()
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.reservation_today_format, "24 de Mayo"),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Surface(
                    color = Color(0xFFF1F3F5),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = stringResource(id = R.string.reservation_num_services_format, 3),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
            }
        }
        val reservations = getSampleReservations()
        items(reservations) { reservation ->
            ReservationItem(reservation = reservation)
        }
    }
}

// Composable secundario para la navegacion de tabs
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






// Datos de ejemplo para las reservas
data class ReservationData(
    val title: String,
    val time: String,
    val icon: Int
)



fun getSampleReservations(): List<ReservationData> {
    return listOf(
        ReservationData(
            title = "Plomería",
            time = "10:00 AM",
            icon = R.drawable.ic_edit
        ),
        ReservationData(
            title = "Electricista",
            time = "02:30 PM",
            icon = R.drawable.ic_mic
        ),
        ReservationData(
            title = "Limpieza",
            time = "05:00 PM",
            icon = R.drawable.ic_star
        )
    )
}


@Preview(showBackground = true)
@Composable
fun ReservationScreenPreview() {
    ServiCercaTheme {
        ReservationScreen()
    }
}
