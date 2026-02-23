package com.servicerca.app.core.components.card

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@Composable
fun CardMainInsignias(){
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp),
        modifier = Modifier
            .size(width = 340.dp, height = 150.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
    ){
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

                Column(
                    Modifier.padding(top = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Image(
                        painter = painterResource(id = R.drawable.insignia_top5),
                        contentDescription = null,
                        modifier = Modifier.size(80.dp)
                            .shadow(
                                elevation = 8.dp,
                                shape = CircleShape,
                                ambientColor = Color(0xFFFFEB3B),
                                spotColor = Color(0xFFFFEB3B)
                            )
                    )

                    Text(
                        text = stringResource(R.string.insignia_top5),
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(90.dp),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Column(
                    Modifier.padding(top = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Image(
                        painter = painterResource(id = R.drawable.insignia_favorita),
                        contentDescription = null,
                        modifier = Modifier.size(80.dp)
                            .shadow(
                                elevation = 8.dp,
                                shape = CircleShape,
                                ambientColor = Color.Red,
                                spotColor = Color.Red
                            )
                    )

                    Text(
                        text = stringResource(R.string.insignia_favorite),
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(90.dp),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Column(
                    Modifier.padding(top = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Image(
                        painter = painterResource(id = R.drawable.insignia_verificado),
                        contentDescription = null,
                        modifier = Modifier.size(80.dp)
                            .shadow(
                                elevation = 8.dp,
                                shape = CircleShape,
                                ambientColor = Color.Cyan,
                                spotColor = Color.Cyan
                            )
                    )

                    Text(
                        text = stringResource(R.string.insignia_verified),
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(90.dp),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }

    }
}


@Composable
@Preview
fun CardMainInsigniasPreview(){
    CardMainInsignias()

}