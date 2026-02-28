package com.servicerca.app.ui.services.create

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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.ArrowCircleRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.servicerca.app.R
import com.servicerca.app.core.components.button.ButtonCreateService
import com.servicerca.app.core.components.card.CardServiceImage
import com.servicerca.app.core.components.input.AppTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateServiceScreen(
    onBack: () -> Unit
) {

    val opciones = listOf("Categoria 1", "Categoria 2", "Categoria 3")
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var minValue by remember { mutableStateOf("") }
    var maxValue by remember { mutableStateOf("") }
    var selectedOption by remember { mutableStateOf(opciones[0]) }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(15.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Column{
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
                        text  = stringResource(R.string.title_create_service),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(bottom = 15.dp)
                    )
                }
                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = stringResource(R.string.create_service_add_image_label),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                )

                Spacer(modifier = Modifier.height(5.dp))

                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    elevation = CardDefaults.elevatedCardElevation(
                        defaultElevation = 7.dp
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CardServiceImage()
                    CardServiceImage()
                    CardServiceImage()
                } }


                Spacer(modifier = Modifier.height(10.dp))

                AppTextField(
                    value = title,
                    onValueChange = {title = it},
                    label = stringResource(R.string.title_service_label)
                )

                Spacer(modifier = Modifier.height(10.dp))

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {

                    TextField(
                        value = selectedOption,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Categoría") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF8F8F8),
                            unfocusedContainerColor = Color(0xFFF0F0F0),
                            focusedIndicatorColor = Color(0xFF6C63FF),
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        containerColor = Color.White,
                        shadowElevation = 10.dp,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        opciones.forEach { option ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        option,
                                        fontWeight = FontWeight.Medium
                                    )
                                },
                                onClick = {
                                    selectedOption = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Box{
                    AppTextField(
                        value = description,
                        onValueChange = {description = it},
                        modifier = Modifier
                            .height(150.dp),
                        label = stringResource(R.string.detailed_description_label),
                    )

                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppTextField(
                        value = minValue,
                        onValueChange = {minValue = it},
                        modifier = Modifier
                            .weight(1f),
                        label = "Precio Min",
                    )

                    AppTextField(
                        value = maxValue,
                        onValueChange = {maxValue = it},
                        modifier = Modifier
                            .weight(1f),
                        label = "Precio Max",
                    )

                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = { /* Acción al agregar imagen */ },
                    modifier = Modifier
                        .padding(vertical = 8.dp, horizontal = 4.dp)
                        .size(DpSize(width = Dp.Infinity, height = 70.dp))
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        pressedElevation = 8.dp,
                        defaultElevation = 4.dp,
                        hoveredElevation = 6.dp,
                        focusedElevation = 6.dp
                    )
                ) {
                    Row {
                        Icon(
                            imageVector = Icons.Default.AddLocationAlt,
                            contentDescription = "Agregar Ubicacion",
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                        )
                        Spacer(modifier = Modifier.width(20.dp))

                        Column{
                            Text(
                                text = stringResource(R.string.title_location_service),
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier
                                    .align(Alignment.Start)
                            )

                            Text(
                                text = stringResource(R.string.description_location_service),
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier
                                    .align(Alignment.Start)
                            )
                        }

                        Spacer(modifier = Modifier.width(30.dp))

                        Icon(
                            imageVector = Icons.Default.ArrowCircleRight,
                            contentDescription = "Ir a Ubicacion",
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                        )


                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                ButtonCreateService(
                    text = stringResource(R.string.publish_service_button),
                    onClick = { /* Acción al crear el servicio */ },
                    modifier = Modifier
                        .padding(vertical = 8.dp),
                    icon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Publicar Servicio",
                            modifier = Modifier
                                .align(Alignment.End)
                        )
                    }

                )


            }
        }
    }

}

@Preview (showBackground = true , showSystemUi = true)
@Composable
fun CreateServicePreview() {
    CreateServiceScreen( onBack = {})
}