package com.servicerca.app.ui.dashboard.moderador

import android.view.View
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.servicerca.app.R
import com.servicerca.app.core.components.card.CardModeratorPanelScreen
import com.servicerca.app.core.components.navigation.TabItemApp
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect

@Composable
fun ModeratorPanelScreen (navController: NavHostController,
                          onBack: () -> Unit,
                          viewModel: ModeratorPanelViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }

    LaunchedEffect(selectedTab) {
        viewModel.loadServicesByTab(selectedTab)
    }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 20.dp, bottom = 20.dp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            ServiceTabRow(
                selectedTabIndex = selectedTab,
                onTabSelected = { index ->
                    selectedTab = index
                }
            )

            // Dentro del Column...

            if (uiState.services.isEmpty()) {
                Text(
                    text = stringResource(R.string.no_services_found),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp), // Corregido: 'modifier'
                    textAlign = TextAlign.Center
                )
            } else {
                uiState.services.forEach { service ->
                    CardModeratorPanelScreen(
                        imageUrl = service.photoUrl,
                        type = service.type,
                        tittle = service.title,
                        description = service.description,
                        onVerifyClick = {
                            navController.navigate("detailsServicesModerator/${service.id}")
                        },
                        onRejectClick = {
                            navController.navigate("rejectReason/${service.id}")
                        }
                    )
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
        color = Color(0xFFF1F3F5)
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