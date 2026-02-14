package com.servicerca.app.ui.theme

import androidx.compose.ui.graphics.Color

// Palette provided by user (light)
val PrimaryLight = Color(0xFF13ECEC)      // Cian Brillante
val SecondaryLight = Color(0xFF006064)    // Azul Petróleo
val SurfaceLight = Color(0xFFF8FDFF)      // Azul Glaciar (Cards / Contenedores)
val BackgroundLight = Color(0xFFF8FDFF)   // Fondo
val TextPrimaryLight = Color(0xFF1A1C1E)  // Casi negro
val Error = Color(0xFFBA1A1A)             // Rojo Material

// Contrapartes pensadas para modo oscuro
val PrimaryDark = Color(0xFF00A3A3)       // versión más suave/oscura del cian
val SecondaryDark = Color(0xFF4FB3B3)     // versión más clara del petróleo para contraste en oscuro
val SurfaceDark = Color(0xFF042428)       // superficie oscura con matiz azulado
val BackgroundDark = Color(0xFF001F22)    // fondo muy oscuro con matiz teal
val TextPrimaryDark = Color(0xFFF8FDFF)   // texto claro sobre fondos oscuros
val ErrorDark = Color(0xFFBA1A1A)         // mantengo el mismo rojo para alertas

// Helpers on* colors
val OnPrimaryLight = TextPrimaryLight
val OnSecondaryLight = Color(0xFFFFFFFF)
val OnBackgroundLight = TextPrimaryLight
val OnSurfaceLight = TextPrimaryLight
val OnError = Color(0xFFFFFFFF)

val OnPrimaryDark = Color(0xFF001F1F)
val OnSecondaryDark = Color(0xFFFFFFFF)
val OnBackgroundDark = TextPrimaryDark
val OnSurfaceDark = TextPrimaryDark
val OnErrorDark = Color(0xFFFFFFFF)
