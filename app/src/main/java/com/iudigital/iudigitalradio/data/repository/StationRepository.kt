package com.iudigital.iudigitalradio.data.repository

import com.iudigital.iudigitalradio.data.OFFICIAL_RADIO_STREAM
import com.iudigital.iudigitalradio.data.OfficialStation
import com.iudigital.iudigitalradio.data.model.RadioStation
import com.iudigital.iudigitalradio.data.model.StationCategory
import com.iudigital.iudigitalradio.data.remote.RadioBrowserApi
import com.iudigital.iudigitalradio.data.remote.RadioBrowserStationDto
import kotlinx.coroutines.CancellationException

/**
 * Única fuente de emisoras para la UI.
 * La emisora oficial es local y siempre está disponible; las externas dependen de Radio Browser,
 * por lo que una falla de la API nunca impide escuchar IU Digital Radio.
 */
class StationRepository(private val api: RadioBrowserApi = RadioBrowserApi()) {

    val officialStation: RadioStation = OfficialStation

    suspend fun getExternalStations(category: StationCategory): Result<List<RadioStation>> =
        try {
            Result.success(api.searchStations(category).toPlayableStations(category))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
}

private val SUPPORTED_CODECS = setOf("MP3", "AAC", "AAC+")

/**
 * Conserva solo emisoras que ExoPlayer puede reproducir sin configuración extra
 * (HTTPS, sin HLS, MP3/AAC) y descarta duplicados o entradas que se hagan pasar por la oficial.
 */
internal fun List<RadioBrowserStationDto>.toPlayableStations(
    requestedCategory: StationCategory,
): List<RadioStation> =
    asSequence()
        .filter { it.isPlayable() && !it.looksLikeOfficialStation() }
        .distinctBy { it.name.lowercase() }
        .map { it.toRadioStation(requestedCategory) }
        .toList()

internal fun RadioBrowserStationDto.isPlayable(): Boolean =
    stationUuid.isNotBlank() &&
        name.isNotBlank() &&
        urlResolved.startsWith("https://", ignoreCase = true) &&
        !isHls &&
        codec.uppercase() in SUPPORTED_CODECS

/** Android bloquea el tráfico HTTP sin cifrar, así que los logos se piden por HTTPS. */
internal fun String.toHttpsUrlOrNull(): String? {
    val url = trim()
    return when {
        url.startsWith("https://", ignoreCase = true) -> url
        url.startsWith("http://", ignoreCase = true) -> "https://" + url.substring("http://".length)
        else -> null
    }
}

private fun RadioBrowserStationDto.looksLikeOfficialStation(): Boolean =
    urlResolved.trimEnd('/') == OFFICIAL_RADIO_STREAM || name.contains("IU Digital", ignoreCase = true)

private fun RadioBrowserStationDto.toRadioStation(requestedCategory: StationCategory): RadioStation {
    val tagList = tags.split(',').map { it.trim() }.filter { it.isNotEmpty() }
    return RadioStation(
        id = stationUuid,
        name = name,
        streamUrl = urlResolved,
        isOfficial = false,
        country = country.ifBlank { null },
        tags = tagList.take(3),
        codec = codec.uppercase(),
        bitrateKbps = bitrate.takeIf { it > 0 },
        category = StationCategory.fromTags(tagList) ?: requestedCategory,
        faviconUrl = favicon.toHttpsUrlOrNull(),
    )
}
