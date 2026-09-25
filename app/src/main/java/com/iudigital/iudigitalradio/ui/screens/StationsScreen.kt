package com.iudigital.iudigitalradio.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.iudigital.iudigitalradio.data.model.RadioStation
import com.iudigital.iudigitalradio.data.model.StationCategory
import com.iudigital.iudigitalradio.data.repository.StationsUiState
import com.iudigital.iudigitalradio.ui.components.CategoryCarousel
import com.iudigital.iudigitalradio.ui.components.SectionHeader
import com.iudigital.iudigitalradio.ui.components.StationItem

@Composable
fun StationsScreen(
    officialStation: RadioStation,
    selectedStation: RadioStation,
    isPlaying: Boolean,
    stationsState: StationsUiState,
    selectedCategory: StationCategory,
    onCategorySelected: (StationCategory) -> Unit,
    onStationSelected: (RadioStation) -> Unit,
    onRetry: () -> Unit,
) {
    // Texto de búsqueda: estado local de la pantalla que sobrevive a la rotación.
    var query by rememberSaveable { mutableStateOf("") }
    val stationCount = (stationsState as? StationsUiState.Success)?.stations?.count { it.matches(query) }

    Column(modifier = Modifier.fillMaxSize()) {
        SearchField(
            query = query,
            onQueryChange = { query = it },
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 12.dp),
        )
        CategoryCarousel(selected = selectedCategory, onCategorySelected = onCategorySelected, compact = true)
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("stations_list"),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            if (officialStation.matches(query)) {
                item(key = "official-header") { SectionHeader(title = "Emisora oficial") }
                item(key = officialStation.id) {
                    StationItem(
                        station = officialStation,
                        isSelected = selectedStation.id == officialStation.id,
                        isPlaying = isPlaying,
                        onClick = { onStationSelected(officialStation) },
                    )
                }
            }
            item(key = "external-header") {
                SectionHeader(
                    title = "Otras emisoras · ${selectedCategory.label}" + (stationCount?.let { " ($it)" } ?: ""),
                    subtitle = "Fuente: Radio Browser. No pertenecen a IU Digital.",
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
            externalStationItems(
                state = stationsState,
                selectedStationId = selectedStation.id,
                isPlaying = isPlaying,
                onStationSelected = onStationSelected,
                onRetry = onRetry,
                filter = { it.matches(query) },
                noMatchesMessage = "Ninguna emisora coincide con \"${query.trim()}\".",
            )
        }
    }
}

@Composable
private fun SearchField(query: String, onQueryChange: (String) -> Unit, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Buscar por nombre, país o género") },
        leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Rounded.Close, contentDescription = "Borrar búsqueda")
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(50),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("search_field"),
    )
}
