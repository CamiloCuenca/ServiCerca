package com.servicerca.app.core.components.tag

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.material3.MaterialTheme
import com.servicerca.app.ui.theme.InfoContainerLight
import com.servicerca.app.ui.theme.OnInfoContainerLight

@Composable
fun LevelTag(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color? = null,
    contentColor: Color? = null
) {
    val (defaultBg, defaultContent) = when (text.uppercase()) {
        "PRINCIPIANTE" -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
        "INTERMEDIO" -> InfoContainerLight to OnInfoContainerLight
        "EXPERTO", "MAESTRO" -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.primary
    }

    Surface(
        color = backgroundColor ?: defaultBg,
        shape = RoundedCornerShape(6.dp),
        modifier = modifier
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = contentColor ?: defaultContent,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}