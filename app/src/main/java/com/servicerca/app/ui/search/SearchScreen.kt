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
import com.servicerca.app.core.components.input.SearchTextField
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



@Composable
fun RecentSearchesSection(
    recentSearches: List<String>,
    onClearAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.recent_searches_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.clear_all),
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF13ECEC),
                modifier = Modifier.clickable { onClearAll() }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        recentSearches.forEach { search ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_history),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = search,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun PopularCategoriesSection(
    onViewAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.popular_categories_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.ver_todas),
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF13ECEC),
                modifier = Modifier.clickable { onViewAll() }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            //TODO @CamiloCuenca luego poner las imagenes
            CategoryCard(
                title = "Hogar",
                icon = R.drawable.ic_push_pin,
                modifier = Modifier.weight(1f),
                onClick = {  }
            )
            Spacer(modifier = Modifier.width(12.dp))
            CategoryCard(
                title = "Educación",
                icon = R.drawable.ic_push_pin,
                modifier = Modifier.weight(1f),
                onClick = {  }
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            CategoryCard(
                title = "Mascotas",
                icon = R.drawable.ic_push_pin,
                modifier = Modifier.weight(1f),
                onClick = {  }
            )
            Spacer(modifier = Modifier.width(12.dp))
            CategoryCard(
                title = "Tecnología",
                icon = R.drawable.ic_push_pin,
                modifier = Modifier.weight(1f),
                onClick = {  }
            )

        }
    }
}



@Composable
fun ExploreMapCard(
    onOpenMap: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE0F7FA)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF13ECEC)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_location),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = stringResource(R.string.explore_map_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF006064)
                    )
                    Text(
                        text = stringResource(R.string.explore_map_description),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF006064).copy(alpha = 0.7f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onOpenMap,
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF13ECEC)
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_location),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.open_map_button),
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
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