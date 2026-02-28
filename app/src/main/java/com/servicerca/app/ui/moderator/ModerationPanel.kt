package com.servicerca.app.ui.moderator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import com.servicerca.app.core.components.card.CardModeratorPanelScreen

@Composable
fun ModeratorPanelScreen (onBack: () -> Unit) {
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
            
            CardModeratorPanelScreen(
                imageRes = R.drawable.electrician,
                type = stringResource(R.string.type_service_moderation_panel),
                tittle = stringResource(R.string.title_service_moderation_panel),
                description = stringResource(R.string.description_service_moderation_panel)
            )
            CardModeratorPanelScreen(
                imageRes = R.drawable.cleaner,
                type = stringResource(R.string.type_service_moderation_panel2),
                tittle = stringResource(R.string.title_service_moderation_panel2),
                description = stringResource(R.string.description_service_moderation_panel2)
            )
            CardModeratorPanelScreen(
                imageRes = R.drawable.plumber2,
                type = stringResource(R.string.type_service_moderation_panel3),
                tittle = stringResource(R.string.title_service_moderation_panel3),
                description = stringResource(R.string.description_service_moderation_panel3)
            )
        }
    }
}

@Preview
@Composable
fun ModeratorPanelScreenPreview(){
    ModeratorPanelScreen(onBack = {})
}