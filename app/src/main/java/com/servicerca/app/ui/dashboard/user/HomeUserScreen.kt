package com.servicerca.app.ui.dashboard.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.servicerca.app.R
import com.servicerca.app.core.components.card.CardService
import com.servicerca.app.core.components.tag.CategoryTagSearch
import com.servicerca.app.domain.model.Categories

@Composable
fun HomeUserScreen(
    onDetailClick: (String) -> Unit = {},
    viewModel: HomeUserViewModel = hiltViewModel()
) {

    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<Categories?>(null) }
    val services by viewModel.services.collectAsState() // Observamos la lista
    val filteredServices = remember(services, selectedCategory) {
        selectedCategory?.let { category ->
            services.filter {
                it.service.type.equals(category.displayName, ignoreCase = true)
            }
        } ?: services
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        //
        /*SearchTextField(
            query = query,
            onQueryChange = { query = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        )*/

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp , vertical = 10.dp)
        ) {
            item {
                Surface(
                    onClick = { selectedCategory = null },
                    shape = RoundedCornerShape(50),
                    color = if (selectedCategory == null) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    }
                ) {
                    Text(
                        text = stringResource(R.string.filter_all_label),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp),
                        color = if (selectedCategory == null) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }
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
            items(
                items = filteredServices,
                key = { it.service.id }
            ) { serviceWithRating ->
                CardService(
                    title = serviceWithRating.service.title,
                    category = serviceWithRating.service.type,
                    priceRange = "$${serviceWithRating.service.priceMin.toInt()} - $${serviceWithRating.service.priceMax.toInt()}",
                    rating = String.format("%.1f", serviceWithRating.averageRating),
                    level = serviceWithRating.ownerLevel,
                    photoUrl = serviceWithRating.service.photoUrl,
                    isBookmarked = serviceWithRating.isBookmarked,
                    likeCount = serviceWithRating.likeCount,
                    isLiked = serviceWithRating.isLiked,
                    onBookmarkClick = {
                        viewModel.onBookmarkClick(serviceWithRating.service.id)
                    },
                    onLikeClick = {
                        viewModel.onLikeClick(serviceWithRating.service.id)
                    },
                    onRequestClick = { onDetailClick(serviceWithRating.service.id) }
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
