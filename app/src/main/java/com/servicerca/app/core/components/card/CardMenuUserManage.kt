package com.servicerca.app.core.components.card

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.servicerca.app.R
import com.servicerca.app.core.components.card.menu.MenuOptionUserManage

@Composable
fun CardMenuUserManage (
    onSeeProfile: () -> Unit = {},
    onSuspendProfile: () -> Unit = {},
    onDeleteProfile: () -> Unit = {}
){

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth(1f),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),

    ) {
        MenuOptionUserManage(
            text = stringResource(R.string.menu_option_see_profile),
            icon = Icons.Default.RemoveRedEye,
            onClick = onSeeProfile
        )
        MenuOptionUserManage(
            text = stringResource(R.string.menu_option_suspend_profile),
            icon = Icons.Default.Block,
            onClick = onSuspendProfile
        )
        HorizontalDivider()
        MenuOptionUserManage(
            text = stringResource(R.string.menu_option_delete_profile),
            icon = Icons.Default.Delete,
            onClick = onDeleteProfile
        )
    }
}

@Preview(showBackground = false)
@Composable
fun CardMenuUserManagePreview(){
    CardMenuUserManage()
}