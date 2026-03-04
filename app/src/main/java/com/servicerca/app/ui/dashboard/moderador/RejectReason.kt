package com.servicerca.app.ui.dashboard.moderador

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import com.mapbox.maps.extension.style.expressions.dsl.generated.color
import com.servicerca.app.R
import com.servicerca.app.core.components.input.AppTextField
import com.servicerca.app.core.components.tag.CategoryTag

@Composable
fun RejectReasonScreen(
    onBack: () -> Unit
) {
    var description by remember { mutableStateOf("") }
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
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
                        text = stringResource(R.string.reject_reason),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.Center)
                    )
                }
                HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.outline)
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .padding(vertical = 20.dp)
                ) {
                    Text(
                        text = stringResource(R.string.question_reject_reason),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Text(
                        text = stringResource(R.string.message_reject_reason),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Text(
                        text = stringResource(R.string.description_reject_reason),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = 5.dp)
                    )
                    Box{
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            modifier = Modifier
                                .height(300.dp)
                                .fillMaxWidth(),
                            label = {
                                Text(text = stringResource(R.string.example_reject_reason))
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedLabelColor = MaterialTheme.colorScheme.outline,
                                unfocusedLabelColor = MaterialTheme.colorScheme.outline,
                            )
                        )
                    }
                    Row() {
                        Surface(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(10.dp),
                            tonalElevation = 2.dp,
                            modifier = Modifier.padding(vertical = 10.dp)
                                .fillMaxWidth()
                                .size(width = 0.dp, height = 30.dp)
                                .weight(1F)
                        ) {
                            Text(
                                text = stringResource(R.string.option1_reject_reason),
                                textAlign = TextAlign.Start,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Surface(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(10.dp),
                            tonalElevation = 2.dp,
                            modifier = Modifier.padding(vertical = 10.dp)
                                .fillMaxWidth()
                                .size(width = 0.dp, height = 30.dp)
                                .weight(1F)
                        ) {
                            Text(
                                text = stringResource(R.string.option1_reject_reason),
                                textAlign = TextAlign.Start,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                }
            }
        }
    }
}

@Preview
@Composable
fun RejectReasonScreenPreview(){
    RejectReasonScreen(
        onBack = {}
    )
}