package com.servicerca.app.core.components.card

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ripple
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.servicerca.app.R
import com.servicerca.app.ui.profile.InsigniaUiModel

@Composable
fun CardMainInsignias(
    insignias: List<InsigniaUiModel>,
    onInsigniaClick: (InsigniaUiModel) -> Unit = {}
) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        modifier = Modifier.size(width = 340.dp, height = 150.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier.padding(vertical = 18.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Show first 3 insignias or limited set
                val displayInsignias = insignias.take(3)
                displayInsignias.forEach { insignia ->
                    InsigniaMainItem(insignia, onInsigniaClick)
                }
                
                // Fill if less than 3
                repeat(3 - displayInsignias.size) {
                    Spacer(Modifier.width(90.dp))
                }
            }
        }
    }
}

@Composable
fun InsigniaMainItem(
    insignia: InsigniaUiModel,
    onClick: (InsigniaUiModel) -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        Modifier
            .padding(top = 8.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = false, radius = 40.dp),
                onClick = { onClick(insignia) }
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = insignia.iconRes),
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .shadow(
                    elevation = 2.dp,
                    shape = CircleShape,
                    ambientColor = if (insignia.isEarned) insignia.shadowColor else Color.Gray.copy(alpha = 0.3f),
                    spotColor = if (insignia.isEarned) insignia.shadowColor else Color.Gray.copy(alpha = 0.3f)
                ),
            alpha = if (insignia.isEarned) 1.0f else 0.4f
        )

        Text(
            text = stringResource(insignia.nameRes),
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(90.dp),
            color = if (insignia.isEarned) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
        )
    }
}

@Composable
@Preview
fun CardMainInsigniasPreview() {
    CardMainInsignias(insignias = emptyList())
}