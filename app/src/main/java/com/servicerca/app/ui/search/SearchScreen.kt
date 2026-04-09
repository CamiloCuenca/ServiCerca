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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.servicerca.app.R
import com.servicerca.app.core.components.card.ExploreMapCard
import com.servicerca.app.core.components.input.SearchTextField
import com.servicerca.app.core.components.utils.PopularCategoriesSection
import com.servicerca.app.core.components.utils.RecentSearchesSection
import com.servicerca.app.ui.theme.ServiCercaTheme

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    onSearch: (String) -> Unit = {},
    onClearRecentSearches: () -> Unit = {},
    onCategoryClick: (String) -> Unit = {},
    onOpenMap: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))


        Spacer(modifier = Modifier.height(16.dp))

        SearchTextField(
            query = "",
            onQueryChange = onSearch,
            placeholder = stringResource(R.string.search_placeholder),
        )

        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                RecentSearchesSection(
                    recentSearches = listOf("Arreglo de aire", "Pintor zona norte", "Limpieza de alfombras"),
                    onClearAll = onClearRecentSearches
                )
            }



            item {
                ExploreMapCard(onOpenMap = onOpenMap)
            }

            item {
                PopularCategoriesSection(
                    onViewAll = {}
                )
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