package com.iudigital.iudigitalradio.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.iudigital.iudigitalradio.audio.PlayerState
import com.iudigital.iudigitalradio.data.model.RadioStation
import com.iudigital.iudigitalradio.data.model.StationCategory
import com.iudigital.iudigitalradio.data.repository.StationsUiState
import com.iudigital.iudigitalradio.ui.components.AvatarWithCameraButton
import com.iudigital.iudigitalradio.ui.components.CategoryCarousel
import com.iudigital.iudigitalradio.ui.components.EmptyView
import com.iudigital.iudigitalradio.ui.components.ErrorView
import com.iudigital.iudigitalradio.ui.components.LoadingView
import com.iudigital.iudigitalradio.ui.components.NowPlayingHero
import com.iudigital.iudigitalradio.ui.components.PlayerActions
import com.iudigital.iudigitalradio.ui.components.SectionHeader
import com.iudigital.iudigitalradio.ui.components.StationItem
import java.util.Calendar

private const val HOME_PREVIEW_COUNT = 5

/**
 * Pantalla de Inicio, armada en un solo LazyColumn: saludo y perfil arriba,
 * reproductor en el centro y, abajo, las categorías y el catálogo de emisoras.
 */
@Composable
fun HomeScreen(
    userName: String,
    profileImage: Bitmap?,
    officialStation: RadioStation,
    selectedStation: RadioStation,
    playerState: PlayerState,
    playerActions: PlayerActions,
    selectedCategory: StationCategory,
    externalStations: StationsUiState,
    onStationSelected: (RadioStation) -> Unit,
    onCategorySelected: (StationCategory) -> Unit,
    onRetryStations: () -> Unit,
    onChangePhoto: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenStations: () -> Unit,
    onOpenPlayer: () -> Unit,
) {
    val itemModifier = Modifier.padding(horizontal = 16.dp)
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_list"),
        contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item(key = "greeting") {
            GreetingHeader(
                userName = userName,
                profileImage = profileImage,
                onChangePhoto = onChangePhoto,
                onOpenProfile = onOpenProfile,
                modifier = itemModifier,
            )
        }
        item(key = "player") {
            NowPlayingHero(
                station = selectedStation,
                playerState = playerState,
                actions = playerActions,
                onOpenPlayer = onOpenPlayer,
                modifier = itemModifier,
            )
        }
        item(key = "categories-header") {
            SectionHeader(
                title = "Explora por tipo de radio",
                subtitle = "Cada género tiene su propio estilo",
                modifier = itemModifier.padding(top = 6.dp),
            )
        }
        item(key = "categories") {
            CategoryCarousel(selected = selectedCategory, onCategorySelected = onCategorySelected)
        }
        item(key = "official-header") {
            SectionHeader(title = "Emisora oficial", modifier = itemModifier.padding(top = 6.dp))
        }
        item(key = officialStation.id) {
            StationItem(
                station = officialStation,
                isSelected = selectedStation.id == officialStation.id,
                isPlaying = playerState.isPlaying,
                onClick = { onStationSelected(officialStation) },
                modifier = itemModifier,
            )
        }
        item(key = "external-header") {
            SectionHeader(
                title = "Otras emisoras",
                subtitle = "${selectedCategory.label} · Fuente: Radio Browser",
                actionLabel = "Ver todas",
                onAction = onOpenStations,
                modifier = itemModifier.padding(top = 6.dp),
            )
        }
        externalStationItems(
            state = externalStations,
            selectedStationId = selectedStation.id,
            isPlaying = playerState.isPlaying,
            onStationSelected = onStationSelected,
            onRetry = onRetryStations,
            maxItems = HOME_PREVIEW_COUNT,
            itemModifier = itemModifier,
        )
    }
}

/** Contenido del catálogo externo según su estado; lo comparten Inicio y Emisoras. */
fun LazyListScope.externalStationItems(
    state: StationsUiState,
    selectedStationId: String,
    isPlaying: Boolean,
    onStationSelected: (RadioStation) -> Unit,
    onRetry: () -> Unit,
    maxItems: Int = Int.MAX_VALUE,
    itemModifier: Modifier = Modifier,
    filter: (RadioStation) -> Boolean = { true },
    noMatchesMessage: String = "Ninguna emisora coincide con la búsqueda.",
) {
    when (state) {
        StationsUiState.Loading -> item(key = "external-loading") {
            LoadingView(message = "Cargando emisoras externas…", modifier = itemModifier)
        }
        StationsUiState.Empty -> item(key = "external-empty") {
            EmptyView(message = "No se encontraron emisoras externas para este tipo de radio.", modifier = itemModifier)
        }
        is StationsUiState.Error -> item(key = "external-error") {
            ErrorView(message = state.message, onRetry = onRetry, modifier = itemModifier)
        }
        is StationsUiState.Success -> {
            val stations = state.stations.filter(filter).take(maxItems)
            if (stations.isEmpty()) {
                item(key = "external-no-matches") { EmptyView(message = noMatchesMessage, modifier = itemModifier) }
            } else {
                items(items = stations, key = { it.id }) { station ->
                    StationItem(
                        station = station,
                        isSelected = station.id == selectedStationId,
                        isPlaying = isPlaying,
                        onClick = { onStationSelected(station) },
                        modifier = itemModifier,
                    )
                }
            }
        }
    }
}

@Composable
private fun GreetingHeader(
    userName: String,
    profileImage: Bitmap?,
    onChangePhoto: () -> Unit,
    onOpenProfile: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val greeting = remember { greetingForCurrentTime() }
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        AvatarWithCameraButton(image = profileImage, size = 60.dp, onChangePhoto = onChangePhoto)
        Spacer(Modifier.width(14.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .clickable(onClick = onOpenProfile),
        ) {
            Text(
                text = greeting,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = userName,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        FilledTonalIconButton(onClick = onOpenProfile) {
            Icon(imageVector = Icons.Rounded.Person, contentDescription = "Ver mi perfil")
        }
    }
}

private fun greetingForCurrentTime(): String = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
    in 5..11 -> "Buenos días,"
    in 12..18 -> "Buenas tardes,"
    else -> "Buenas noches,"
}
