package com.servicerca.app.ui.dashboard.moderador

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.servicerca.app.R
import com.servicerca.app.core.components.alertDialog.ConfirmAlertDialog
import com.servicerca.app.core.components.card.CardMenuModerator
import com.servicerca.app.core.components.card.CardProfileModerator
import com.servicerca.app.core.components.images.ProfileImage
import com.servicerca.app.core.navigation.DashboardRoutes

@Composable
fun ProfileModerator (
    navController: NavHostController,
    onLogout: () -> Unit
){
    var showConfirmDialog by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(
                onClick = { showConfirmDialog = true }
            ) {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = "Cerrar sesión"
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {


                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        contentAlignment = Alignment.BottomEnd,
                        modifier = Modifier.size(150.dp)
                    ) {
                        ProfileImage(
                            url = "https://i.pinimg.com/originals/ae/90/f5/ae90f5c41e36d420e8175f072367ead9.jpg"
                        )
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.name_moderator),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,

                modifier = Modifier.align(Alignment.Center)
            )
        }
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {

                Text(
                    text = stringResource(R.string.position_moderator),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
        }
        CardProfileModerator(
            title = stringResource(R.string.pending_profile_moderator),
            label = stringResource(R.string.pending_number),
            color = Color(0xFFDB9C16)
        )
        CardProfileModerator(
            title = stringResource(R.string.approved_profile_moderator),
            label = stringResource(R.string.approved_number),
            color = Color(0xFF3CA834)
        )
        CardProfileModerator(
            title = stringResource(R.string.rejected_profile_moderator),
            label = stringResource(R.string.rejected_number),
            color = Color(0xFFC72E2E)
        )

        Text(
            text = stringResource(R.string.pending_profile_moderator),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )

        CardMenuModerator(
            onValidateClick = {
                navController.navigate(DashboardRoutes.HomeModerator) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            onHistoryClick = {
                navController.navigate(DashboardRoutes.Historial) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
    }
    if (showConfirmDialog) {
        ConfirmAlertDialog(
            onShowExitDialogChange = { showConfirmDialog = it },
            onConfirm = {
                onLogout()
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileModeratorPreview(){
    ProfileModerator(navController = rememberNavController() , onLogout = {})
}

