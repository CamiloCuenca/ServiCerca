package com.servicerca.app.core.components.card

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.servicerca.app.R
import com.servicerca.app.core.components.card.menu.MenuOptionModerator

@Composable
fun CardMenuModerator() {

    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(219.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {

        Column {

            MenuOptionModerator(
                icon = Icons.Default.Checklist,
                text = stringResource(R.string.validate_new_services),
                onClick = { }
            )

            Divider()

            MenuOptionModerator(
                icon = Icons.Default.Group,
                text = stringResource(R.string.user_management),
                onClick = { }
            )

            Divider()

            MenuOptionModerator(
                icon = Icons.Default.History,
                text = stringResource(R.string.moderation_history),
                onClick = { }
            )
        }
    }
}

@Preview (showBackground = true)
@Composable
fun CardMenuModeratorPreview() {
    CardMenuModerator()
}