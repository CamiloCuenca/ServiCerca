package com.servicerca.app.ui.services.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import com.servicerca.app.R
import com.servicerca.app.core.components.button.DeleteButton
import com.servicerca.app.core.components.button.PrimaryButton
import com.servicerca.app.core.components.card.CardServiceImage
import com.servicerca.app.core.components.input.AppTextField
import com.servicerca.app.ui.theme.ServiCercaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsigniasScreen(onBack: () -> Unit) {
    val opciones = listOf("Categoria 1", "Categoria 2", "Categoria 3")
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var minValue by remember { mutableStateOf("") }
    var maxValue by remember { mutableStateOf("") }
    var selectedOption by remember { mutableStateOf(opciones[0]) }

    Column(
        modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(15.dp),
        verticalArrangement = Arrangement.SpaceBetween) {
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
                text  = "Editar Servicio",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(bottom = 15.dp)
            )
        }


        Text(
            text = "Fotos del servicio",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .padding(bottom = 8.dp)
        )

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

        PrimaryButton(
            text = "Guardar Cambios",
            onClick = { /* Acción al crear el servicio */ },
        )

        DeleteButton(
            text = "Eliminar Servicio",
            onClick = { /* acción */ },
            icon = {
                Icon(
                    imageVector = Icons.Default.Delete, // Icono de Material
                    contentDescription = null
                )
            }
        )
    }



}


@Composable
@Preview( showBackground = true, showSystemUi = true)
fun InsigniasScreenPreview(){
    ServiCercaTheme{
        InsigniasScreen( onBack = {})
    }

}

