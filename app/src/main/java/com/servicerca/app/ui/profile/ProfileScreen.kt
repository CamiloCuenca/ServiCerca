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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Check
import com.servicerca.app.data.datastore.AppThemeMode
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.servicerca.app.R
import com.servicerca.app.core.components.alertDialog.ConfirmAlertDialog
import com.servicerca.app.core.components.alertDialog.LanguagePickerDialog
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
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedLanguageTag by viewModel.selectedLanguageTag.collectAsStateWithLifecycle()
    val selectedThemeMode by viewModel.selectedThemeMode.collectAsStateWithLifecycle()
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var selectedInsignia by remember { mutableStateOf<InsigniaUiModel?>(null) }

    when (val state = uiState) {
        is ProfileUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is ProfileUiState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = state.message, color = MaterialTheme.colorScheme.error)
            }
        }
        is ProfileUiState.Success -> {
            val user = state.user
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
                        ProfileImage(url = user.profilePictureUrl)
                    }
                }

                // Nombre del Usuario
                Text(
                    text = "${user.name1} ${user.lastname1}",
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
                        text = user.city,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                // Nivel y Gestión
                CardLevel(
                    level = state.level,
                    levelName = state.levelName,
                    currentXp = state.totalXp,
                    nextLevelXp = state.xpRequiredForNextLevel,
                    progress = state.progress,
                    remainingXp = (state.xpRequiredForNextLevel - state.totalXp).coerceAtLeast(0)
                )

                ManageServicesCard(onClick = onListService)

                ManageServicesCard(
                    onClick = onListInteresting,
                    color = MaterialTheme.colorScheme.surface,
                    borderColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    borderWidth = 2.dp,
                    title = stringResource(R.string.interesting_posts_title),
                    description = stringResource(R.string.interesting_posts_description),
                    icon = Icons.Default.Bookmark
                )

                // Insignias Dinámicas
                val earnedInsignias = state.insignias.filter { it.isEarned }
                InsigniasSection(
                    insignias = earnedInsignias,
                    onInsignias = onInsignias,
                    onInsigniaClick = { selectedInsignia = it }
                )

                // Estadísticas
                StatisticsSection(state)

                // Botones de Configuración
                AccountSettingsSection(
                    onEdit = onEditProflie,
                    onUpdatePass = onUpdatePassword,
                    onDelete = onDeleteProfile,
                    onChangeLanguage = { showLanguageDialog = true },
                    onChangeTheme = { showThemeDialog = true }
                )
            }
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

    selectedInsignia?.let { insignia ->
        InsigniaDetailDialog(
            insignia = insignia,
            onDismiss = { selectedInsignia = null }
        )
    }

    if (showLanguageDialog) {
        LanguagePickerDialog(
            selectedLanguageTag = selectedLanguageTag,
            onDismiss = { showLanguageDialog = false },
            onLanguageSelected = { tag ->
                viewModel.setLanguage(tag)
                showLanguageDialog = false
            }
        )
    }

    if (showThemeDialog) {
        ThemePickerDialog(
            selectedThemeMode = selectedThemeMode,
            onDismiss = { showThemeDialog = false },
            onThemeSelected = { mode ->
                viewModel.setThemeMode(mode)
                showThemeDialog = false
            }
        )
    }
}

@Composable
fun InsigniasSection(
    insignias: List<InsigniaUiModel>,
    onInsignias: () -> Unit,
    onInsigniaClick: (InsigniaUiModel) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = stringResource(R.string.title_insignias), fontWeight = FontWeight.Bold)
            Text(
                text = stringResource(R.string.ver_todas),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onInsignias() }
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Mostramos un resumen de hasta 3 insignias
            val displayInsignias = insignias.take(3)
            displayInsignias.forEach { insignia ->
                Box(modifier = Modifier.weight(1f)) {
                    InsigniaItem(
                        insignia = insignia,
                        onClick = { onInsigniaClick(insignia) }
                    )
                }
            }
            // Espaciado si hay menos de 3
            repeat(3 - displayInsignias.size) {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun InsigniaItem(
    insignia: InsigniaUiModel,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Image(
            painter = painterResource(id = insignia.iconRes),
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .shadow(
                    elevation = 2.dp,
                    shape = CircleShape,
                    ambientColor = insignia.shadowColor,
                    spotColor = insignia.shadowColor
                )
        )
        Text(
            text = stringResource(insignia.nameRes),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}


@Composable
fun StatisticsSection(state: ProfileUiState.Success) {
    val user = state.user
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = stringResource(R.string.statistic), fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 8.dp, bottom = 8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            CardStatistics(
                R.drawable.servicios_completados,
                user.completedServices.toString(),
                stringResource(R.string.services_completed)
            )
            CardStatistics(
                R.drawable.puntos_totales,
                state.totalXp.toString(),
                stringResource(R.string.total_points)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            CardStatistics(
                R.drawable.calificacion,
                String.format("%.1f", state.averageRating),
                stringResource(R.string.average_rating)
            )
            CardStatistics(
                R.drawable.tiempo_miembro,
                run {
                    val currentYear = java.time.Year.now().value
                    val years = (currentYear - user.memberSince).coerceAtLeast(0)
                    val yearsText = if (years == 1) stringResource(R.string.member_time_year_singular)
                                    else stringResource(R.string.member_time_year_plural, years)
                    "$yearsText (${user.memberSince})"
                },
                stringResource(R.string.member_time)
            )
        }
    }
}


@Composable
fun AccountSettingsSection(
    onEdit: () -> Unit,
    onUpdatePass: () -> Unit,
    onDelete: () -> Unit,
    onChangeLanguage: () -> Unit,
    onChangeTheme: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp), verticalArrangement = Arrangement.spacedBy(15.dp)) {
        ButtonIcon(
            text = "Cambiar Tema / Apariencia",
            onClick = onChangeTheme,
            icon = { Icon(Icons.Default.Palette, null) }
        )
        ButtonIcon(
            text = stringResource(R.string.language_button),
            onClick = onChangeLanguage,
            icon = { Icon(Icons.Default.Language, null) }
        )
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

@Composable
fun ThemePickerDialog(
    selectedThemeMode: AppThemeMode,
    onDismiss: () -> Unit,
    onThemeSelected: (AppThemeMode) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        title = { 
            Text(
                text = "Seleccionar Tema", 
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            ) 
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                ThemeOptionItem("Por defecto del Sistema", AppThemeMode.SYSTEM_DEFAULT, selectedThemeMode, onThemeSelected)
                ThemeOptionItem("Modo Claro", AppThemeMode.LIGHT, selectedThemeMode, onThemeSelected)
                ThemeOptionItem("Modo Oscuro", AppThemeMode.DARK, selectedThemeMode, onThemeSelected)
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = androidx.compose.material3.ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
            ) {
                Text(
                    text = stringResource(android.R.string.cancel),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    )
}

@Composable
fun ThemeOptionItem(
    label: String,
    mode: AppThemeMode,
    selectedMode: AppThemeMode,
    onSelect: (AppThemeMode) -> Unit
) {
    val isSelected = mode == selectedMode
    val containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else Color.Transparent
    val contentColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface

    TextButton(
        onClick = { onSelect(mode) },
        modifier = Modifier.fillMaxWidth(),
        colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
            if (isSelected) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
