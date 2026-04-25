package com.servicerca.app.core.components.tag

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.servicerca.app.domain.model.Categories
import com.servicerca.app.ui.theme.InfoContainerLight
import com.servicerca.app.ui.theme.OnInfoContainerLight
import com.servicerca.app.ui.theme.OnWarningContainerLight
import com.servicerca.app.ui.theme.WarningContainerLight


@Composable
fun CategoryTag(
    text: String,
    modifier: Modifier = Modifier,
    color: Color? = null,
    colorText: Color? = null
) {
    val (defaultBg, defaultContent) = when (text.uppercase()) {
        "HOGAR" -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.primary
        "TECNOLOGÍA", "TECNOLOGIA" -> InfoContainerLight to OnInfoContainerLight
        "EDUCACIÓN", "EDUCACION" -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        "MASCOTAS" -> WarningContainerLight to OnWarningContainerLight
        "TRANSPORTE" -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        color = color ?: defaultBg,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            color = colorText ?: defaultContent,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun CategoryTagSearch(
    category: Categories,
    isSelected: Boolean,
    onClick: (Categories) -> Unit
) {
    Surface(
        onClick = { onClick(category) },
        shape = RoundedCornerShape(50),
        color = if (isSelected)
            MaterialTheme.colorScheme.primary
        else
            MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Text(
            text = stringResource(category.nameRes),
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp),
            color = if (isSelected)
                MaterialTheme.colorScheme.onPrimary
            else
                MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}