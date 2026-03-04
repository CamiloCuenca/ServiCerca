package com.servicerca.app.core.components.tag

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.servicerca.app.domain.model.Categories


@Composable
fun CategoryTag(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color(0xFFF1F3F5),
    colorText: Color = Color.Gray
) {
    Surface(
        color = color,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            color = colorText,
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
            Color(0xFFF1F3F5),
    ) {
        Text(
            text = category.displayName,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp),
            color = if (isSelected)
                MaterialTheme.colorScheme.onPrimary
            else
                Color.Gray
        )
    }
}