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
import androidx.compose.material3.MaterialTheme
import com.servicerca.app.R
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.servicerca.app.core.components.card.CardModerationHistory
import com.servicerca.app.core.components.navigation.TabItemApp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

@Composable
fun ModerationHistory (viewModel: ModerationHistoryViewModel = hiltViewModel()){
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()

    val historyItems by viewModel.filteredHistory.collectAsStateWithLifecycle()


    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 20.dp, bottom = 20.dp)
            .padding(horizontal =24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 1. Ponemos el TabRow como un item de la lista (o fuera si quieres que sea fijo)
        item {
            ServiceTabRowHistory(
                selectedTabIndex = selectedTab,
                onTabSelected = { viewModel.onTabSelected(it) }
            )
        }

        // 2. Renderizamos la lista de items dinámicamente
        items(historyItems) { item ->
            CardModerationHistory(
                tittle = item.title,
                result = item.resultado, // Usamos los campos de tu data class
                userName = item.userName,
                actionPerformed = item.actionPerformed,
                reason = item.reason,
                date = item.date,
                time = item.time
            )
        }
    }

}

@Composable
fun ServiceTabRowHistory(
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHighest
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            TabItemApp(
                text = "Todos",
                isSelected = selectedTabIndex == 0,
                onClick = { onTabSelected(0) },
                modifier = Modifier.weight(1f)
            )
            TabItemApp(
                text = "Aprobados",
                isSelected = selectedTabIndex == 1,
                onClick = { onTabSelected(1) },
                modifier = Modifier.weight(1f)
            )
            TabItemApp(
                text = "Rechazados",
                isSelected = selectedTabIndex == 2,
                onClick = { onTabSelected(2) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Preview (showBackground = true)
@Composable
fun ModerationHistoryPreview (){
    ModerationHistory()
}