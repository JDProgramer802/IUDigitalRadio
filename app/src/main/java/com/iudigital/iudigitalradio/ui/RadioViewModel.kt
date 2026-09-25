package com.iudigital.iudigitalradio.ui

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.iudigital.iudigitalradio.audio.PlayerState
import com.iudigital.iudigitalradio.audio.RadioPlayer
import com.iudigital.iudigitalradio.camera.PhotoDecoder
import com.iudigital.iudigitalradio.camera.ProfilePhotoStorage
import com.iudigital.iudigitalradio.data.model.RadioStation
import com.iudigital.iudigitalradio.data.model.StationCategory
import com.iudigital.iudigitalradio.data.repository.StationRepository
import com.iudigital.iudigitalradio.data.repository.StationsUiState
import com.iudigital.iudigitalradio.utils.toStationsErrorMessage
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Estado global de la app (RF-04).
 *
 * El ViewModel sobrevive a los cambios de configuración (rotación, tema, idioma), así que aquí
 * viven los estados que no se pueden guardar con rememberSaveable: la instancia de ExoPlayer
 * y el Bitmap de la foto de perfil. Los estados se exponen con mutableStateOf o
 * mutableStateListOf para que Compose se recomponga automáticamente cuando cambian.
 */
class RadioViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = StationRepository()
    private val player = RadioPlayer(application)
    private val photoStorage = ProfilePhotoStorage(application)

    val officialStation: RadioStation = repository.officialStation

    var selectedStation by mutableStateOf(officialStation)
        private set

    /** Estado que publica RadioPlayer: isPlaying, isMuted, isLoading, playerError y nowPlaying. */
    val playerState: PlayerState
        get() = player.state

    var profileImage by mutableStateOf<Bitmap?>(null)
        private set

    /** Tipo de radio elegido en el catálogo externo. */
    var stationCategory by mutableStateOf(StationCategory.COLOMBIA)
        private set

    var externalStations by mutableStateOf<StationsUiState>(StationsUiState.Loading)
        private set

    /** Emisoras escuchadas en esta sesión, de la más reciente a la más antigua. */
    val recentStations = mutableStateListOf<RadioStation>()

    private var loadJob: Job? = null
    private var resumeOnForeground = false

    init {
        player.load(officialStation, playWhenReady = false)
        loadExternalStations()
        viewModelScope.launch { profileImage = photoStorage.load() }
    }

    // ---- Reproductor -------------------------------------------------------------------------

    fun play() {
        player.play()
        addToHistory(selectedStation)
    }

    fun pause() = player.pause()

    fun toggleMute() = player.setMuted(!playerState.isMuted)

    /**
     * RF-06: cambia la emisora activa. Si ya se estaba escuchando algo, la nueva emisora
     * empieza a sonar; si estaba en pausa, queda lista para reproducir.
     */
    fun selectStation(station: RadioStation) {
        if (station.id == selectedStation.id) return
        val keepPlaying = playerState.isPlaying || playerState.isLoading
        selectedStation = station
        player.load(station, playWhenReady = keepPlaying)
        if (keepPlaying) addToHistory(station)
    }

    /** La app no tiene servicio en segundo plano: el audio se pausa al salir y se reanuda al volver. */
    fun onAppBackgrounded() {
        if (playerState.isPlaying || playerState.isLoading) {
            resumeOnForeground = true
            player.pause()
        }
    }

    fun onAppForegrounded() {
        if (resumeOnForeground) {
            resumeOnForeground = false
            player.play()
        }
    }

    // ---- Catálogo externo --------------------------------------------------------------------

    fun selectCategory(category: StationCategory) {
        if (category == stationCategory) return
        stationCategory = category
        loadExternalStations()
    }

    fun loadExternalStations() {
        loadJob?.cancel()
        externalStations = StationsUiState.Loading
        loadJob = viewModelScope.launch {
            externalStations = repository.getExternalStations(stationCategory).fold(
                onSuccess = { stations ->
                    if (stations.isEmpty()) StationsUiState.Empty else StationsUiState.Success(stations)
                },
                onFailure = { error -> StationsUiState.Error(error.toStationsErrorMessage()) },
            )
        }
    }

    // ---- Perfil ------------------------------------------------------------------------------

    /** Asigna y guarda un Bitmap ya listo: el de la cámara o el que se decodificó de la galería. */
    fun updateProfilePhoto(bitmap: Bitmap) {
        profileImage = bitmap
        viewModelScope.launch { photoStorage.save(bitmap) }
    }

    /** Foto elegida en la galería: se decodifica fuera del hilo principal. */
    fun updateProfilePhoto(uri: Uri, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val bitmap = PhotoDecoder.decode(getApplication(), uri)
            if (bitmap != null) updateProfilePhoto(bitmap)
            onResult(bitmap != null)
        }
    }

    fun removeProfilePhoto() {
        profileImage = null
        viewModelScope.launch { photoStorage.delete() }
    }

    private fun addToHistory(station: RadioStation) {
        recentStations.removeAll { it.id == station.id }
        recentStations.add(0, station)
        if (recentStations.size > MAX_RECENT_STATIONS) {
            recentStations.removeAt(recentStations.lastIndex)
        }
    }

    override fun onCleared() {
        player.release()
    }

    private companion object {
        const val MAX_RECENT_STATIONS = 5
    }
}
