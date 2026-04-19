package com.servicerca.app.core.components.utils

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.clickable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.servicerca.app.R
import com.servicerca.app.core.components.card.CategoryCard
import com.servicerca.app.domain.model.Categories

@Composable
@Suppress("UNUSED_PARAMETER")
fun PopularCategoriesSection(
    onCategoryClick: (Categories) -> Unit,
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
                text = stringResource(R.string.view_all),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onViewAll() }
            )

        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            CategoryCard(
                title = stringResource(R.string.category_home),
                icon = R.drawable.ic_push_pin,
                modifier = Modifier.weight(1f),
                onClick = { onCategoryClick(Categories.HOGAR) },
                image = R.drawable.categoria_hogar


            )
            Spacer(modifier = Modifier.width(12.dp))
            CategoryCard(
                title = stringResource(R.string.category_education),
                icon = R.drawable.ic_push_pin,
                modifier = Modifier.weight(1f),
                onClick = { onCategoryClick(Categories.EDUCACIÓN) },
                image = R.drawable.categoria_educacion
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            CategoryCard(
                title = stringResource(R.string.category_pets),
                icon = R.drawable.ic_push_pin,
                modifier = Modifier.weight(1f),
                onClick = { onCategoryClick(Categories.MASCOTAS) },
                image = R.drawable.categoria_mascotas
            )
            Spacer(modifier = Modifier.width(12.dp))
            CategoryCard(
                title = stringResource(R.string.category_technology),
                icon = R.drawable.ic_push_pin,
                modifier = Modifier.weight(1f),
                onClick = { onCategoryClick(Categories.TECNOLOGIA) }
                ,image = R.drawable.categoria_tecnologia
            )

        }
    }
}
