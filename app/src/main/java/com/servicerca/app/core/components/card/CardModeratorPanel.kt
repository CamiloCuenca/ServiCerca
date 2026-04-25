package com.servicerca.app.core.components.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.HighlightOff
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.servicerca.app.R
import com.servicerca.app.core.components.button.ButtonIcon
import com.servicerca.app.core.components.button.ButtonIconDecline
import com.servicerca.app.core.components.tag.NewServiceTag

@Composable
fun CardModeratorPanelScreen (
    isNewService: Boolean = true,
    imageUrl: String,
    type: String,
    tittle: String,
    description: String,
    onVerifyClick: () -> Unit,
    onRejectClick: () -> Unit,
    onClick: () -> Unit = onVerifyClick
){
    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Column {

            // Imagen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.electrician), // Imagen por defecto mientras carga
                    error = painterResource(R.drawable.electrician)       // Imagen si falla la carga
                )

                if (isNewService) {
                    NewServiceTag(modifier = Modifier.align(Alignment.TopEnd))
                }
            }

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = type.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = tittle,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ButtonIcon(
                        text = stringResource(R.string.btn_virified_moderation_panel),
                        onClick = onVerifyClick,
                        icon = {
                            Icon(
                                imageVector = Icons.Default.CheckCircleOutline,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        modifier = Modifier.weight(1f)
                    )

                    ButtonIconDecline(
                        text = stringResource(R.string.btn_decline_moderation_panel),
                        onClick = onRejectClick,
                        icon = {
                            Icon(
                                imageVector = Icons.Default.HighlightOff,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun CardModeratorPanelPreview(){
    CardModeratorPanelScreen(
        imageUrl = "https://projectssdn.com/wp-content/uploads/elementor/thumbs/plomeria-en-general-qp5x9n6u64ze4tk30xqoxt57okaxdr7apr7hp13vds.png",
        type = "Tipo",
        tittle = "Título",
        description = "Descripción",
        onVerifyClick = {},
        onRejectClick = {}
    )
}