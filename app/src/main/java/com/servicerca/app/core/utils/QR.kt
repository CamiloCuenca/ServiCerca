package com.servicerca.app.core.utils

import android.graphics.Bitmap
import android.graphics.Bitmap.createBitmap
import android.graphics.Color
import androidx.core.graphics.set
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import java.util.zip.CRC32

private const val RESERVATION_QR_PREFIX = "servicerca:reservation:"

fun generarQR(datos: String, size: Int = 512): Bitmap? {
    return try {
        val writer = QRCodeWriter()
        // Crea la matriz de puntos (BitMatrix)
        val bitMatrix = writer.encode(datos, BarcodeFormat.QR_CODE, size, size)
        val width = bitMatrix.width
        val height = bitMatrix.height
//        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        val bitmap = createBitmap(width, height, Bitmap.Config.RGB_565)

        // Recorremos la matriz para pintar píxel por píxel
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap[x, y] = if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE
                // bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
            }
        }
        bitmap
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun generarContenidoReservaQR(reservationId: String): String {
    return "$RESERVATION_QR_PREFIX$reservationId"
}

fun leerReservaIdDesdeQR(qrContent: String): String? {
    val normalized = qrContent.trim()
    if (normalized.isBlank()) return null

    return if (normalized.startsWith(RESERVATION_QR_PREFIX)) {
        normalized.removePrefix(RESERVATION_QR_PREFIX).takeIf { it.isNotBlank() }
    } else {
        // Compatibilidad: aceptamos QR antiguos que solo tenían el id de la reserva.
        normalized
    }
}

fun generarCodigoAlternativoReserva(reservationId: String): String {
    val checksum = CRC32().apply { update(reservationId.trim().toByteArray()) }.value
    return checksum.toString(16).uppercase().padStart(8, '0').takeLast(8)
}
