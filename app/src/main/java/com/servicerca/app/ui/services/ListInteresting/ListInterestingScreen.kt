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
        import androidx.compose.material3.Text
        import androidx.compose.runtime.Composable
        import androidx.lifecycle.compose.collectAsStateWithLifecycle
        import androidx.compose.runtime.getValue
        import androidx.compose.runtime.mutableStateOf
        import androidx.compose.runtime.remember
        import androidx.compose.runtime.setValue
        import androidx.compose.ui.Alignment
        import androidx.compose.ui.Modifier
        import androidx.compose.ui.platform.LocalInspectionMode
        import androidx.compose.ui.tooling.preview.Preview
        import androidx.compose.ui.unit.dp
        import androidx.hilt.navigation.compose.hiltViewModel
        import com.servicerca.app.core.components.card.InterestingServiceCard
        import com.servicerca.app.core.components.tag.CategoryTagSearch
        import com.servicerca.app.domain.model.Categories
        import com.servicerca.app.domain.model.Service

        @Composable
        fun ListInteresting(
            onBack: () -> Unit,
            onServiceClick: (String) -> Unit = {},
            viewModel: ListInterestingViewModel? = null
        ) {
            val isInPreview = LocalInspectionMode.current
            var selectedCategory by remember { mutableStateOf<Categories?>(null) }

            val services: List<Service> = if (isInPreview) {
                emptyList()
            } else {
                val actualViewModel = viewModel ?: hiltViewModel<ListInterestingViewModel>()
                val state by actualViewModel.interestingServices.collectAsStateWithLifecycle()
                state
            }

            val filteredServices: List<Service> = remember(services, selectedCategory) {
                if (selectedCategory == null) {
                    services
                } else {
                    services.filter { service ->
                        service.type.contains(selectedCategory!!.displayName, ignoreCase = true)
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver"
                    )
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    items(Categories.entries) { category ->
                        CategoryTagSearch(
                            category = category,
                            isSelected = selectedCategory == category,
                            onClick = {
                                selectedCategory = if (selectedCategory == category) null else category
                            }
                        )
                    }
                }

                LazyColumn(
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(
                        items = filteredServices,
                        key = { it.id }
                    ) { service ->
                        InterestingServiceCard(
                            onClick = { onServiceClick(service.id) },
                            imageUrl = service.photoUrl,
                            title = service.title,
                            category = service.type,
                            priceMin = service.priceMin.toInt().toString(),
                            priceMax = service.priceMax.toInt().toString(),
                            rating = 0f,
                            isFavorite = true
                        )
                    }

                    if (filteredServices.isEmpty()) {
                        item {
                            Text(
                                text = "No tienes servicios interesantes guardados.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        @Preview(showBackground = true, showSystemUi = true)
        @Composable
        fun ListInterestingPreview() {
            ListInteresting(onBack = {})
        }