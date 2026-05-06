package com.servicerca.app.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    secondary = SecondaryDark,
    onSecondary = OnSecondaryDark,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    primaryContainer = Color(0xFF1B3D5E),       // Azul profundo para contenedores
    onPrimaryContainer = Color(0xFFD1E4FF),     // Azul muy claro
    secondaryContainer = Color(0xFF1D2E45),     // Medianoche oscuro
    onSecondaryContainer = Color(0xFFD6E3FF),   // Texto suave azulado
    tertiaryContainer = Color(0xFF2D253D),      // Púrpura apagado
    onTertiaryContainer = Color(0xFFD4C8E2),    // Lila suave
    surfaceVariant = Color(0xFF252B35),         // Variante de superficie azul pizarra
    onSurfaceVariant = Color(0xFFBCC7D5),       // Texto secundario azulado suave
    surfaceContainerHighest = Color(0xFF1E242E), // Para elementos elevados en azul
    error = ErrorDark,
    onError = OnErrorDark,
    errorContainer = ErrorContainerDark,
    onErrorContainer = OnErrorContainerDark
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = OnPrimaryLight,
    secondary = SecondaryLight,
    onSecondary = OnSecondaryLight,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    primaryContainer = Color(0xFFE0F2F1),
    onPrimaryContainer = PrimaryLight,
    secondaryContainer = Color(0xFFE0F7FA),
    onSecondaryContainer = SecondaryLight,
    tertiaryContainer = Color(0xFFF3E5F5), // Light Purple
    onTertiaryContainer = Color(0xFF7F56D9),
    surfaceVariant = Color(0xFFE1E2E4),
    onSurfaceVariant = Color(0xFF454748),
    error = ErrorLight,
    onError = OnErrorLight,
    errorContainer = ErrorContainerLight,
    onErrorContainer = OnErrorContainerLight
)

val LocalAppIsDark = compositionLocalOf { false }

@Composable
fun ServiCercaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    CompositionLocalProvider(LocalAppIsDark provides darkTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

/**
 * Helper composable para obtener los colores recomendados para un BottomNavigation/NavigationBar
 * según el tema activo (modo claro/oscuro). Devuelve Pair(fondo, contenido).
 * Uso sugerido:
 * val (bg, content) = NavigationBarColors()
 * BottomNavigation(backgroundColor = bg, contentColor = content) { ... }
 */
@Composable
fun NavigationBarColors(): Pair<Color, Color> {
    val colors = MaterialTheme.colorScheme
    // Por defecto usamos surface/onSurface para mantener contraste accesible.
    return Pair(colors.surface, colors.onSurface)
}
