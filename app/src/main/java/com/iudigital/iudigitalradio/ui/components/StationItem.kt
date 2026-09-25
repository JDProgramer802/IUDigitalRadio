package com.iudigital.iudigitalradio.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.iudigital.iudigitalradio.data.model.RadioStation
import com.iudigital.iudigitalradio.ui.theme.AccentCyan
import com.iudigital.iudigitalradio.ui.theme.AccentViolet
import com.iudigital.iudigitalradio.ui.theme.BrandBlue
import com.iudigital.iudigitalradio.ui.theme.Navy800

private val SelectedBorder = Brush.linearGradient(listOf(BrandBlue, AccentCyan, AccentViolet))

/** Fila seleccionable del catálogo (RF-06). La emisora activa se resalta con borde de color y ecualizador. */
@Composable
fun StationItem(
    station: RadioStation,
    isSelected: Boolean,
    isPlaying: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scheme = MaterialTheme.colorScheme
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) scheme.primaryContainer else scheme.surface,
        ),
        border = if (isSelected) BorderStroke(2.dp, SelectedBorder) else BorderStroke(1.dp, scheme.outlineVariant),
        modifier = modifier
            .fillMaxWidth()
            .testTag("station_${station.id}")
            .semantics { selected = isSelected },
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StationImage(station = station, size = 60.dp)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text = station.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (station.isOfficial) {
                    OfficialLabel()
                } else if (station.details.isNotBlank()) {
                    Text(
                        text = station.details,
                        style = MaterialTheme.typography.bodySmall,
                        color = scheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                station.qualityLabel?.let { QualityTag(it) }
            }
            Spacer(Modifier.width(8.dp))
            if (isSelected) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    EqualizerBars(isAnimating = isPlaying)
                    Text(
                        text = if (isPlaying) "SONANDO" else "ACTIVA",
                        style = MaterialTheme.typography.labelSmall,
                        color = scheme.primary,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(scheme.primaryContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.PlayArrow,
                        contentDescription = null,
                        tint = scheme.primary,
                    )
                }
            }
        }
    }
}

@Composable
private fun QualityTag(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 8.dp, vertical = 2.dp),
    )
}

@Composable
private fun OfficialLabel() {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Brush.horizontalGradient(listOf(Navy800, BrandBlue)))
            .padding(horizontal = 8.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(
            imageVector = Icons.Rounded.Verified,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(14.dp),
        )
        Text(
            text = "Emisora oficial",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
        )
    }
}
