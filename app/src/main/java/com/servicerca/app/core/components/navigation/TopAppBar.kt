package com.servicerca.app.core.components.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.servicerca.app.R
import com.servicerca.app.core.components.button.AppNotificationAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopAppBar(
    title: String,
    notificationCount: Int = 0,
    onLocationClick: () -> Unit,
    onNotificationClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior? = null
) {

    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = {
            Image(
                painter = painterResource(id = R.drawable.logo_servicerca),
                contentDescription = "Logo ServiCerca",
                modifier = Modifier.size(40.dp)
            )
        },


        actions = {

            IconButton(onClick = onLocationClick) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = "Ubicación"
                )
            }

            AppNotificationAction(
                count = notificationCount,
                onClick = onNotificationClick
            )
        },

        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(),

        scrollBehavior = scrollBehavior
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun AppTopAppBarPreview(){
    AppTopAppBar(
        title = "Inicio",
        notificationCount = 1,
        onLocationClick = {},
        onNotificationClick = {},
        scrollBehavior = null
    )
}


// Moderador:
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopAppBarModerator(
    title: String,
    notificationCount: Int = 0,
    onLocationClick: () -> Unit,
    onNotificationClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior? = null
) {

    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
        },
        navigationIcon = {
            Image(
                painter = painterResource(id = R.drawable.logo_servicerca),
                contentDescription = "Logo ServiCerca",
                modifier = Modifier.size(40.dp)
            )
        },


        actions = {


            AppNotificationAction(
                count = notificationCount,
                onClick = onNotificationClick
            )
        },

        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(),

        scrollBehavior = scrollBehavior
    )
}


