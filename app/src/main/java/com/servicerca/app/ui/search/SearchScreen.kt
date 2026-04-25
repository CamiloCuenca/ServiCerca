package com.servicerca.app.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.servicerca.app.R
import com.servicerca.app.core.components.card.ExploreMapCard
import com.servicerca.app.core.components.card.GridServiceCard
import com.servicerca.app.core.components.input.SearchTextField
import com.servicerca.app.core.components.utils.PopularCategoriesSection
import com.servicerca.app.core.components.utils.RecentSearchesSection
import com.servicerca.app.domain.model.Categories
import com.servicerca.app.ui.theme.ServiCercaTheme

@Composable
@Suppress("UNUSED_PARAMETER")
fun SearchScreen(
    modifier: Modifier = Modifier,
    onSearch: (String) -> Unit = {},
    onClearRecentSearches: () -> Unit = {},
    onOpenMap: () -> Unit = {},
    onServiceClick: (String) -> Unit = {},
    onBackClick: () -> Unit = {},
    viewModel: SearchViewModel? = null
) {
    val isInPreview = LocalInspectionMode.current
    val actualViewModel = if (isInPreview) {
        null
    } else {
        viewModel ?: hiltViewModel<SearchViewModel>()
    }

    val query = actualViewModel?.query?.collectAsState()?.value.orEmpty()
    val searchResults = actualViewModel?.searchResults?.collectAsState()?.value.orEmpty()
    val recentSearches = actualViewModel?.recentSearches?.collectAsState()?.value.orEmpty()
    val selectedCategory = actualViewModel?.selectedCategory?.collectAsState()?.value
    val categoryResults = actualViewModel?.categoryResults?.collectAsState()?.value.orEmpty()
    val selectedFilter = actualViewModel?.selectedFilter?.collectAsState()?.value ?: SearchFilter.ALL

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                if (query.isNotBlank() || selectedCategory != null) {
                    actualViewModel?.onQueryChange("")
                    actualViewModel?.clearSelectedCategory()
                } else {
                    onBackClick()
                }
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.action_back)
                )
            }
            Text(
                text = "Buscar Servicios",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
            SearchTextField(
                query = query,
                onQueryChange = { newQuery ->
                    actualViewModel?.onQueryChange(newQuery)
                    onSearch(newQuery)
                },
                onSearch = {
                    actualViewModel?.submitCurrentSearch()
                },
                placeholder = stringResource(R.string.search_placeholder),
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Filters Row
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(SearchFilter.values()) { filter ->
                FilterChip(
                    selected = filter == selectedFilter,
                    onClick = { actualViewModel?.onFilterSelect(filter) },
                    label = { Text(filter.displayName) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = CircleShape
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (query.isBlank() && selectedCategory == null) {
            // Default View
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                item {
                    RecentSearchesSection(
                        recentSearches = recentSearches,
                        onClearAll = {
                            actualViewModel?.clearRecentSearches()
                            onClearRecentSearches()
                        },
                        onSearchClick = { recent ->
                            actualViewModel?.selectRecentSearch(recent)
                        }
                    )
                }

                item {
                    ExploreMapCard(onOpenMap = onOpenMap)
                }

                item {
                    PopularCategoriesSection(
                        onCategoryClick = { category ->
                            actualViewModel?.selectCategory(category)
                        },
                        onViewAll = {}
                    )
                }
            }
        } else {
            // Search or Category Results View (Staggered Grid)
            val resultsToDisplay = if (query.isNotBlank()) searchResults else categoryResults
            
            if (selectedCategory != null) {
                val categoryName = stringResource(categoryLabelRes(selectedCategory))
                Text(
                    text = stringResource(R.string.search_category_results_title, categoryName),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 12.dp)
                )
            } else if (query.isNotBlank()) {
                Text(
                    text = stringResource(R.string.search_results_for_query, query),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 12.dp)
                )
            }

            if (resultsToDisplay.isEmpty()) {
                EmptyStateView()
            } else {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp), // Less padding as cards have internal padding
                    contentPadding = PaddingValues(bottom = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalItemSpacing = 8.dp
                ) {
                    items(
                        items = resultsToDisplay,
                        key = { it.service.id }
                    ) { result ->
                        GridServiceCard(
                            onClick = { onServiceClick(result.service.id) },
                            imageUrl = result.service.photoUrl,
                            title = result.service.title,
                            category = result.service.type,
                            priceMin = result.service.priceMin.toInt().toString(),
                            priceMax = result.service.priceMax.toInt().toString(),
                            rating = 0f,
                            isFavorite = result.isBookmarked,
                            onFavoriteClick = {
                                actualViewModel?.onBookmarkClick(result.service.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyStateView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.size(100.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.SearchOff,
                    contentDescription = null,
                    modifier = Modifier.size(50.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "No encontramos resultados",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Prueba con palabras diferentes o revisa la ortografía.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    ServiCercaTheme {
        SearchScreen()
    }
}

private fun categoryLabelRes(category: Categories): Int = when (category) {
    Categories.HOGAR -> R.string.category_home
    Categories.EDUCACIÓN -> R.string.category_education
    Categories.MASCOTAS -> R.string.category_pets
    Categories.TECNOLOGIA -> R.string.category_technology
    Categories.TRANSPORTE -> R.string.category_transport
}
