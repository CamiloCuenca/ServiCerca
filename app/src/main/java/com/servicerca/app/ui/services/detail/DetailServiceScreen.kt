package com.servicerca.app.ui.services.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.servicerca.app.core.components.button.PrimaryButton
import com.servicerca.app.core.components.input.AppPasswordField
import com.servicerca.app.core.components.input.AppTextField
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.OutlinedButton
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SuggestionChip

import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.servicerca.app.R
import com.servicerca.app.core.components.button.SocialButton

@Composable
fun DetailServiceScreen(



) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 24.dp)


    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .weight(1f) // 👈 esto empuja el contenido de abajo hacia el final

        ) {
            Box(
                modifier = Modifier.fillMaxWidth().height(300.dp)
            ){
                Image(
                    painter = painterResource(id = R.drawable.service),
                    contentDescription = "service",
                    contentScale = ContentScale.Crop, // importante para que llene bien
                    modifier = Modifier.fillMaxSize()
                )

                IconButton(
                    onClick = { /*TODO*/ },
                    modifier = Modifier.align(Alignment.TopStart)
                        .padding(8.dp)
                        .background(
                            color = Color.Black.copy(alpha = 0.5f),
                            shape = CircleShape
                        )


                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White
                    )

                }

                IconButton(
                    onClick = { /*TODO*/ },
                    modifier = Modifier.align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(
                            color = Color.Black.copy(alpha = 0.5f),
                            shape = CircleShape
                        )


                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Volver",
                        tint = Color.White
                    )



                }



            }


            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .border(
                        width = 1.dp
                        ,color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(16.dp)
                    )

            ) {

                Spacer(modifier = Modifier.height(20.dp))

               Row(
                   modifier = Modifier.fillMaxWidth()
                       .padding(16.dp),
                   horizontalArrangement = Arrangement.SpaceBetween,
                   verticalAlignment = Alignment.Top


               ) {
                   Column(
                       modifier = Modifier.weight(1f),
                       verticalArrangement = Arrangement.spacedBy(4.dp)

                   ) {
                       Row(
                           horizontalArrangement = Arrangement.spacedBy(4.dp),
                           verticalAlignment = Alignment.CenterVertically


                       ) {
                           AssistChip(
                               onClick = { /*TODO*/ },
                               label = {Text("Verificada", fontSize = 12.sp) },
                               leadingIcon = {
                                   Icon(
                                       imageVector = Icons.Default.CheckCircle,
                                       contentDescription = null,
                                       tint = Color(0xFF00BFA5),
                                       modifier = Modifier.size(16.dp)
                                   )
                               }

                           )

                           SuggestionChip(
                               onClick = { /*TODO*/ },
                               label = {Text("HOGAR", fontSize = 12.sp)}
                           )
                       }
                       Text(
                           text = "Reparacion de Fuga",
                           style = MaterialTheme.typography.headlineMedium,
                           fontWeight = FontWeight.Bold,
                       )

                       Text(
                           text = "Plomeria Residencial",
                           style = MaterialTheme.typography.bodyMedium,
                           color = Color.Gray
                       )
                   }
                   Column(
                       horizontalAlignment = Alignment.End
                   ) {
                       Text(
                           text = "$350",
                           style = MaterialTheme.typography.headlineMedium,
                           color = Color(0xFF00BCD4),
                           fontWeight = FontWeight.Bold
                       )
                       Text(
                           text = "por hora",
                           style = MaterialTheme.typography.bodySmall,
                           color = Color.Gray
                       )
                   }
               }





            }





        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            PrimaryButton(
                text = "Solicitar Servicio",
                onClick = { /* Registrarse */ }
            )
        }




    }
}

@Preview (showBackground = true , showSystemUi = true)
@Composable
fun DetailServiceScreenPreview() {
    DetailServiceScreen()
}