package com.iudigital.iudigitalradio.audio

/** Estado observable del reproductor. La emisora seleccionada vive en el ViewModel. */
data class PlayerState(
    val isPlaying: Boolean = false,
    val isMuted: Boolean = false,
    val isLoading: Boolean = false,
    val playerError: String? = null,
    /** Título que envía la emisora por metadatos ICY (canción o programa), si lo hay. */
    val nowPlaying: String? = null,
)
