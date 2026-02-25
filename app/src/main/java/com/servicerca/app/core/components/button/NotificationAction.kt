package com.servicerca.app.core.components.button

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AppNotificationAction(
    count: Int,
    onClick: () -> Unit
) {
    BadgedBox(
        badge = {
            if (count > 0) {
                Badge {
                    Text(
                        text = if (count > 99) "99+" else count.toString()
                    )
                }
            }
        }
        ,modifier = Modifier.padding(end = 15.dp)
    ) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = "Notificaciones"
            )
        }
    }
}