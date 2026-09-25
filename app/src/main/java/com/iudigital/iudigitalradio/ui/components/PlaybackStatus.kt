package com.iudigital.iudigitalradio.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.iudigital.iudigitalradio.audio.PlayerState
import com.iudigital.iudigitalradio.ui.theme.LiveRed

/** Estado del reproductor tal como se le muestra al usuario. */
enum class PlaybackStatus(val label: String) {
    LIVE("EN VIVO"),
    CONNECTING("CONECTANDO"),
    PAUSED("EN PAUSA"),
    ERROR("SIN SEÑAL"),
}

val PlayerState.playbackStatus: PlaybackStatus
    get() = when {
        playerError != null -> PlaybackStatus.ERROR
        isLoading -> PlaybackStatus.CONNECTING
        isPlaying -> PlaybackStatus.LIVE
        else -> PlaybackStatus.PAUSED
    }

val PlayerState.statusDescription: String
    get() = when {
        playerError != null -> playerError
        isLoading -> "Conectando con la emisora…"
        isPlaying && isMuted -> "Reproduciendo en silencio"
        isPlaying -> "Reproduciendo en vivo"
        else -> "En pausa · Presiona Play para escuchar"
    }

/** Etiqueta "EN VIVO" (con punto pulsante) o el estado actual del reproductor. */
@Composable
fun PlaybackBadge(
    playerState: PlayerState,
    modifier: Modifier = Modifier,
    onDarkBackground: Boolean = false,
) {
    val status = playerState.playbackStatus
    val (container, content) = when (status) {
        PlaybackStatus.LIVE -> LiveRed to Color.White
        PlaybackStatus.ERROR -> MaterialTheme.colorScheme.error to MaterialTheme.colorScheme.onError
        else -> if (onDarkBackground) {
            Color.White.copy(alpha = 0.18f) to Color.White
        } else {
            MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
        }
    }
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(container)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        if (status == PlaybackStatus.LIVE) {
            val transition = rememberInfiniteTransition(label = "live-dot")
            val dotAlpha by transition.animateFloat(
                initialValue = 1f,
                targetValue = 0.3f,
                animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse),
                label = "live-dot-alpha",
            )
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .alpha(dotAlpha)
                    .clip(CircleShape)
                    .background(content),
            )
        }
        Text(text = status.label, style = MaterialTheme.typography.labelSmall, color = content)
    }
}
