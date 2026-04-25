package com.servicerca.app.core.components.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.servicerca.app.R

@Composable
fun CardProfileModerator(
    title: String,
    label: String,
    color: Color,
    onClick: () -> Unit = {}
){
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp),
        onClick = onClick,
        modifier = Modifier
            .size(width = 350.dp, height = 100.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(16.dp)
            ) {
            Row(
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            Row(
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = color
                )
            }
        }
    }
}

@Preview (showBackground = true)
@Composable
fun CardProfileModeratorPreview(){
    CardProfileModerator(
        title = stringResource(R.string.pending_profile_moderator),
        label = stringResource(R.string.pending_number),
        color = Color(0xFFDB9C16)
    )

}
