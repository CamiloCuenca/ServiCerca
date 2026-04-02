package com.servicerca.app.core.components.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CardStatisticsUserManage(
    number : String,
    label : String,
    colorAdd : Color = Color(0xFF074014),
    colorPending : Color = Color(0xFFA81313)
) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,

        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        modifier = Modifier
            .size(width = 170.dp, height = 80.dp)
            .padding(2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 4.dp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline,
            )

            if(label.contains("Pendientes")){
                Text(
                    text = number,
                    style = MaterialTheme.typography.titleLarge,
                    color = colorPending,
                    fontWeight = FontWeight.Bold
                )
            } else {

                Text(
                    text = number,
                    style = MaterialTheme.typography.titleLarge,
                    color = if (number.contains('+'))
                        colorAdd else MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = false)
fun CardStatisticsUserManagePreview() {
    CardStatisticsUserManage(
        number = "5",
        label = "Pendientes de revisión"
    )
}