package com.servicerca.app.ui.dashboard.moderador.userProfile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.servicerca.app.R
import com.servicerca.app.core.components.card.CardLevel
import com.servicerca.app.core.components.images.ProfileImage
import com.servicerca.app.ui.profile.ProfileUiState
import com.servicerca.app.ui.profile.StatisticsSection

@Composable
fun UserProfileManage(
    viewModel: UserProfileManageViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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
                // Imagen de Perfil
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.size(150.dp)) {
                        ProfileImage(url = user.profilePictureUrl)
                    }
                }

                // Nombre completo
                Text(
                    text = "${user.name1} ${user.name2 ?: ""} ${user.lastname1}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                // Tarjeta de Nivel (con datos dinámicos)
                CardLevel(
                    level = state.level,
                    levelName = state.levelName,
                    currentXp = state.totalXp,
                    nextLevelXp = state.xpRequiredForNextLevel,
                    progress = state.progress,
                    remainingXp = (state.xpRequiredForNextLevel - state.totalXp).coerceAtLeast(0)
                )

                // Sección de Estadísticas
                StatisticsSection(state = state)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserProfileManagePreview() {
    // UserProfileManage()
}
