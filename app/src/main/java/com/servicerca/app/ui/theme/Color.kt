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

// Contrapartes pensadas para modo oscuro (Azules Profundos - Sapphire & Midnight)
val PrimaryDark = Color(0xFF5C9DFF)       // Azul Zafiro brillante pero suave
val SecondaryDark = Color(0xFF2E5A9D)     // Azul Medianoche
val SurfaceDark = Color(0xFF161B26)       // Azul Pizarra profundo
val BackgroundDark = Color(0xFF0A0D14)    // Negro con tinte azul medianoche
val TextPrimaryDark = Color(0xFFE6E9EF)   // Blanco azulado suave
val ErrorDark = Color(0xFFFFB4AB)         // Rojo material estándar dark
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

// ─── Semantic: Success (Aprobado) ─────────────────────────────────────────────
// Modo claro: verde oscuro legible sobre fondo blanco
val SuccessLight = Color(0xFF1A7F37)           // Verde oscuro — texto/icono en modo claro
val OnSuccessLight = Color.White
val SuccessContainerLight = Color(0xFFCCFBD3)  // Verde muy suave — fondo del chip/badge
val OnSuccessContainerLight = Color(0xFF0A3D17) // Verde muy oscuro — texto sobre el container

// Modo oscuro: verde lima brillante sobre fondo oscuro teal
val SuccessDark = Color(0xFF5EDB7A)            // Verde lima — texto/icono en modo oscuro
val OnSuccessDark = Color(0xFF00200A)           // Negro verdoso — sobre el primario oscuro
val SuccessContainerDark = Color(0xFF0D3B1A)   // Verde muy oscuro — fondo container en oscuro
val OnSuccessContainerDark = Color(0xFFA3F0B1)  // Verde claro — texto sobre container oscuro

// ─── Semantic: Warning (Pendiente) ────────────────────────────────────────────
// Modo claro: ámbar cálido
val WarningLight = Color(0xFFA05C00)           // Ámbar oscuro — texto/icono en modo claro
val OnWarningLight = Color.White
val WarningContainerLight = Color(0xFFFFF0C2)  // Amarillo muy suave — fondo del badge
val OnWarningContainerLight = Color(0xFF3E2400) // Marrón oscuro — texto sobre container

// Modo oscuro: amarillo brillante visible
val WarningDark = Color(0xFFFFCC40)            // Amarillo ámbar brillante — sobre fondo oscuro
val OnWarningDark = Color(0xFF3E2400)           // Marrón muy oscuro — sobre primario oscuro
val WarningContainerDark = Color(0xFF3D2700)   // Marrón muy oscuro — fondo container en oscuro
val OnWarningContainerDark = Color(0xFFFFE49E)  // Amarillo claro — texto sobre container oscuro

// ─── Info ──────────────────────────────────────────────────────────────────────
val InfoLight = Color(0xFF0B50C2)
val OnInfoLight = Color.White
val InfoContainerLight = Color(0xFFD6E4FF)
val OnInfoContainerLight = Color(0xFF001A4D)

val InfoDark = Color(0xFF8AB4F8)
val OnInfoDark = Color(0xFF001A4D)
val InfoContainerDark = Color(0xFF0A2A6B)
val OnInfoContainerDark = Color(0xFFD6E4FF)

// ─── Stars ────────────────────────────────────────────────────────────────────
val StarColor = Color(0xFFFFD700)
val StarInactiveColor = Color(0xFFE0E0E0)
