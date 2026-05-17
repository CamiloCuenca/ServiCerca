package com.servicerca.app.ui.dashboard.user

import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.servicerca.app.R
import com.servicerca.app.core.components.card.CardService
import com.servicerca.app.core.components.card.SkeletonServiceCard
import com.servicerca.app.core.components.tag.CategoryTagSearch
import com.servicerca.app.domain.model.Categories
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeUserScreen(
    onDetailClick: (String) -> Unit = {},
    viewModel: HomeUserViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val services by viewModel.services.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val homeFilters by viewModel.homeFilters.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    var showFilterSheet by remember { mutableStateOf(false) }

    val hasActiveFilters = homeFilters.maxPrice > 0f || homeFilters.sort != HomeSort.RECENT

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Fila de categorías + botón filtros
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(start = 16.dp, end = 4.dp, top = 10.dp, bottom = 10.dp),
                modifier = Modifier.weight(1f)
            ) {
                item {
                    Surface(
                        onClick = { if (selectedCategory != null) viewModel.selectCategory(selectedCategory!!) },
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
                        onClick = { viewModel.selectCategory(it) }
                    )
                }
            }

            // Botón filtros con badge si hay filtros activos
            BadgedBox(
                badge = { if (hasActiveFilters) Badge() },
                modifier = Modifier.padding(end = 8.dp)
            ) {
                IconButton(onClick = { showFilterSheet = true }) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Filtros",
                        tint = if (hasActiveFilters) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier.fillMaxSize()
        ) {
            if (services.isEmpty()) {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(5) { SkeletonServiceCard() }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(
                        items = services,
                        key = { it.service.id }
                    ) { serviceWithRating ->
                        CardService(
                            serviceId = serviceWithRating.service.id,
                            title = serviceWithRating.service.title,
                            category = serviceWithRating.service.type,
                            priceRange = "$${serviceWithRating.service.priceMin.toInt()} - $${serviceWithRating.service.priceMax.toInt()}",
                            rating = String.format("%.1f", serviceWithRating.averageRating),
                            level = serviceWithRating.ownerLevel,
                            photoUrl = serviceWithRating.service.photoUrl,
                            isBookmarked = serviceWithRating.isBookmarked,
                            likeCount = serviceWithRating.likeCount,
                            isLiked = serviceWithRating.isLiked,
                            isOwner = serviceWithRating.isOwner,
                            onBookmarkClick = {
                                viewModel.onBookmarkClick(serviceWithRating.service.id)
                            },
                            onLikeClick = {
                                viewModel.onLikeClick(serviceWithRating.service.id)
                            },
                            onRequestClick = { onDetailClick(serviceWithRating.service.id) },
                            onShareClick = {
                                val s = serviceWithRating.service
                                val deepLink = "https://servicerca-6ee07.web.app/service?serviceId=${s.id}"
                                val texto = buildString {
                                    appendLine("🔧 *${s.title}*")
                                    appendLine()
                                    appendLine("📝 ${s.description}")
                                    appendLine("💰 Precio: \$${s.priceMin.toInt()} - \$${s.priceMax.toInt()}")
                                    appendLine()
                                    appendLine("👆 Ver en ServiCerca:")
                                    appendLine(deepLink)
                                    append("_Abre directo en la app si la tienes instalada_ 📱")
                                }
                                val pm = context.packageManager
                                val whatsappPackage = listOf("com.whatsapp", "com.whatsapp.w4b")
                                    .firstOrNull { pkg ->
                                        try {
                                            pm.getPackageInfo(pkg, PackageManager.GET_ACTIVITIES)
                                            true
                                        } catch (e: PackageManager.NameNotFoundException) { false }
                                    }
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, texto)
                                    whatsappPackage?.let { setPackage(it) }
                                }
                                if (whatsappPackage != null) {
                                    context.startActivity(intent)
                                } else {
                                    context.startActivity(
                                        Intent.createChooser(intent, "Compartir servicio")
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    if (showFilterSheet) {
        HomeFilterBottomSheet(
            filters = homeFilters,
            onFiltersChange = { viewModel.updateFilters(it) },
            onDismiss = { showFilterSheet = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeFilterBottomSheet(
    filters: HomeFilters,
    onFiltersChange: (HomeFilters) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Filtros",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            // Ordenar por
            Text(
                text = "Ordenar por",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                HomeSort.entries.forEach { sort ->
                    FilterChip(
                        selected = sort == filters.sort,
                        onClick = { onFiltersChange(filters.copy(sort = sort)) },
                        label = { Text(sort.label, style = MaterialTheme.typography.labelMedium) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = CircleShape
                    )
                }
            }

            HorizontalDivider()

            // Precio máximo
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Precio máximo",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = if (filters.maxPrice > 0f) "$${filters.maxPrice.roundToInt()}" else "Sin límite",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Slider(
                    value = filters.maxPrice,
                    onValueChange = { onFiltersChange(filters.copy(maxPrice = it)) },
                    valueRange = 0f..500000f,
                    steps = 9,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Sin límite",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$500.000",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Botón limpiar + aplicar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        onFiltersChange(HomeFilters())
                        onDismiss()
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors()
                ) {
                    Text("Limpiar")
                }
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Aplicar")
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun HomeUserScreenPreview() {
    HomeUserScreen()
}
