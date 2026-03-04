package com.servicerca.app.ui.services.ListService

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.servicerca.app.R
import com.servicerca.app.core.components.card.MyServiceCard
import com.servicerca.app.core.components.navigation.TabItemApp
import com.servicerca.app.ui.theme.ServiCercaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListServiceScreen(
    onBackClick: () -> Unit = {},
    onEditService: (String) -> Unit = {},
    onDeleteService: (String) -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf(
        stringResource(id = R.string.tab_active),
        stringResource(id = R.string.tab_pending),
        stringResource(id = R.string.tab_completed)
    )

    val services = listOf(
        MyServiceItem(
            id = "1",
            title = "Plomeria Residencial",
            description = "Reparación experta de tuberías, grifería y filtraciones en hogares.",
            status = stringResource(id = R.string.status_active),
            imageRes = R.drawable.plumber
        ),
        MyServiceItem(
            id = "2",
            title = "Electricista Certificado",
            description = "Mantenimiento preventivo e Instalaciones eléctricas de alta...",
            status = stringResource(id = R.string.status_active),
            imageRes = R.drawable.service
        )
    )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .background(MaterialTheme.colorScheme.background)
        ) {


            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically

            ){
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
                Text(
                    text = stringResource(id = R.string.my_services_title),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

            }

            ServiceTabRow(
                selectedTabIndex = selectedTab,
                onTabSelected = { selectedTab = it }
            )


            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(services) { service ->
                    MyServiceCard(
                        service = service,
                        onEdit = { onEditService(service.id) },
                        onDelete = { onDeleteService(service.id) }
                    )
                }
            }
        }
    }


// TODO info provisional
data class MyServiceItem(
    val id: String,
    val title: String,
    val description: String,
    val status: String,
    val imageRes: Int
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
        Row(modifier = Modifier.fillMaxSize()) {
            TabItemApp(
                text = "Activos",
                isSelected = selectedTabIndex == 0,
                onClick = { onTabSelected(0) },
                modifier = Modifier.weight(1f)
            )
            TabItemApp(
                text = "Inactivos",
                isSelected = selectedTabIndex == 1,
                onClick = { onTabSelected(1) },
                modifier = Modifier.weight(1f)
            )

        }
    }
}

@Composable
@Preview(showBackground = true)
fun ListServiceScreenPreview() {
    ServiCercaTheme {
        ListServiceScreen()
    }
}