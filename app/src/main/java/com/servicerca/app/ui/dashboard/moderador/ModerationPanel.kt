package com.servicerca.app.ui.dashboard.moderador

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.servicerca.app.R
import com.servicerca.app.core.components.card.CardModeratorPanelScreen
import com.servicerca.app.core.components.card.SkeletonServiceCard
import com.servicerca.app.core.components.navigation.TabItemApp
import com.servicerca.app.core.navigation.DashboardRoutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModeratorPanelScreen(
    navController: NavHostController,
    onBack: () -> Unit,
    viewModel: ModeratorPanelViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        // Podríamos integrar la TabRow aquí si el ViewModel la soportara
        // ServiceTabRow(selectedTabIndex = 0, onTabSelected = {})

        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = { viewModel.loadPendingServices() },
            modifier = Modifier.weight(1f)
        ) {
            if (uiState.isLoading) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(4) {
                        SkeletonServiceCard()
                    }
                }
            } else if (uiState.services.isEmpty()) {
                Text(
                    text = stringResource(R.string.no_services_found),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    textAlign = TextAlign.Center
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 20.dp, bottom = 20.dp)
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    items(uiState.services) { service ->
                        CardModeratorPanelScreen(
                            imageUrl = service.photoUrl,
                            type = service.type,
                            title = service.title,
                            description = service.description,
                            onVerifyClick = {
                                navController.navigate(DashboardRoutes.DetailServiceModerator(service.id))
                            },
                            onRejectClick = {
                                navController.navigate(DashboardRoutes.RejectReason(service.id))
                            }
                        )
                    }
                }
            }
        }
    }
}

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
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            TabItemApp(
                text = stringResource(R.string.tab_pending_moderator),
                isSelected = selectedTabIndex == 0,
                onClick = { onTabSelected(0) },
                modifier = Modifier.weight(1f)
            )
            TabItemApp(
                text = stringResource(R.string.tab_in_review_moderator),
                isSelected = selectedTabIndex == 1,
                onClick = { onTabSelected(1) },
                modifier = Modifier.weight(1f)
            )
            TabItemApp(
                text = stringResource(R.string.tab_urgent_moderator),
                isSelected = selectedTabIndex == 2,
                onClick = { onTabSelected(2) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ModeratorPanelScreenPreview() {
    ModeratorPanelScreen(
        navController = rememberNavController(),
        onBack = {}
    )
}
