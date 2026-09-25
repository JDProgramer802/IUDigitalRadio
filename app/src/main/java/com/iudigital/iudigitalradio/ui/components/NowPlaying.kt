package com.iudigital.iudigitalradio.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.iudigital.iudigitalradio.audio.PlayerState
import com.iudigital.iudigitalradio.data.model.RadioStation
import com.iudigital.iudigitalradio.ui.theme.LiveRed

private val ErrorOnDark = Color(0xFFFFB4AB)

/**
 * Sección central de la pantalla de Inicio: un "escenario" con el vinilo de la emisora activa,
 * su estado, un visualizador y los controles Play / Pause / Mute.
 */
@Composable
fun NowPlayingHero(
    station: RadioStation,
    playerState: PlayerState,
    actions: PlayerActions,
    onOpenPlayer: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .auroraBackground(station.stageColors())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onOpenPlayer),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(modifier = Modifier.size(124.dp), contentAlignment = Alignment.Center) {
                    PulseRings(isActive = playerState.isPlaying, modifier = Modifier.fillMaxSize())
                    VinylRecord(station = station, isSpinning = playerState.isPlaying, size = 100.dp)
                }
                Spacer(Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    PlaybackBadge(playerState = playerState, onDarkBackground = true)
                    Text(
                        text = station.name,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = station.subtitle(),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.75f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    station.qualityLabel?.let { InfoPill(text = it) }
                }
            }

            WaveVisualizer(
                isActive = playerState.isPlaying && !playerState.isMuted,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp),
            )

            PlayerStatusText(playerState = playerState)

            PlayerControls(
                playerState = playerState,
                actions = actions,
                onDarkBackground = true,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
        }
    }
}

/** Estado del reproductor y, si la emisora lo envía, la canción o programa actual. */
@Composable
fun PlayerStatusText(playerState: PlayerState, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = playerState.statusDescription,
            style = MaterialTheme.typography.bodyMedium,
            color = if (playerState.playerError != null) ErrorOnDark else Color.White,
        )
        playerState.nowPlaying?.let {
            Text(
                text = "Sonando: $it",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.75f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

/** Reproductor flotante para las pantallas que no tienen controles propios. */
@Composable
fun MiniPlayer(
    station: RadioStation,
    playerState: PlayerState,
    actions: PlayerActions,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .auroraBackground(station.stageColors())
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            VinylRecord(station = station, isSpinning = playerState.isPlaying, size = 44.dp)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = station.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (playerState.isPlaying) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(LiveRed),
                        )
                        Spacer(Modifier.width(5.dp))
                    }
                    Text(
                        text = playerState.playbackStatus.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.8f),
                    )
                }
            }
            Spacer(Modifier.width(8.dp))
            CompactPlayerControls(playerState = playerState, actions = actions)
        }
    }
}
