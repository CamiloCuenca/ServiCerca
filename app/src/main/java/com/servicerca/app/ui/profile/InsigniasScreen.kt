package com.servicerca.app.ui.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.servicerca.app.R
import com.servicerca.app.core.components.card.CadInsignias
import com.servicerca.app.core.components.card.CardMainInsignias
import com.servicerca.app.core.components.card.CardMessageInsignias

@Composable
fun InsigniasScreen(onBack: () -> Unit) {

    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->

        AnimatedVisibility(
            visible = visible,
            enter = slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth }, // Empieza fuera de la pantalla (derecha)
                animationSpec = tween(durationMillis = 400)
            )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            modifier = Modifier.clickable { onBack() }
                        )

                        Spacer(modifier = Modifier.width(95.dp))

                        Text(
                            text = stringResource(R.string.tittle_insignias),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .padding(bottom = 15.dp)
                        )
                    }

                    HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.onSurfaceVariant,)

                    Column(
                        modifier = Modifier
                            .padding(top = 15.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {

                        Text(
                            text = stringResource(R.string.title_achievements),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    CardMainInsignias()

                    Column(
                        modifier = Modifier
                            .padding(top = 20.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {

                        Text(
                            text = stringResource(R.string.title_insginias_obtained),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        CadInsignias()
                    }
                }

                CardMessageInsignias()
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun InsigniasScreenPreview() {
    InsigniasScreen(onBack = {})
}