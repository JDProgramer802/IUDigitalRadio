package com.iudigital.iudigitalradio.audio

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.iudigital.iudigitalradio.data.model.RadioStation
import com.iudigital.iudigitalradio.utils.toPlaybackErrorMessage

/**
 * Envoltorio de Media3 ExoPlayer para transmisiones en vivo.
 *
 * Se crea una sola vez (en el ViewModel) y debe liberarse con [release] cuando ya no se use.
 * La UI solo lee [state] y llama a los métodos públicos; nunca toca ExoPlayer directamente.
 */
class RadioPlayer(context: Context) {

    private val exoPlayer: ExoPlayer = ExoPlayer.Builder(context.applicationContext)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                .build(),
            /* handleAudioFocus = */ true,
        )
        // Pausa automáticamente si se desconectan los audífonos.
        .setHandleAudioBecomingNoisy(true)
        .build()

    var state by mutableStateOf(PlayerState())
        private set

    private val listener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            if (playbackState == Player.STATE_READY) {
                // STATE_READY: el stream respondió y ya hay audio en el búfer.
                state = state.copy(playerError = null)
            }
            updateLoading()
        }

        override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) = updateLoading()

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            state = state.copy(isPlaying = isPlaying)
        }

        override fun onPlayerError(error: PlaybackException) {
            state = state.copy(
                isPlaying = false,
                isLoading = false,
                playerError = error.toPlaybackErrorMessage(),
            )
        }

        override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
            state = state.copy(nowPlaying = mediaMetadata.title?.toString()?.takeIf { it.isNotBlank() })
        }
    }

    init {
        exoPlayer.addListener(listener)
    }

    /**
     * Cambia la emisora activa: detiene la anterior, carga la nueva y, si [playWhenReady]
     * es verdadero, empieza a reproducirla.
     */
    fun load(station: RadioStation, playWhenReady: Boolean) {
        exoPlayer.stop()
        exoPlayer.setMediaItem(
            MediaItem.Builder()
                .setMediaId(station.id)
                .setUri(station.streamUrl)
                .build(),
        )
        state = state.copy(isPlaying = false, isLoading = false, playerError = null, nowPlaying = null)
        if (playWhenReady) play()
    }

    /** Reproduce o reanuda. Si hubo un error o la emisora aún no se preparó, se reconecta. */
    fun play() {
        if (exoPlayer.mediaItemCount == 0) return
        state = state.copy(playerError = null)
        if (exoPlayer.playbackState == Player.STATE_IDLE) {
            exoPlayer.prepare()
        }
        exoPlayer.play()
    }

    fun pause() {
        exoPlayer.pause()
    }

    fun setMuted(muted: Boolean) {
        exoPlayer.volume = if (muted) 0f else 1f
        state = state.copy(isMuted = muted)
    }

    fun release() {
        exoPlayer.removeListener(listener)
        exoPlayer.release()
    }

    private fun updateLoading() {
        state = state.copy(
            isLoading = exoPlayer.playWhenReady && exoPlayer.playbackState == Player.STATE_BUFFERING,
        )
    }
}
