package com.servicerca.app.ui.reservation.details

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.servicerca.app.R
import com.servicerca.app.core.components.card.CardDetailsReservation

@Composable
fun DetailsReservationScreen(){

    Column() {
        CardDetailsReservation(
            serviceRequestedLabel = stringResource(R.string.reservation_servicio_solicitado),
            statusText = stringResource(R.string.reservation_status_confirmed),
            statusContainerColor = Color(0xFFD1FADF),
            statusContentColor = Color(0xFF027A48),
            serviceTitle = stringResource(R.string.reservation_service_plumber),
            professionalName = "Kavin Payanene",
            professionalBadgeText = stringResource(R.string.reservation_profesional_certificado),
            rating = "4.9"

        )
    }

}


@Composable
@Preview
fun DetailsReservationScreenPreview(){
    DetailsReservationScreen()

}