package com.iudigital.iudigitalradio.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import com.iudigital.iudigitalradio.data.model.RadioStation
import com.iudigital.iudigitalradio.data.model.StationCategory
import com.iudigital.iudigitalradio.ui.theme.AccentCyan
import com.iudigital.iudigitalradio.ui.theme.AccentViolet
import com.iudigital.iudigitalradio.ui.theme.BrandBlue
import com.iudigital.iudigitalradio.ui.theme.Navy800
import com.iudigital.iudigitalradio.ui.theme.Navy900
import com.iudigital.iudigitalradio.ui.theme.Navy950

/**
 * Fondo con degradado y dos luces difusas ("aurora") en esquinas opuestas.
 * Se usa en la tarjeta principal de Inicio, el reproductor, el mini reproductor y el perfil.
 */
fun Modifier.auroraBackground(
    colors: List<Color>,
    topGlow: Color = AccentCyan,
    bottomGlow: Color = AccentViolet,
): Modifier = drawBehind {
    drawRect(Brush.linearGradient(colors, start = Offset.Zero, end = Offset(size.width, size.height)))
    drawGlow(topGlow, center = Offset(size.width * 0.95f, size.height * 0.05f), radius = size.maxDimension * 0.55f)
    drawGlow(bottomGlow, center = Offset(size.width * 0.05f, size.height), radius = size.maxDimension * 0.5f)
}

private fun DrawScope.drawGlow(color: Color, center: Offset, radius: Float) {
    drawCircle(
        brush = Brush.radialGradient(listOf(color.copy(alpha = 0.42f), Color.Transparent), center = center, radius = radius),
        radius = radius,
        center = center,
    )
}

/** Colores del "escenario" del reproductor: azul de marca para la emisora oficial y el color de su tipo para las demás. */
fun RadioStation.stageColors(): List<Color> =
    if (isOfficial) {
        listOf(Navy950, Navy800, BrandBlue)
    } else {
        listOf(Navy950, Navy900, (category ?: StationCategory.POPULAR).style.colors.last())
    }

/** Subtítulo corto de una emisora para tarjetas y reproductor. */
fun RadioStation.subtitle(): String = when {
    isOfficial -> "Emisora oficial · IU Digital"
    details.isNotBlank() -> details
    else -> category?.label ?: "Emisora externa"
}

/** Etiqueta translúcida para fondos oscuros (calidad de audio, tipo de radio…). */
@Composable
fun InfoPill(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = Color.White,
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(Color.White.copy(alpha = 0.16f))
            .padding(horizontal = 10.dp, vertical = 4.dp),
    )
}
