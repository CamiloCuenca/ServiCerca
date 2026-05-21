package com.servicerca.app.core.utils

import android.content.Context
import com.servicerca.app.R
import java.util.concurrent.TimeUnit

/**
 * Utilidades para formatear fechas y timestamps de manera legible.
 */
object DateUtils {
    
    /**
     * Convierte un timestamp en milisegundos a una fecha relativa legible en español.
     * Ejemplos: "Hace 5 minutos", "Hace 2 horas", "Hace 3 días", etc.
     */
    fun getRelativeTimeString(context: Context, timestampMillis: Long): String {
        val now = System.currentTimeMillis()
        val diffMillis = now - timestampMillis
        
        return when {
            diffMillis < 0 -> "Ahora"
            diffMillis < TimeUnit.MINUTES.toMillis(1) -> "Ahora"
            diffMillis < TimeUnit.MINUTES.toMillis(2) -> "Hace 1 minuto"
            diffMillis < TimeUnit.HOURS.toMillis(1) -> {
                val minutes = TimeUnit.MILLISECONDS.toMinutes(diffMillis)
                "Hace $minutes minuto${if (minutes > 1) "s" else ""}"
            }
            diffMillis < TimeUnit.HOURS.toMillis(2) -> "Hace 1 hora"
            diffMillis < TimeUnit.DAYS.toMillis(1) -> {
                val hours = TimeUnit.MILLISECONDS.toHours(diffMillis)
                "Hace $hours hora${if (hours > 1) "s" else ""}"
            }
            diffMillis < TimeUnit.DAYS.toMillis(2) -> "Hace 1 día"
            diffMillis < TimeUnit.DAYS.toMillis(7) -> {
                val days = TimeUnit.MILLISECONDS.toDays(diffMillis)
                "Hace $days día${if (days > 1) "s" else ""}"
            }
            diffMillis < TimeUnit.DAYS.toMillis(30) -> {
                val weeks = diffMillis / TimeUnit.DAYS.toMillis(7)
                "Hace $weeks semana${if (weeks > 1) "s" else ""}"
            }
            else -> {
                val months = diffMillis / TimeUnit.DAYS.toMillis(30)
                "Hace $months mes${if (months > 1) "es" else ""}"
            }
        }
    }
}

