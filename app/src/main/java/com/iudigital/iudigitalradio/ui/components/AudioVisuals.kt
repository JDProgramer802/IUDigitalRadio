package com.iudigital.iudigitalradio.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.iudigital.iudigitalradio.data.model.RadioStation
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sin

private val VinylDark = Color(0xFF0C0E13)
private val VinylLight = Color(0xFF2B303B)

/**
 * Disco de vinilo con la imagen de la emisora en la etiqueta central.
 * Gira mientras suena el audio y se detiene donde quedó al pausar.
 */
@Composable
fun VinylRecord(
    station: RadioStation,
    isSpinning: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
) {
    val rotation = remember { Animatable(0f) }
    LaunchedEffect(isSpinning) {
        if (!isSpinning) return@LaunchedEffect
        while (true) {
            rotation.animateTo(rotation.value + 360f, tween(durationMillis = 7_000, easing = LinearEasing))
            rotation.snapTo(rotation.value % 360f)
        }
    }

    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { rotationZ = rotation.value }
                .drawBehind { drawVinyl() },
            contentAlignment = Alignment.Center,
        ) {
            StationImage(station = station, size = size * 0.42f, shape = CircleShape)
        }
        // El brillo no gira: así el disco parece real al moverse debajo de la luz.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind { drawVinylShine() },
        )
        Box(
            modifier = Modifier
                .size(size * 0.05f)
                .clip(CircleShape)
                .background(VinylDark),
        )
    }
}

private fun DrawScope.drawVinyl() {
    val radius = size.minDimension / 2
    drawCircle(Brush.radialGradient(listOf(VinylLight, VinylDark), center = center, radius = radius), radius = radius)
    val grooveStroke = Stroke(width = 1.dp.toPx())
    var grooveRadius = radius * 0.5f
    while (grooveRadius < radius * 0.95f) {
        drawCircle(Color.White.copy(alpha = 0.07f), radius = grooveRadius, style = grooveStroke)
        grooveRadius += radius * 0.06f
    }
    drawCircle(Color.White.copy(alpha = 0.14f), radius = radius - 1.dp.toPx(), style = Stroke(width = 1.5.dp.toPx()))
}

private fun DrawScope.drawVinylShine() {
    val shine = Color.White.copy(alpha = 0.10f)
    drawCircle(
        brush = Brush.sweepGradient(
            listOf(Color.Transparent, shine, Color.Transparent, Color.Transparent, shine, Color.Transparent),
            center = center,
        ),
        radius = size.minDimension / 2,
    )
}

/** Anillos que se expanden desde el centro, como ondas de radio. Solo se dibujan si [isActive]. */
@Composable
fun PulseRings(
    isActive: Boolean,
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    ringCount: Int = 3,
) {
    if (!isActive) return
    val transition = rememberInfiniteTransition(label = "pulse")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(durationMillis = 2_600, easing = LinearEasing)),
        label = "pulse-progress",
    )
    Canvas(modifier = modifier) {
        val maxRadius = size.minDimension / 2
        repeat(ringCount) { index ->
            val ringProgress = (progress + index.toFloat() / ringCount) % 1f
            drawCircle(
                color = color.copy(alpha = (1f - ringProgress) * 0.45f),
                radius = maxRadius * (0.62f + 0.38f * ringProgress),
                style = Stroke(width = 2.dp.toPx()),
            )
        }
    }
}

/**
 * Visualizador de audio: barras simétricas que se mueven como una onda mientras suena la radio.
 * Es decorativo (no analiza la señal real) y queda en reposo cuando el audio se detiene.
 */
@Composable
fun WaveVisualizer(
    isActive: Boolean,
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    barCount: Int = 32,
) {
    val phase = if (isActive) {
        val transition = rememberInfiniteTransition(label = "wave")
        transition.animateFloat(
            initialValue = 0f,
            targetValue = (2 * PI).toFloat(),
            animationSpec = infiniteRepeatable(tween(durationMillis = 1_400, easing = LinearEasing)),
            label = "wave-phase",
        ).value
    } else {
        0f
    }

    Canvas(modifier = modifier) {
        val slot = size.width / barCount
        val barWidth = slot * 0.55f
        for (index in 0 until barCount) {
            val level = if (isActive) {
                val wave = sin(phase + index * 0.45f) * 0.6f + sin(phase * 2 + index * 0.9f) * 0.4f
                (0.18f + 0.82f * abs(wave)).coerceIn(0.12f, 1f)
            } else {
                0.12f + 0.06f * (index % 3)
            }
            val barHeight = size.height * level
            drawRoundRect(
                color = color.copy(alpha = if (isActive) 0.9f else 0.35f),
                topLeft = Offset(index * slot + (slot - barWidth) / 2, (size.height - barHeight) / 2),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(barWidth / 2),
            )
        }
    }
}
