package com.servicerca.app.ui.notifications

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.servicerca.app.R
import com.servicerca.app.core.components.notifications.ContainerNotifications

@Composable
fun NotificationsScreen (
    onBack: () -> Unit,
){
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {

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
                        text = stringResource(R.string.notifications),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.Center)
                    )
                }
                HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.outline)
            }

            ContainerNotifications(
                imageRes = R.drawable.nueva_solicitud_servicio,
                tittleNotification = stringResource(R.string.tittle1),
                date = stringResource(R.string.date1),
                content = stringResource(R.string.content1),
                imageRes2 = R.drawable.nueva_notificacion,
            )
            ContainerNotifications(
                imageRes = R.drawable.comentario_recibido,
                tittleNotification = stringResource(R.string.tittle2),
                date = stringResource(R.string.date2),
                content = stringResource(R.string.content2),
                imageRes2 = R.drawable.nueva_notificacion,
            )
            ContainerNotifications(
                imageRes = R.drawable.servicio_verificado,
                tittleNotification = stringResource(R.string.tittle3),
                date = stringResource(R.string.date3),
                content = stringResource(R.string.content3)
            )
            ContainerNotifications(
                imageRes = R.drawable.publicacion_rechazada,
                tittleNotification = stringResource(R.string.tittle4),
                date = stringResource(R.string.date4),
                content = stringResource(R.string.content4)
            )
            ContainerNotifications(
                imageRes = R.drawable.nueva_publicacion,
                tittleNotification = stringResource(R.string.tittle5),
                date = stringResource(R.string.date5),
                content = stringResource(R.string.content5)
            )
        }
    }
}

@Preview
@Composable
fun NotificationsScreenPreview(){
    NotificationsScreen(
        onBack = {}
    )
}