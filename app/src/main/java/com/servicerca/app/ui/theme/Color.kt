package com.servicerca.app.ui.theme

import androidx.compose.ui.graphics.Color

// Palette provided by user (light)
val PrimaryLight = Color(0xFF00C8B3)      // Cian Brillante
val SecondaryLight = Color(0xFF006064)    // Azul Petróleo
val SurfaceLight = Color(0xFFF8FDFF)      // Azul Glaciar (Cards / Contenedores)
val BackgroundLight = Color(0xFFF8FDFF)   // Fondo
val TextPrimaryLight = Color(0xFF1A1C1E)  // Casi negro
val ErrorLight = Color(0xFFBA1A1A)             // Rojo Material
val OnErrorLight = Color(0xFFFFFFFF)
val ErrorContainerLight = Color(0xFFFFDAD6)
val OnErrorContainerLight = Color(0xFF410002)

// Contrapartes pensadas para modo oscuro
val PrimaryDark = Color(0xFF1272C6)       // versión más suave/oscura del cian
val SecondaryDark = Color(0xCE0802BF)     // versión más clara del petróleo para contraste en oscuro
val SurfaceDark = Color(0xFF545454)   // superficie oscura con matiz azulado
val BackgroundDark = Color(0xFF3C3C3C)   // fondo muy oscuro con matiz teal
val TextPrimaryDark = Color(0xFFF8FDFF)   // texto claro sobre fondos oscuros
val ErrorDark = Color(0xFFFFB4AB)         // Rojo más claro para mejor contraste en oscuro
val OnErrorDark = Color(0xFF690005)
val ErrorContainerDark = Color(0xFF93000A)
val OnErrorContainerDark = Color(0xFFFFDAD6)

// Helpers on* colors (Light)
val OnPrimaryLight = Color(0xFFFFFFFF)
val OnSecondaryLight = Color(0xFFFFFFFF)
val OnBackgroundLight = TextPrimaryLight
val OnSurfaceLight = TextPrimaryLight

// Helpers on* colors (Dark)
val OnPrimaryDark = Color(0xFFFFFFFF)
val OnSecondaryDark = Color(0xFFFFFFFF)
val OnBackgroundDark = TextPrimaryDark
val OnSurfaceDark = TextPrimaryDark

// Semantic Colors
val SuccessLight = Color(0xFF027A48)
val OnSuccessLight = Color.White
val SuccessContainerLight = Color(0xFFD1FADF)
val OnSuccessContainerLight = Color(0xFF027A48)

val WarningLight = Color(0xFFB54708)
val OnWarningLight = Color.White
val WarningContainerLight = Color(0xFFFEF0C7)
val OnWarningContainerLight = Color(0xFFB54708)

val InfoLight = Color(0xFF175CD3)
val OnInfoLight = Color.White
val InfoContainerLight = Color(0xFFD1E9FF)
val OnInfoContainerLight = Color(0xFF175CD3)

val StarColor = Color(0xFFFFD700)
val StarInactiveColor = Color(0xFFE0E0E0)