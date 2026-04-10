package com.servicerca.app.core.components.card



import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.servicerca.app.R

@Composable
fun CardInfoDeleteProfile(){
    Card(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp),
        modifier = Modifier
            .size(width = 340.dp, height = 120.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
    ){
        Column(
        ) {
            Row(
                modifier = Modifier
                    .padding(vertical = 10.dp, horizontal = 15.dp)) {
                Icon(
                    imageVector = Icons.Default.Info, // Icono de Material
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(end = 8.dp)
                )
                Text(
                    text = stringResource(R.string.data_question),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
            val options = listOf(
                R.string.option1,
                R.string.option2,
                R.string.option3
            )

            Row(
                modifier = Modifier
                    .padding(bottom = 15.dp)
            ) {
                LazyColumn {
                    items(options) { stringId ->

                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier
                                .padding(start = 45.dp, end = 15.dp)
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(
                                        color = MaterialTheme.colorScheme.onSurface,
                                        shape = CircleShape
                                    )
                                    .align(Alignment.CenterVertically)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = stringResource(id = stringId),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }


        }

    }
}


@Composable
@Preview
fun CardInfoDeleteProfilePreview(){
    CardInfoDeleteProfile()

}