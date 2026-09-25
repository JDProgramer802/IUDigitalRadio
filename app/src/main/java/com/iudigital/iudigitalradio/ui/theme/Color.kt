package com.iudigital.iudigitalradio.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Paleta tomada del logo de IU Digital Radio.
val Navy950 = Color(0xFF060F26)
val Navy900 = Color(0xFF0B1B3F)
val Navy800 = Color(0xFF12306B)
val Navy700 = Color(0xFF1B3F8A)
val BrandBlue = Color(0xFF1A73FF)
val BrandBlueLight = Color(0xFF5C9DFF)
val Blue50 = Color(0xFFEAF2FF)
val Blue100 = Color(0xFFD6E6FF)

// Acentos para degradados y efectos de "aurora".
val AccentCyan = Color(0xFF22D3EE)
val AccentViolet = Color(0xFF8B5CF6)

val Background = Color(0xFFF4F7FC)
val SurfaceWhite = Color(0xFFFFFFFF)
val SurfaceMuted = Color(0xFFEEF3FB)
val TextSecondary = Color(0xFF5A6A89)
val Outline = Color(0xFFC9D4E8)
val OutlineSoft = Color(0xFFE2E9F5)

val LiveRed = Color(0xFFE5383B)
val ErrorRed = Color(0xFFC62828)

// Modo oscuro
val DarkBackground = Color(0xFF070F22)
val DarkSurface = Color(0xFF0F1A33)
val DarkSurfaceHigh = Color(0xFF17264A)
val DarkTextSecondary = Color(0xFFA3B3D1)
val DarkOutline = Color(0xFF34466B)

/** Degradado de marca: de azul marino profundo a azul brillante. */
val BrandGradient = Brush.linearGradient(listOf(Navy950, Navy800, BrandBlue))

/** Anillo de colores para la foto de perfil y los elementos destacados. */
val AccentRing = listOf(BrandBlue, AccentCyan, AccentViolet, BrandBlue)
