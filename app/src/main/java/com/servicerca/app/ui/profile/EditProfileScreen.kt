package com.servicerca.app.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.servicerca.app.R
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import com.servicerca.app.core.components.button.PrimaryButton
import com.servicerca.app.core.components.card.CardInfoprofile
import com.servicerca.app.core.components.input.AppTextField

@Composable
fun EditProfileScreen(
    onBack: () -> Unit


) {

    var first_name by remember { mutableStateOf("Primer nombre") }
    var middle_name by remember { mutableStateOf("Segundo nombre") }
    var first_last_name by remember { mutableStateOf("Primer apellido") }
    var second_last_name by remember { mutableStateOf("Segundo apellido") }
    var address by remember { mutableStateOf("Dirección") }
    var num_tel by remember { mutableStateOf("300 123 4567") }

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
                        text = stringResource(R.string.update_profile),
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
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 30.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.BottomEnd,
                        modifier = Modifier.size(150.dp)
                    ) {

                        // Imagen de perfil
                        Card(
                            shape = CircleShape,
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            modifier = Modifier
                                .size(150.dp)
                                .shadow(
                                    elevation = 2.dp,
                                    shape = CircleShape
                                )
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.logo_profile),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        // Botón cámara
                        IconButton(
                            onClick = {
                                // 🔥 Acción aquí
                                println("Cambiar foto")
                            },
                            modifier = Modifier
                                .size(45.dp)
                                .offset(x = 5.dp, y = 5.dp)
                                .shadow(8.dp, CircleShape)
                                .background(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = CircleShape
                                )
                                .border(
                                    width = 3.dp,
                                    color = Color.White,
                                    shape = CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Cambiar foto",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
                Text(
                    text = stringResource(R.string.update_image),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)

                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.
                        padding(bottom = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1F)
                        ) {
                            AppTextField(
                                value = first_name,
                                onValueChange = { first_name = it },
                                label = stringResource(R.string.first_name)

                            )
                        }
                        Column(
                            modifier = Modifier
                                .weight(1F)
                        ) {

                            AppTextField(
                                value = middle_name,
                                onValueChange = { middle_name = it },
                                label = stringResource(R.string.middle_name),
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.
                        padding(bottom = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1F)
                        ) {

                            AppTextField(
                                value = first_last_name,
                                onValueChange = { first_name = it },
                                label = stringResource(R.string.first_last_name)
                            )
                        }
                        Column(
                            modifier = Modifier
                                .weight(1F)
                        ) {

                            AppTextField(
                                value = second_last_name,
                                onValueChange = { second_last_name = it },
                                label = stringResource(R.string.second_last_name)
                            )
                        }
                    }
                    Column (
                        modifier = Modifier.padding(bottom = 20.dp)
                    ) {

                        AppTextField(
                            value = address,
                            onValueChange = { address = it },
                            label = stringResource(R.string.address)
                        )
                    }
                    Column (
                        modifier = Modifier.padding(bottom = 20.dp)
                    ) {

                        AppTextField(
                            value = num_tel,
                            onValueChange = { input ->
                                num_tel = input.filter { it.isDigit() } // 🔥 Solo números
                            },
                            label = stringResource(R.string.number_tel),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = "Teléfono"
                                )
                            }
                        )
                    }
                }
                CardInfoprofile()

                Box(
                    modifier = Modifier.padding(vertical = 20.dp)
                ) {
                    PrimaryButton(
                        text = stringResource(R.string.btn_edit_profile),
                        onClick = { }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EditProfileScreenPreview() {
    EditProfileScreen(
        onBack = {}
    )

}
