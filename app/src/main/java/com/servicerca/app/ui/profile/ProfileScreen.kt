package com.servicerca.app.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.servicerca.app.R
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import com.servicerca.app.core.components.button.ButtonIcon
import com.servicerca.app.core.components.button.DeleteButton
import com.servicerca.app.core.components.button.PrimaryButton
import com.servicerca.app.core.components.card.CardLevel
import com.servicerca.app.core.components.card.CardStatistics

@Composable
fun ProfileScreen(
    onInsignias : () -> Unit,
){

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {


            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    contentAlignment = Alignment.BottomEnd, // El icono va en la esquina inferior derecha
                    modifier = Modifier.size(150.dp)
                ) {
                    // Imagen de perfil
                    Card(
                        shape = CircleShape,
                        colors = CardDefaults.cardColors(
                            Color.White
                        ),
                        modifier = Modifier
                            .size(150.dp)
                            .shadow(
                                elevation = 20.dp,
                                shape = CircleShape,
                                ambientColor = Color.Cyan,
                                spotColor = Color.Cyan
                            )
                    )
                    {
                        Image(painter = painterResource(id = R.drawable.logo_profile), contentDescription = null,
                            modifier = Modifier
                                .size(250.dp)
                                .shadow(
                                    elevation = 16.dp,
                                    shape = CircleShape,
                                    ambientColor = Color.Cyan,
                                    spotColor = Color.Cyan
                                )
                        )
                    }


                    // Icono para cambiar foto
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Cambiar foto",
                        tint = Color.White, //Color del lapiz
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.Black, CircleShape) //Color del circulo
                            .padding(8.dp)
                            .clickable {
                                // Aca iria la acción para cambiar la foto que no sé como hacer :D
                            }
                    )
                }
            }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.name_user),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = "Ubicación",
                        tint = Color.Cyan,
                        modifier = Modifier.size(25.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = stringResource(R.string.lacation_profile),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CardLevel()
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.tittle_insignias),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = stringResource(R.string.ver_todas),
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Cyan,
                        modifier = Modifier.clickable {onInsignias()}
                    )
                }
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
                        Image(painter = painterResource(id = R.drawable.insignia_verificado), contentDescription = null,
                            modifier = Modifier
                                .size(80.dp)
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
                            )
                    }
                    Column(
                        Modifier.padding(top = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(painter = painterResource(id = R.drawable.insignia_rapido), contentDescription = null,
                            modifier = Modifier
                                .size(80.dp)
                                .shadow(
                                    elevation = 8.dp,
                                    shape = CircleShape,
                                    ambientColor = Color(0xFF9C27B0),
                                    spotColor = Color(0xFF9C27B0)
                                )
                        )
                        Text(
                            text = stringResource(R.string.insignia_fast),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                    Column(
                        Modifier.padding(top = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(painter = painterResource(id = R.drawable.insignia_top5), contentDescription = null,
                            modifier = Modifier
                                .size(80.dp)
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
                        )
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.statistic),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CardStatistics(
                        imageRes = R.drawable.servicios_completados,
                        number = stringResource(R.string.num_services),
                        label = stringResource(R.string.services_completed)
                    )

                    CardStatistics(
                        imageRes = R.drawable.puntos_totales,
                        number = stringResource(R.string.num_points),
                        label = stringResource(R.string.total_points)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, top = 30.dp, end = 10.dp, bottom = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CardStatistics(
                        imageRes = R.drawable.calificacion,
                        number = stringResource(R.string.qualification),
                        label = stringResource(R.string.average_rating)
                    )

                    CardStatistics(
                        imageRes = R.drawable.tiempo_miembro,
                        number = stringResource(R.string.time),
                        label = stringResource(R.string.member_time)
                    )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, bottom = 15.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ButtonIcon(
                        text = stringResource(R.string.edit_account),
                        onClick = { /* acción */ },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Edit, // Icono de Material
                                contentDescription = null
                            )
                        }
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, bottom = 15.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DeleteButton(
                        text = stringResource(R.string.delete_account),
                        onClick = { /* acción */ },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Delete, // Icono de Material
                                contentDescription = null
                            )
                        }
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(
        onInsignias = {}
    )
}
