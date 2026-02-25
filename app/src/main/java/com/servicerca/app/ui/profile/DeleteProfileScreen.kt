package com.servicerca.app.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.servicerca.app.R
import com.servicerca.app.core.components.button.ButtonIcon
import com.servicerca.app.core.components.button.DeleteButton
import com.servicerca.app.core.components.button.PrimaryButton
import com.servicerca.app.core.components.card.CardInfoDeleteProfile

@Composable
fun DeleteProfileScreen (
    onBack: () -> Unit
){
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {

                // Header: ícono a la izquierda, título centrado
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    IconButton(
                        onClick = { onBack() },
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }

                    Text(
                        text = stringResource(R.string.delete_profile),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(bottom = 15.dp)
                    )
                }
                HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.outline)

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 30.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.BottomEnd,
                        modifier = Modifier.size(150.dp)
                    ) {

                        Image(
                                painter = painterResource(id = R.drawable.eliminar_perfil),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                    }
                }
            Row(
                Modifier.fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Surface(
                color = Color(0xFFFDDFDF),
                shape = RoundedCornerShape(12.dp), //  cuadrado con esquinas redondeadas
                tonalElevation = 2.dp,
            ){
                Text(
                    text = stringResource(R.string.important_notice),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .padding(horizontal = 12.dp, vertical = 6.dp)

                    )
                }
            }
            Row(
            ) {
                Text(
                    text = stringResource(R.string.question_confirm_delete_profile),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .padding(horizontal = 12.dp)

                )
            }
            Row() {
                Text(
                    text = stringResource(R.string.information_delete_prefil),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .padding(horizontal = 12.dp)

                )
            }

            CardInfoDeleteProfile()

            ButtonIcon(
                text = stringResource(R.string.no_delete_account),
                onClick = { onBack()},
                icon = {
                    Icon(
                        imageVector = Icons.Default.Favorite, // Icono de Material
                        contentDescription = null
                    )
                }
            )
            DeleteButton(
                text = stringResource(R.string.delete_account_permanently),
                onClick = { },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Delete, // Icono de Material
                        contentDescription = null
                    )
                }
            )
        }
    }
}


@Preview
@Composable
fun DeleteProfileScreenPreview(){
    DeleteProfileScreen(
        onBack = {}
    )
}