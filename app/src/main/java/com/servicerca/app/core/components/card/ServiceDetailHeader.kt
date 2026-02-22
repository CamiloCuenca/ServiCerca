package com.servicerca.app.core.components.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.servicerca.app.core.components.tag.CategoryTag
import com.servicerca.app.core.components.tag.VerificationTag

@Composable
fun ServiceDetailHeader(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    price: Double,
    isVerified: Boolean,
    category: String
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)

        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically  // esto alinea ambos al centro
            ) {
                if (isVerified) {
                    VerificationTag()
                }
                CategoryTag(text = category)
            }

            Text(
                text = title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1C1E)
            )

            Text(
                text = subtitle,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "$$price",  // Double convertido a String
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00BCD4)
            )
            Text(
                text = "por hora",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ServiceDetailHeaderPreview() {
    ServiceDetailHeader(
        title = "Reparación de Fugas",
        subtitle = "Plomería Residencial",
        price = 350.0,
        isVerified = true,
        category = "HOGAR"
    )
}