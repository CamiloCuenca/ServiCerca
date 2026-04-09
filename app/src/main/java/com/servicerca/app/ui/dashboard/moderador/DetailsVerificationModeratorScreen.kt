package com.servicerca.app.ui.dashboard.moderador

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import com.servicerca.app.core.components.card.CardBudget
import com.servicerca.app.core.components.card.CardRevisionUserService
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.HighlightOff
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.servicerca.app.R
import com.servicerca.app.core.components.button.ButtonIcon
import com.servicerca.app.core.components.button.ButtonIconDecline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import coil3.compose.AsyncImage
import com.servicerca.app.domain.model.ServiceStatus

@Composable
fun DetailsVerificationModeratorScreen(
    serviceId: String?,
    onBack: () -> Unit,
    onRejectClick: () -> Unit,
    viewModel: DetailsVerificationViewModel = hiltViewModel()
){
    val uiState by viewModel.uiState.collectAsState()
    val service = uiState.service

    LaunchedEffect(serviceId) {
        serviceId?.let { viewModel.loadService(it) }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        if (uiState.isLoading) {
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
                            text = stringResource(R.string.details_verifications),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .align(Alignment.Center)
                        )
                    }
                    HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.outline)
                    Column() {
                        AsyncImage(
                            model = service?.photoUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxWidth()
                                .padding(vertical = 16.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .height(200.dp),
                            placeholder = painterResource(id = R.drawable.electrician),
                            error = painterResource(id = R.drawable.electrician)
                        )
                        Surface(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(10.dp),
                            tonalElevation = 2.dp,
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {

                                Icon(
                                    imageVector = Icons.Outlined.VerifiedUser,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(14.dp)
                                )

                                Spacer(modifier = Modifier.width(5.dp))

                                Text(
                                    text = when (service?.status) {
                                        ServiceStatus.PENDING -> stringResource(R.string.pending_revision)
                                        ServiceStatus.IN_PROGRESS -> stringResource(R.string.status_in_review)
                                        ServiceStatus.RESOLVED -> stringResource(R.string.status_resolved)
                                        else -> stringResource(R.string.pending_revision)
                                    },
                                    textAlign = TextAlign.Start,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        Text(
                            text = service?.title ?: "",
                            textAlign = TextAlign.Start,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(vertical = 10.dp)

                        )
                        CardRevisionUserService(
                            imageUrl = uiState.owner?.profilePictureUrl,
                            label = stringResource(R.string.label_revision),
                            username = "${uiState.owner?.name1} ${uiState.owner?.lastname1}",
                            qualification = uiState.owner?.rating.toString()
                        )
                        CardBudget(
                            budget = stringResource(
                                R.string.budget_range_format,
                                service?.priceMin ?: 0.0,
                                service?.priceMax ?: 0.0
                            )
                        )
                        Column() {
                            Text(
                                text = stringResource(R.string.service_description),
                                textAlign = TextAlign.Start,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.padding(vertical = 10.dp)
                            )
                            Text(
                                text = service?.description ?: "",
                                textAlign = TextAlign.Start,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(bottom = 10.dp)
                            )
                        }
                        Column() {
                            ButtonIcon(
                                text = stringResource(R.string.approve_publication),
                                onClick = { viewModel.approveService() },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Outlined.CheckCircle, // Icono de Material
                                        contentDescription = null
                                    )
                                }
                            )
                            ButtonIconDecline(
                                text = stringResource(R.string.reject_publication),
                                onClick = onRejectClick,
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.HighlightOff, // Icono de Material
                                        contentDescription = null
                                    )
                                },
                                modifier = Modifier
                                    .padding(vertical = 10.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}


@Preview
@Composable
fun DetailsVerificationModeratorPreview() {
    DetailsVerificationModeratorScreen(
        serviceId = "1",
        onBack = {},
        onRejectClick = {}
    )
}
