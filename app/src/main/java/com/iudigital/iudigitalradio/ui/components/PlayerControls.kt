package com.iudigital.iudigitalradio.ui.components

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeOff
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.iudigital.iudigitalradio.audio.PlayerState
import com.iudigital.iudigitalradio.ui.theme.Navy900
import com.iudigital.iudigitalradio.utils.performHapticFeedback

/** Acciones del reproductor agrupadas para no repetir tres lambdas en cada pantalla. */
@Immutable
data class PlayerActions(
    val onPlay: () -> Unit,
    val onPause: () -> Unit,
    val onToggleMute: () -> Unit,
)

/** Envuelve una acción para que primero vibre (RF-05). */
private fun withHaptics(context: Context, action: () -> Unit): () -> Unit = {
    performHapticFeedback(context)
    action()
}

/**
 * Controles principales: Mute · Play · Pause. Play es el botón grande del centro.
 * Cada pulsación produce una vibración corta.
 */
@Composable
fun PlayerControls(
    playerState: PlayerState,
    actions: PlayerActions,
    modifier: Modifier = Modifier,
    onDarkBackground: Boolean = false,
) {
    val context = LocalContext.current
    val labelColor = if (onDarkBackground) Color.White.copy(alpha = 0.85f) else MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        ControlButton(
            icon = if (playerState.isMuted) Icons.AutoMirrored.Rounded.VolumeOff else Icons.AutoMirrored.Rounded.VolumeUp,
            label = if (playerState.isMuted) "Silenciado" else "Mute",
            contentDescription = if (playerState.isMuted) "Quitar silencio" else "Silenciar",
            onClick = withHaptics(context, actions.onToggleMute),
            enabled = true,
            size = 56.dp,
            colors = secondaryColors(onDarkBackground, highlighted = playerState.isMuted),
            labelColor = labelColor,
            testTag = "mute_button",
        )
        ControlButton(
            icon = Icons.Rounded.PlayArrow,
            label = "Play",
            contentDescription = "Reproducir",
            onClick = withHaptics(context, actions.onPlay),
            enabled = !playerState.isPlaying && !playerState.isLoading,
            size = 76.dp,
            colors = primaryColors(onDarkBackground),
            labelColor = labelColor,
            isLoading = playerState.isLoading,
            testTag = "play_button",
        )
        ControlButton(
            icon = Icons.Rounded.Pause,
            label = "Pause",
            contentDescription = "Pausar",
            onClick = withHaptics(context, actions.onPause),
            enabled = playerState.isPlaying || playerState.isLoading,
            size = 56.dp,
            colors = secondaryColors(onDarkBackground, highlighted = false),
            labelColor = labelColor,
            testTag = "pause_button",
        )
    }
}

/** Versión compacta para el mini reproductor: Play/Pause en un solo botón y Mute. */
@Composable
fun CompactPlayerControls(
    playerState: PlayerState,
    actions: PlayerActions,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val isActive = playerState.isPlaying || playerState.isLoading
    val colors = IconButtonDefaults.filledIconButtonColors(
        containerColor = Color.White.copy(alpha = 0.16f),
        contentColor = Color.White,
    )
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        FilledIconButton(
            onClick = withHaptics(context, actions.onToggleMute),
            colors = colors,
            modifier = Modifier
                .size(40.dp)
                .testTag("mini_mute"),
        ) {
            Icon(
                imageVector = if (playerState.isMuted) Icons.AutoMirrored.Rounded.VolumeOff else Icons.AutoMirrored.Rounded.VolumeUp,
                contentDescription = if (playerState.isMuted) "Quitar silencio" else "Silenciar",
                modifier = Modifier.size(20.dp),
            )
        }
        FilledIconButton(
            onClick = withHaptics(context, if (isActive) actions.onPause else actions.onPlay),
            colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color.White, contentColor = Navy900),
            modifier = Modifier
                .size(40.dp)
                .testTag("mini_play_pause"),
        ) {
            Icon(
                imageVector = if (isActive) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                contentDescription = if (isActive) "Pausar" else "Reproducir",
                modifier = Modifier.size(22.dp),
            )
        }
    }
}

@Composable
private fun ControlButton(
    icon: ImageVector,
    label: String,
    contentDescription: String,
    onClick: () -> Unit,
    enabled: Boolean,
    size: Dp,
    colors: IconButtonColors,
    labelColor: Color,
    testTag: String,
    isLoading: Boolean = false,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        FilledIconButton(
            onClick = onClick,
            enabled = enabled,
            colors = colors,
            modifier = Modifier
                .size(size)
                .testTag(testTag),
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(size * 0.42f),
                    strokeWidth = 3.dp,
                    color = LocalContentColor.current,
                )
            } else {
                Icon(imageVector = icon, contentDescription = contentDescription, modifier = Modifier.size(size * 0.5f))
            }
        }
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = labelColor)
    }
}

@Composable
private fun primaryColors(onDarkBackground: Boolean): IconButtonColors {
    val scheme = MaterialTheme.colorScheme
    return if (onDarkBackground) {
        IconButtonDefaults.filledIconButtonColors(
            containerColor = Color.White,
            contentColor = Navy900,
            disabledContainerColor = Color.White.copy(alpha = 0.35f),
            disabledContentColor = Color.White,
        )
    } else {
        IconButtonDefaults.filledIconButtonColors(
            containerColor = scheme.primary,
            contentColor = scheme.onPrimary,
            disabledContainerColor = scheme.primary.copy(alpha = 0.3f),
            disabledContentColor = scheme.onPrimary,
        )
    }
}

@Composable
private fun secondaryColors(onDarkBackground: Boolean, highlighted: Boolean): IconButtonColors {
    val scheme = MaterialTheme.colorScheme
    return when {
        onDarkBackground && highlighted -> IconButtonDefaults.filledIconButtonColors(
            containerColor = scheme.tertiary,
            contentColor = Color.White,
        )
        onDarkBackground -> IconButtonDefaults.filledIconButtonColors(
            containerColor = Color.White.copy(alpha = 0.16f),
            contentColor = Color.White,
            disabledContainerColor = Color.White.copy(alpha = 0.08f),
            disabledContentColor = Color.White.copy(alpha = 0.4f),
        )
        highlighted -> IconButtonDefaults.filledIconButtonColors(
            containerColor = scheme.tertiary,
            contentColor = scheme.onTertiary,
        )
        else -> IconButtonDefaults.filledIconButtonColors(
            containerColor = scheme.primaryContainer,
            contentColor = scheme.onPrimaryContainer,
            disabledContainerColor = scheme.surfaceVariant,
            disabledContentColor = scheme.onSurfaceVariant.copy(alpha = 0.5f),
        )
    }
}
