package com.servicerca.app.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.servicerca.app.R
import com.servicerca.app.domain.model.Categories
import com.servicerca.app.core.components.card.ExploreMapCard
import com.servicerca.app.core.components.card.InterestingServiceCard
import com.servicerca.app.core.components.input.SearchTextField
import com.servicerca.app.core.components.utils.PopularCategoriesSection
import com.servicerca.app.core.components.utils.RecentSearchesSection
import com.servicerca.app.ui.theme.ServiCercaTheme

@Composable
@Suppress("UNUSED_PARAMETER")
fun SearchScreen(
    modifier: Modifier = Modifier,
    onSearch: (String) -> Unit = {},
    onClearRecentSearches: () -> Unit = {},
    onOpenMap: () -> Unit = {},
    onServiceClick: (String) -> Unit = {},
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Spacer(modifier = Modifier.height(16.dp))

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

        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            if (query.isBlank()) {
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

                if (selectedCategory != null) {
                    item {
                        val categoryName = stringResource(categoryLabelRes(selectedCategory))
                        Text(
                            text = stringResource(R.string.search_category_results_title, categoryName),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    items(
                        items = categoryResults,
                        key = { it.service.id }
                    ) { result ->
                        InterestingServiceCard(
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
                            },
                            modifier = Modifier.padding(horizontal = 0.dp)
                        )
                    }

                    if (categoryResults.isEmpty()) {
                        item {
                            Text(
                                text = stringResource(R.string.search_no_services_in_category),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                item {
                    Text(
                        text = stringResource(R.string.search_results_for_query, query),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                items(
                    items = searchResults,
                    key = { it.service.id }
                ) { result ->
                    InterestingServiceCard(
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
                        },
                        modifier = Modifier.padding(horizontal = 0.dp)
                    )
                }

                if (searchResults.isEmpty()) {
                    item {
                        Text(
                            text = stringResource(R.string.search_no_results_by_name),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
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
