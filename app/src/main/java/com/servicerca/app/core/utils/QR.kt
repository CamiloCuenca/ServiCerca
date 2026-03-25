package com.servicerca.app.core.utils

import android.graphics.Bitmap
import android.graphics.Bitmap.createBitmap
import android.graphics.Color
import androidx.core.graphics.set
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

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