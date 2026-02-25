package com.servicerca.app.ui.dashboard.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.servicerca.app.core.components.card.CardService
import com.servicerca.app.core.components.input.SearchTextField
import com.servicerca.app.core.components.tag.CategoryTagSearch
import com.servicerca.app.domain.model.Categories

/**
 *TODO  Cuando hagamos los ViewModel seria algo asi :
 *  items(serviceList) { service ->
 *     CardService(...)
 * }
 */


@Composable
fun HomeUserScreen() {

    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<Categories?>(null) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        //
        SearchTextField(
            query = query,
            onQueryChange = { query = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp , vertical = 10.dp)
        ) {
            items(Categories.entries) { category ->
                CategoryTagSearch(
                    category = category,
                    isSelected = selectedCategory == category,
                    onClick = {
                        selectedCategory =
                            if (selectedCategory == category) null else category
                    }
                )
            }
        }


        // Lista scrolleable
        LazyColumn(
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            item {
                CardService(
                    title = "Reparación de Fugas Urgente",
                    category = "Plomería",
                    level = "EXPERTO"
                )
            }

            item {
                CardService(
                    title = "Instalación Eléctrica Residencial",
                    category = "Electricidad",
                    level = "PRO"
                )
            }

            item {
                CardService(
                    title = "Remodelación de Cocina",
                    category = "Construcción",
                    level = "PREMIUM"
                )
            }

            item {
                CardService(
                    title = "Limpieza Profunda del Hogar",
                    category = "House Keeping",
                    level = "BÁSICO"
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun HomeUserScreenPreview() {
    HomeUserScreen()
}