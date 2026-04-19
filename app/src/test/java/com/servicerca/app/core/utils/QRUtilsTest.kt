package com.servicerca.app.core.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class QRUtilsTest {

    @Test
    fun generarContenidoReservaQR_y_leerReservaIdDesdeQR_devuelvenIdOriginal() {
        val reservationId = "reserva-123"

        val qrContent = generarContenidoReservaQR(reservationId)
        val parsedId = leerReservaIdDesdeQR(qrContent)

        assertEquals(reservationId, parsedId)
    }

    @Test
    fun leerReservaIdDesdeQR_aceptaFormatoLegacyConSoloId() {
        val parsedId = leerReservaIdDesdeQR("legacy-id")
        assertEquals("legacy-id", parsedId)
    }

    @Test
    fun leerReservaIdDesdeQR_retornaNull_conContenidoVacio() {
        assertNull(leerReservaIdDesdeQR("   "))
    }

    @Test
    fun generarCodigoAlternativoReserva_generaCodigoDe8Caracteres() {
        val code = generarCodigoAlternativoReserva("reserva-abc123456")
        assertEquals("C43E94D1", code)
    }
}
