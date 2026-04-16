package com.servicerca.app.ui.services.ListService

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalInspectionMode
import com.servicerca.app.R
import com.servicerca.app.core.components.card.MyServiceCard
import com.servicerca.app.core.components.navigation.TabItemApp
import com.servicerca.app.ui.reservation.ConfirmActionModal
import com.servicerca.app.ui.theme.ServiCercaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListServiceScreen(
    onBackClick: () -> Unit = {},
    onEditService: () -> Unit = {},
    viewModel: ListServiceViewModel? = null
) {

    var showDeleteModal by remember { mutableStateOf(false) }
    var selectedServiceId by remember { mutableStateOf<String?>(null) }
    var selectedTab by remember { mutableStateOf(0) }

    val isInPreview = LocalInspectionMode.current

    val services: List<MyServiceItem> = if (!isInPreview) {
        // Use the provided ViewModel or obtain one via Hilt
        val actualViewModel = viewModel ?: hiltViewModel<ListServiceViewModel>()
        val servicesDomain by actualViewModel.services.collectAsState()
        
        val filteredServices = servicesDomain.filter { s ->
            when (selectedTab) {
                0 -> s.status == com.servicerca.app.domain.model.ServiceStatus.APPROVED
                1 -> s.status == com.servicerca.app.domain.model.ServiceStatus.PENDING
                2 -> s.status == com.servicerca.app.domain.model.ServiceStatus.REJECTED
                3 -> s.status == com.servicerca.app.domain.model.ServiceStatus.DELETED
                else -> false
            }
        }

        filteredServices.map { s ->
            MyServiceItem(
                id = s.id,
                title = s.title,
                description = s.description,
                status = when (s.status) {
                    com.servicerca.app.domain.model.ServiceStatus.PENDING -> "Pendiente"
                    com.servicerca.app.domain.model.ServiceStatus.APPROVED -> "Aprobado"
                    com.servicerca.app.domain.model.ServiceStatus.REJECTED -> "Rechazado"
                    com.servicerca.app.domain.model.ServiceStatus.DELETED -> "Eliminado"
                },
                imageRes = R.drawable.service,
                imageUrl = s.photoUrl
            )
        }
    } else {
        // Datos de ejemplo para preview
        listOf(
            MyServiceItem(
                id = "1",
                title = "Plomería Residencial",
                description = "Reparación experta de tuberías, grifería y filtraciones en hogares.",
                status = "Activo",
                imageRes = R.drawable.plumber
            ),
            MyServiceItem(
                id = "2",
                title = "Electricista Certificado",
                description = "Mantenimiento preventivo e instalaciones eléctricas...",
                status = "Activo",
                imageRes = R.drawable.service
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {

        // HEADER
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }

            Text(
                text = stringResource(id = R.string.my_services_title),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }

        // TABS
        ServiceTabRow(
            selectedTabIndex = selectedTab,
            onTabSelected = { selectedTab = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // LISTA
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            items(services) { service ->

                // Obtener el ViewModel real para manejar acciones (si no estamos en preview)
                val actualViewModel = if (!isInPreview) (viewModel ?: hiltViewModel<ListServiceViewModel>()) else null

                MyServiceCard(
                    service = service,
                    onEdit = { onEditService() },

                    onDelete = {
                        selectedServiceId = service.id
                        showDeleteModal = true
                    }
                )
            }
        }
    }

    // MODAL DE CONFIRMACIÓN
    if (showDeleteModal) {
        val actualViewModel = if (!isInPreview) (viewModel ?: hiltViewModel<ListServiceViewModel>()) else null

        ConfirmActionModal(
            onDismiss = { showDeleteModal = false },
            onConfirm = {
                // Si tenemos un ViewModel real y un id seleccionado, eliminamos
                actualViewModel?.let { vm ->
                    selectedServiceId?.let { id ->
                        vm.deleteService(id)
                    }
                }
                showDeleteModal = false
                selectedServiceId = null
            },
            title = "¿Eliminar servicio?",
            textPrimary = "Cancelar",
            textSecondary = "Eliminar servicio"
        )
    }
}

// DATA CLASS PROVISIONAL
data class MyServiceItem(
    val id: String,
    val title: String,
    val description: String,
    val status: String,
    val imageRes: Int,
    val imageUrl: String? = null
)


@Composable
fun ServiceTabRow(
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
        val scrollState = rememberScrollState()
        Row(
            modifier = Modifier
                .fillMaxSize()
                .horizontalScroll(scrollState)
        ) {
            val tabWidth = 120.dp
            TabItemApp(
                text = "Aprobados",
                isSelected = selectedTabIndex == 0,
                onClick = { onTabSelected(0) },
                modifier = Modifier.width(tabWidth)
            )

            TabItemApp(
                text = "Pendientes",
                isSelected = selectedTabIndex == 1,
                onClick = { onTabSelected(1) },
                modifier = Modifier.width(tabWidth)
            )

            TabItemApp(
                text = "Rechazados",
                isSelected = selectedTabIndex == 2,
                onClick = { onTabSelected(2) },
                modifier = Modifier.width(tabWidth)
            )

            TabItemApp(
                text = "Eliminados",
                isSelected = selectedTabIndex == 3,
                onClick = { onTabSelected(3) },
                modifier = Modifier.width(tabWidth)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ListServiceScreenPreview() {

    ServiCercaTheme {
        ListServiceScreen()
    }
}
