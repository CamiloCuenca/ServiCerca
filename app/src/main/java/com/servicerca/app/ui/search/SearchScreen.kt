package com.servicerca.app.ui.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.servicerca.app.R
import com.servicerca.app.core.components.card.CategoryCard
import com.servicerca.app.core.components.card.ExploreMapCard
import com.servicerca.app.core.components.input.SearchTextField
import com.servicerca.app.core.components.utils.RecentSearchesSection
import com.servicerca.app.ui.theme.ServiCercaTheme
import com.servicerca.app.core.components.utils.PopularCategoriesSection

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
        Text(
            text = stringResource(R.string.search_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

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