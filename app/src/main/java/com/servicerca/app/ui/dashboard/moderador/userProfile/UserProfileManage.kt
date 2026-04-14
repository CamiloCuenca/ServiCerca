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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.servicerca.app.ui.profile.StatisticsSection

@Composable
fun UserProfileManage(
    viewModel: UserProfileManageViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsState()

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
                ProfileImage(url = user?.profilePictureUrl ?: "")
            }
        }

        // Nombre del Usuario
        Text(
            text = user?.let { "${it.name1} ${it.lastname1}" } ?: stringResource(R.string.profile_fallback_name),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )


        CardLevel()


        StatisticsSection(
            user = user
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UserProfileManagePreview() {
    UserProfileManage()
}
