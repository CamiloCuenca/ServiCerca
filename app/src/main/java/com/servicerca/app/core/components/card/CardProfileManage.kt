package com.servicerca.app.core.components.card

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.servicerca.app.R

@Composable
fun CardProfileManage(
    name : String,
    email : String,
    imageProfile : Int,
    onOpenOptions : () -> Unit ={}
) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,

            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp)
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ){
            Image(
                painter = painterResource(id = imageProfile),
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .align(Alignment.CenterVertically)
            )

            Spacer(modifier = Modifier.size(16.dp))

             Column (
                 modifier = Modifier
                        .align(Alignment.CenterVertically)
             ){
                 Text(
                     text = name,
                     style = MaterialTheme.typography.titleLarge,
                     fontWeight = FontWeight.Bold
                 )

                 Text(
                     text = email,
                     style = MaterialTheme.typography.bodyMedium,
                     color = MaterialTheme.colorScheme.outline
                 )
             }

            Spacer(modifier = Modifier.weight(1f))


            IconButton(
                onClick = { onOpenOptions() },
                modifier = Modifier
                    .size(30.dp)
                    .align(Alignment.CenterVertically)

            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = null
                )
            }

        }
    }
}

@Composable
@Preview(showBackground = false)
fun CardProfileManagePreview() {
    CardProfileManage(
        name = "Heliana Cuenca",
        email = "juan.camilo@email.com",
        imageProfile = R.drawable.foto_jcc
    )
}