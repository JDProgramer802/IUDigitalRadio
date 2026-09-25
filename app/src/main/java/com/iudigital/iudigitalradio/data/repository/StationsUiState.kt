package com.iudigital.iudigitalradio.data.repository

import com.iudigital.iudigitalradio.data.model.RadioStation

/** Estados posibles del catálogo de emisoras externas. */
sealed interface StationsUiState {
    data object Loading : StationsUiState
    data object Empty : StationsUiState
    data class Success(val stations: List<RadioStation>) : StationsUiState
    data class Error(val message: String) : StationsUiState
}
