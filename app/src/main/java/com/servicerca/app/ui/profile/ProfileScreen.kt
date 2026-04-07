package com.servicerca.app.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.servicerca.app.R
import com.servicerca.app.core.components.alertDialog.ConfirmAlertDialog
import com.servicerca.app.core.components.button.ButtonIcon
import com.servicerca.app.core.components.button.DeleteButton
import com.servicerca.app.core.components.button.PasswordButton
import com.servicerca.app.core.components.card.CardLevel
import com.servicerca.app.core.components.card.CardStatistics
import com.servicerca.app.core.components.card.ManageServicesCard
import com.servicerca.app.core.components.images.ProfileImage

@Composable
fun ProfileScreen(
    onInsignias: () -> Unit,
    onEditProflie: () -> Unit,
    onUpdatePassword: () -> Unit,
    onDeleteProfile: () -> Unit,
    onListService: () -> Unit,
    onListInteresting: () -> Unit,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val user = uiState.user
    var showConfirmDialog by remember { mutableStateOf(false) }

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Botón de Cerrar Sesión
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = { showConfirmDialog = true }) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.Logout, contentDescription = stringResource(R.string.logout_content_description))
                }
            }

            // Imagen de Perfil
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(modifier = Modifier.size(150.dp)) {
                    ProfileImage(url = user?.profilePictureUrl ?: "")
                }
            }

            // Nombre del Usuario
            Text(
                text = if (user != null) "${user.name1} ${user.lastname1}" else stringResource(R.string.profile_fallback_name),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            // Ubicación
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(25.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = user?.city ?: stringResource(R.string.location_not_available),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            // Nivel y Gestión
            CardLevel()

            ManageServicesCard(onClick = onListService)

            ManageServicesCard(
                onClick = onListInteresting,
                color = MaterialTheme.colorScheme.surface,
                borderColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onSecondary,
                borderWidth = 2.dp,
                title = stringResource(R.string.interesting_posts_title),
                description = stringResource(R.string.interesting_posts_description),
                icon = Icons.Default.Bookmark
            )

            // Insignias (Sección Estática por ahora)
            InsigniasSection(onInsignias)

            // Estadísticas
            StatisticsSection(user)

            // Botones de Configuración
            AccountSettingsSection(onEditProflie, onUpdatePassword, onDeleteProfile)
        }
    }

    if (showConfirmDialog) {
        ConfirmAlertDialog(
            onShowExitDialogChange = { showConfirmDialog = it },
            onConfirm = {
                viewModel.logout()
                onLogout()
            }
        )
    }
}

@Composable
fun InsigniasSection(onInsignias: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = stringResource(R.string.tittle_insignias), fontWeight = FontWeight.Bold)
            Text(
                text = stringResource(R.string.ver_todas),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onInsignias() }
            )
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            InsigniaItem(R.drawable.insignia_verificado, R.string.insignia_verified, Color.Cyan)
            InsigniaItem(R.drawable.insignia_rapido, R.string.insignia_fast, Color(0xFF9C27B0))
            InsigniaItem(R.drawable.insignia_top5, R.string.insignia_top5, Color(0xFFFFEB3B))
        }
    }
}

@Composable
fun InsigniaItem(imageRes: Int, labelRes: Int, shadowColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier.size(80.dp).shadow(1.dp, CircleShape, ambientColor = shadowColor, spotColor = shadowColor)
        )
        Text(text = stringResource(labelRes), style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun StatisticsSection(user: com.servicerca.app.domain.model.User?) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = stringResource(R.string.statistic), fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 8.dp, bottom = 8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            CardStatistics(
                R.drawable.servicios_completados,
                user?.completedServices?.toString() ?: "0",
                stringResource(R.string.services_completed)
            )
            CardStatistics(
                R.drawable.puntos_totales,
                user?.totalPoints?.toString() ?: "0",
                stringResource(R.string.total_points)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            CardStatistics(
                R.drawable.calificacion,
                user?.rating?.toString() ?: "0.0",
                stringResource(R.string.average_rating)
            )
            CardStatistics(
                R.drawable.tiempo_miembro,
                if (user != null) stringResource(R.string.member_time_format, user.memberSince) else "-",
                stringResource(R.string.member_time)
            )
        }
    }
}

@Composable
fun AccountSettingsSection(onEdit: () -> Unit, onUpdatePass: () -> Unit, onDelete: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp), verticalArrangement = Arrangement.spacedBy(15.dp)) {
        ButtonIcon(text = stringResource(R.string.edit_account), onClick = onEdit, icon = { Icon(Icons.Default.Edit, null) })
        PasswordButton(text = stringResource(R.string.edit_password), onClick = onUpdatePass, icon = { Icon(Icons.Default.Lock, null) })
        DeleteButton(text = stringResource(R.string.delete_account), onClick = onDelete, icon = { Icon(Icons.Default.Delete, null) })
    }
}

@Preview (showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(
        onInsignias = {},
        onEditProflie = {},
        onUpdatePassword = {},
        onDeleteProfile = {},
        onListService = {},
        onListInteresting = {},
        onLogout = {}
    )

}
