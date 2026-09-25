package com.iudigital.iudigitalradio.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Radio
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.iudigital.iudigitalradio.audio.PlayerState
import com.iudigital.iudigitalradio.data.model.RadioStation
import com.iudigital.iudigitalradio.ui.components.InfoPill
import com.iudigital.iudigitalradio.ui.components.PlaybackBadge
import com.iudigital.iudigitalradio.ui.components.PlayerActions
import com.iudigital.iudigitalradio.ui.components.PlayerControls
import com.iudigital.iudigitalradio.ui.components.PulseRings
import com.iudigital.iudigitalradio.ui.components.VinylRecord
import com.iudigital.iudigitalradio.ui.components.WaveVisualizer
import com.iudigital.iudigitalradio.ui.components.auroraBackground
import com.iudigital.iudigitalradio.ui.components.stageColors
import com.iudigital.iudigitalradio.ui.components.statusDescription
import com.iudigital.iudigitalradio.ui.components.subtitle

private val ErrorOnDark = Color(0xFFFFB4AB)

/** Vista ampliada de la emisora activa: un escenario con el vinilo girando. */
@Composable
fun PlayerScreen(
    station: RadioStation,
    playerState: PlayerState,
    playerActions: PlayerActions,
    onOpenStations: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Card(
            shape = RoundedCornerShape(32.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .auroraBackground(station.stageColors())
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    PlaybackBadge(playerState = playerState, onDarkBackground = true)
                    station.qualityLabel?.let { InfoPill(text = it) }
                }

                Box(modifier = Modifier.size(260.dp), contentAlignment = Alignment.Center) {
                    PulseRings(isActive = playerState.isPlaying, modifier = Modifier.fillMaxSize())
                    VinylRecord(station = station, isSpinning = playerState.isPlaying, size = 212.dp)
                }

                Text(
                    text = station.name,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = station.subtitle(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.75f),
                    textAlign = TextAlign.Center,
                )

                WaveVisualizer(
                    isActive = playerState.isPlaying && !playerState.isMuted,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                )

                Text(
                    text = playerState.statusDescription,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (playerState.playerError != null) ErrorOnDark else Color.White,
                    textAlign = TextAlign.Center,
                )
                playerState.nowPlaying?.let { NowPlayingPill(title = it) }

                PlayerControls(
                    playerState = playerState,
                    actions = playerActions,
                    onDarkBackground = true,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }

        FilledTonalButton(onClick = onOpenStations) {
            Icon(Icons.Rounded.Radio, contentDescription = null, modifier = Modifier.size(18.dp))
            Text("Cambiar de emisora", modifier = Modifier.padding(start = 8.dp))
        }
    }
}

@Composable
private fun NowPlayingPill(title: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White.copy(alpha = 0.12f))
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "SONANDO AHORA",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.7f),
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
