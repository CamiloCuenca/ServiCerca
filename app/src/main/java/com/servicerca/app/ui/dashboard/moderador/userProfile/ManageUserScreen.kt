package com.servicerca.app.ui.dashboard.moderador.userProfile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.servicerca.app.R
import com.servicerca.app.core.components.card.CardProfileManage
import com.servicerca.app.core.components.card.CardStatisticsUserManage
import com.servicerca.app.core.components.input.SearchTextField
import com.servicerca.app.core.components.navigation.ScrollPage

@Composable
fun ManageUserScreen(
    viewModel: ManageUserViewModel = hiltViewModel(),
    onSeeProfile: (String) -> Unit = {},
    onSuspendProfile: (String) -> Unit = {},
    onDeleteProfile: (String) -> Unit = {}
) {
    val users by viewModel.users.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SearchTextField(
            query = searchQuery,
            onQueryChange = viewModel::onSearchQueryChange,
            placeholder = stringResource(R.string.label_search_user)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CardStatisticsUserManage(
                    number = users.size.toString(),
                    label = stringResource(R.string.label_count_all_users)
                )

                CardStatisticsUserManage(
                    number = "+0", // Placeholder
                    label = stringResource(R.string.label_count_new_users)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CardStatisticsUserManage(
                    number = users.size.toString(), // Placeholder
                    label = stringResource(R.string.label_count_active_users)
                )

                CardStatisticsUserManage(
                    number = "0", // Placeholder
                    label = stringResource(R.string.label_count_pending_users)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .padding(8.dp)
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(users) { user ->
                CardProfileManage(
                    name = "${user.name1} ${user.lastname1}",
                    email = user.email,
                    imageProfile = R.drawable.foto_perfil, // Debería cargarse desde URL si CardProfileManage lo soporta
                    onSeeProfile = { onSeeProfile(user.id) }
                )
            }
        }

        ScrollPage(
            currentPage = 1,
            totalPages = 1,
            onPageChange = {}
        )
    }
}

@Composable
@Preview(showBackground = true)
fun ManageUserScreenPreview() {
    ManageUserScreen()
}
