package com.iudigital.iudigitalradio.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val Base = Typography()

// Títulos gruesos y compactos (como el logo) y texto de lectura cómodo.
val Typography = Typography(
    headlineMedium = Base.headlineMedium.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.5).sp),
    headlineSmall = Base.headlineSmall.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.3).sp),
    titleLarge = Base.titleLarge.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.2).sp),
    titleMedium = Base.titleMedium.copy(fontWeight = FontWeight.Bold),
    titleSmall = Base.titleSmall.copy(fontWeight = FontWeight.SemiBold),
    bodyLarge = TextStyle(fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.3.sp),
    labelLarge = Base.labelLarge.copy(fontWeight = FontWeight.SemiBold),
    labelMedium = Base.labelMedium.copy(fontWeight = FontWeight.SemiBold),
    labelSmall = Base.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp),
)
