package com.servicerca.app.ui.dashboard.moderador

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.servicerca.app.R
import com.servicerca.app.core.components.card.CardModeratorPanelScreen
import com.servicerca.app.core.components.navigation.TabItemApp
import com.servicerca.app.ui.reservation.ReservationTabRow

@Composable
fun ModeratorPanelScreen (navController: NavHostController,
                          onBack: () -> Unit) {

    var selectedTab by remember { mutableStateOf(0) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top= 20.dp , bottom = 20.dp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            ServiceTabRow(
                selectedTabIndex = selectedTab,
                onTabSelected = { selectedTab = it }
            )


            CardModeratorPanelScreen(
                imageRes = R.drawable.electrician,
                type = stringResource(R.string.type_service_moderation_panel),
                tittle = stringResource(R.string.title_service_moderation_panel),
                description = stringResource(R.string.description_service_moderation_panel),
                onVerifyClick = {
                    navController.navigate("detailsServicesModerator")
                }
            )
            CardModeratorPanelScreen(
                imageRes = R.drawable.cleaner,
                type = stringResource(R.string.type_service_moderation_panel2),
                tittle = stringResource(R.string.title_service_moderation_panel2),
                description = stringResource(R.string.description_service_moderation_panel2),
                onVerifyClick = {
                    navController.navigate("detailsServicesModerator")
                }
            )
            CardModeratorPanelScreen(
                imageRes = R.drawable.plumber2,
                type = stringResource(R.string.type_service_moderation_panel3),
                tittle = stringResource(R.string.title_service_moderation_panel3),
                description = stringResource(R.string.description_service_moderation_panel3),
                onVerifyClick = {
                    navController.navigate("detailsServicesModerator")
                }
            )
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
                text = "Pendientes",
                isSelected = selectedTabIndex == 0,
                onClick = { onTabSelected(0) },
                modifier = Modifier.weight(1f)
            )
            TabItemApp(
                text = "En revición",
                isSelected = selectedTabIndex == 1,
                onClick = { onTabSelected(1) },
                modifier = Modifier.weight(1f)
            )
            TabItemApp(
                text = "Urgentes",
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