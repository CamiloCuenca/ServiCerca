package com.servicerca.app.ui.dashboard.moderador

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.servicerca.app.R
import com.servicerca.app.core.components.card.CardProfileManage
import com.servicerca.app.core.components.card.CardStatisticsUserManage
import com.servicerca.app.core.components.input.SearchTextField
import com.servicerca.app.core.components.navigation.ScrollPage

@Composable
fun ManageUserScreen(){

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SearchTextField(
            query = "",
            onQueryChange = {},
            placeholder = stringResource(R.string.label_search_user)
        )

        Spacer(modifier = Modifier.height(16.dp)) // Espacio para el filtro

        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CardStatisticsUserManage(
                    number = "12952",
                    label = stringResource(R.string.label_count_all_users)
                )

                CardStatisticsUserManage(
                    number = "+69",
                    label = stringResource(R.string.label_count_new_users)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)

            ) {
                CardStatisticsUserManage(
                    number = "1204",
                    label = stringResource(R.string.label_count_active_users)
                )

                CardStatisticsUserManage(
                    number = "29",
                    label = stringResource(R.string.label_count_pending_users)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .padding(8.dp)
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CardProfileManage(
                name = stringResource(R.string.username_manage_user1),
                email = stringResource(R.string.email_manage_user1),
                imageProfile = R.drawable.foto_perfil
            )

            CardProfileManage(
                name = stringResource(R.string.username_manage_user2),
                email = stringResource(R.string.email_manage_user2),
                imageProfile = R.drawable.foto_perfil
            )

            CardProfileManage(
                name = stringResource(R.string.username_manage_user3),
                email = stringResource(R.string.email_manage_user3),
                imageProfile = R.drawable.foto_perfil
            )

            CardProfileManage(
                name = stringResource(R.string.username_manage_user4),
                email = stringResource(R.string.email_manage_user4),
                imageProfile = R.drawable.foto_perfil
            )


        }

        ScrollPage(
            currentPage = 1,
            totalPages = 5,
            onPageChange = {}
        )






    }
}

@Composable
@Preview(showBackground = true)
fun ManageUserScreenPreview(){
    ManageUserScreen()
}