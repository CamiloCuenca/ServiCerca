package com.servicerca.app.core.components.card

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.servicerca.app.R

@Composable
fun ConfirmServiceSummaryCard(
    modifier: Modifier = Modifier,
    providerName: String = "Carlos Ruiz",
    serviceCategory: String = "Plomería Residencial",
    dateText: String = "Hoy, 10:00",
    statusText: String = "En progreso",
    avatarRes: Int = R.drawable.foto_usuario // Assuming this exists or falls back
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar with online badge
                Box {
                    Image(
                        painter = painterResource(id = avatarRes),
                        contentDescription = "Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .border(2.dp, colorScheme.primary, CircleShape)
                    )
                    // Online Badge (Green dot)
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .background(Color(0xFF00E676), CircleShape) // Bright green
                            .border(2.dp, colorScheme.surface, CircleShape)
                            .align(Alignment.BottomEnd)
                            .offset(x = (-2).dp, y = (-2).dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Name and Category
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = providerName,
                        style = typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface
                    )
                    Text(
                        text = serviceCategory,
                        style = typography.bodyMedium,
                        color = colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                // Status Tag (Cyan pill)
                Box(
                    modifier = Modifier
                        .background(
                            color = colorScheme.primary.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = statusText,
                        style = typography.labelSmall,
                        color = colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = colorScheme.onSurface.copy(alpha = 0.05f)
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            // Date and Time
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.CalendarToday,
                    contentDescription = "Calendar",
                    modifier = Modifier.size(18.dp),
                    tint = colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = dateText,
                    style = typography.bodyMedium,
                    color = colorScheme.onSurface.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ConfirmServiceSummaryCardPreview() {
    ConfirmServiceSummaryCard()
}
