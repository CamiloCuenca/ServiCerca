package com.servicerca.app.core.components.card

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ServiceDescriptionSection(
    modifier: Modifier = Modifier,
    description: String,
    maxLines: Int = 4
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Sobre el servicio",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1C1E)
        )

        Spacer(modifier = Modifier.height(8.dp))

        val displayText = if (expanded) description else {
            val words = description.split(" ")
            if (words.size > 35) words.take(35).joinToString(" ") + "..." else description
        }

        val annotated = buildAnnotatedString {
            append(displayText)
            if (!expanded && description.split(" ").size > 35) {
                append(" ")
                withStyle(SpanStyle(color = Color(0xFF00BCD4), fontWeight = FontWeight.Medium)) {
                    append("Leer más")
                }
            }
        }

        Text(
            text = annotated,
            fontSize = 14.sp,
            color = Color(0xFF555555),
            lineHeight = 22.sp,
            modifier = Modifier.clickable { expanded = !expanded }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ServiceDescriptionSectionPreview() {
    ServiceDescriptionSection(
        description = "Servicio profesional de plomería especializado en detección y reparación de fugas de agua y gas. Utilizamos tecnología de ultrasonido para localizar problemas sin romper paredes innecesariamente. Incluye materiales básicos y garantía de 30 días en mano de obra."
    )
}
