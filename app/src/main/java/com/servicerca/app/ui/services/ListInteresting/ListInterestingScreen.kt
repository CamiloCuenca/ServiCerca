package com.servicerca.app.ui.services.ListInteresting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.servicerca.app.core.components.card.InterestingServiceCard
import com.servicerca.app.core.components.tag.CategoryTagSearch
import com.servicerca.app.domain.model.Categories

@Composable
fun ListInteresting(
    onBack: () -> Unit
){
    var selectedCategory by remember { mutableStateOf<Categories?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        IconButton(
            onClick = { onBack() },
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null
            )
        }
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

        LazyColumn(
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                InterestingServiceCard()
            }
            item {
                InterestingServiceCard()
            }
            item {
                InterestingServiceCard()
            }
            item {
                InterestingServiceCard()
            }

            //@TODO @CAMILOCUENCA ver como poner cuando un servicio ya no esta disponible.
        }
    }
}


@Composable
@Preview(showBackground = true, showSystemUi = true)
fun ListInterestingPreview(){
    ListInteresting(
        onBack = {}

    )

}