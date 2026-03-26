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

@Composable
fun ModerationHistory (){
    var selectedTab by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top= 20.dp , bottom = 20.dp)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ){
        ServiceTabRowHistory(
            selectedTabIndex = selectedTab,
            onTabSelected = { selectedTab = it }
        )
        CardModerationHistory(
            tittle = stringResource(R.string.history_tittle1),
            result = stringResource(R.string.history_result1),
            userName = stringResource(R.string.history_username1),
            actionPerformed = stringResource(R.string.history_actionPerformed1),
            reason = stringResource(R.string.history_reason1),
            date = stringResource(R.string.history_date1),
            time = stringResource(R.string.history_time1)
        )
        CardModerationHistory(
            tittle = stringResource(R.string.history_tittle2),
            result = stringResource(R.string.history_result2),
            userName = stringResource(R.string.history_username2),
            actionPerformed = stringResource(R.string.history_actionPerformed2),
            reason = stringResource(R.string.history_reason2),
            date = stringResource(R.string.history_date2),
            time = stringResource(R.string.history_time2)
        )
        CardModerationHistory(
            tittle = stringResource(R.string.history_tittle3),
            result = stringResource(R.string.history_result3),
            userName = stringResource(R.string.history_username3),
            actionPerformed = stringResource(R.string.history_actionPerformed3),
            reason = stringResource(R.string.history_reason3),
            date = stringResource(R.string.history_date3),
            time = stringResource(R.string.history_time3)
        )
        CardModerationHistory(
            tittle = stringResource(R.string.history_tittle4),
            result = stringResource(R.string.history_result4),
            userName = stringResource(R.string.history_username4),
            actionPerformed = stringResource(R.string.history_actionPerformed4),
            reason = stringResource(R.string.history_reason4),
            date = stringResource(R.string.history_date4),
            time = stringResource(R.string.history_time4)
        )
        CardModerationHistory(
            tittle = stringResource(R.string.history_tittle5),
            result = stringResource(R.string.history_result5),
            userName = stringResource(R.string.history_username5),
            actionPerformed = stringResource(R.string.history_actionPerformed5),
            reason = stringResource(R.string.history_reason5),
            date = stringResource(R.string.history_date5),
            time = stringResource(R.string.history_time5)
        )
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