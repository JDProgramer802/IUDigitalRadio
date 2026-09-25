package com.iudigital.iudigitalradio.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val IdleHeights = listOf(0.45f, 0.8f, 0.6f, 0.35f, 0.7f)

/** Barras de ecualizador (como las del logo) que se animan mientras suena el audio. */
@Composable
fun EqualizerBars(
    isAnimating: Boolean,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    barCount: Int = 4,
    barWidth: Dp = 4.dp,
    height: Dp = 20.dp,
) {
    val transition = rememberInfiniteTransition(label = "equalizer")
    Row(
        modifier = modifier.height(height),
        horizontalArrangement = Arrangement.spacedBy(barWidth * 0.75f),
        verticalAlignment = Alignment.Bottom,
    ) {
        repeat(barCount) { index ->
            val fraction = if (isAnimating) {
                transition.animateFloat(
                    initialValue = 0.2f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 360 + index * 120, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse,
                    ),
                    label = "bar-$index",
                ).value
            } else {
                IdleHeights[index % IdleHeights.size]
            }
            Box(
                modifier = Modifier
                    .width(barWidth)
                    .fillMaxHeight(fraction)
                    .clip(RoundedCornerShape(50))
                    .background(color),
            )
        }
    }
}
