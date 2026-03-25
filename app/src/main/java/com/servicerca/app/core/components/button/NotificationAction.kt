package com.servicerca.app.core.components.button

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.servicerca.app.R

@Composable
fun AppNotificationAction(
    count: Int,
    onClick: () -> Unit
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.lottie_notification))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1, // Animate once
        speed = 1.25f   // Elegant speed
    )

    BadgedBox(
        badge = {
            if (count > 0) {
                Badge {
                    Text(
                        text = if (count > 99) "99+" else count.toString()
                    )
                }
            }
        },
        modifier = Modifier.padding(end = 15.dp)
    ) {
        IconButton(onClick = onClick) {
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.size(40.dp) // Aumentando el tamaño del Lottie
            )
        }
    }
}