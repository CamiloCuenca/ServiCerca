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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.servicerca.app.R
import com.servicerca.app.core.components.input.AppTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateServiceScreen(
) {

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
            .padding(15.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Column{
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = stringResource(R.string.create_service_add_image_label),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.align(Alignment.Start))

            Spacer(modifier = Modifier.height(5.dp))

            Box{
               /* Card(
                    shape = CardDefaults.shape,
                    colors = CardDefaults.cardColors(),
                   // modifier = Modifier.size(10.dp)
                )*/

                Icon(
                    imageVector = Icons.Default.AddPhotoAlternate,
                    contentDescription = "Añadir imagen",
                    modifier = Modifier.size(50.dp)
                )

            }


            Spacer(modifier = Modifier.height(32.dp))

            AppTextField(
                value = title,
                onValueChange = {title = it},
                label = stringResource(R.string.title_service_label),
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
                    label = { Text(stringResource(R.string.category_service_label)) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    }
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.align(Alignment.Start)
                ) {
                    opciones.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
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
                    label = stringResource(R.string.detailed_description_label),
                    modifier = Modifier
                        .height(150.dp)
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
                    label = "Precio Min",
                    modifier = Modifier
                        .weight(1f)
                )

                AppTextField(
                    value = maxValue,
                    onValueChange = {maxValue = it},
                    label = "Precio Max",
                    modifier = Modifier
                        .weight(1f)
                )

            }

            Spacer(modifier = Modifier.height(10.dp))

            Box{

            }


        }
    }

}

@Preview (showBackground = true , showSystemUi = true)
@Composable
fun CreateServicePreview() {
    CreateServiceScreen()
}