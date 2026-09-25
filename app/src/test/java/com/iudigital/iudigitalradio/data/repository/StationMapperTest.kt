package com.iudigital.iudigitalradio.data.repository

import com.iudigital.iudigitalradio.data.OFFICIAL_RADIO_STREAM
import com.iudigital.iudigitalradio.data.model.StationCategory
import com.iudigital.iudigitalradio.data.remote.RadioBrowserStationDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class StationMapperTest {

    private fun dto(
        uuid: String = "uuid-1",
        name: String = "Jazz Radio",
        url: String = "https://example.com/stream.mp3",
        codec: String = "MP3",
        bitrate: Int = 128,
        hls: Boolean = false,
        tags: String = "jazz, smooth jazz,lounge,  chill",
        country: String = "France",
        favicon: String = "",
    ) = RadioBrowserStationDto(
        stationUuid = uuid,
        name = name,
        urlResolved = url,
        country = country,
        tags = tags,
        codec = codec,
        bitrate = bitrate,
        isHls = hls,
        favicon = favicon,
    )

    @Test
    fun `mapea una emisora valida como externa`() {
        val station = listOf(dto()).toPlayableStations(StationCategory.COLOMBIA).single()

        assertEquals("uuid-1", station.id)
        assertEquals("Jazz Radio", station.name)
        assertFalse(station.isOfficial)
        assertEquals(listOf("jazz", "smooth jazz", "lounge"), station.tags)
        assertEquals("MP3 · 128 kbps", station.qualityLabel)
    }

    @Test
    fun `descarta streams sin https, HLS o con codec no soportado`() {
        val stations = listOf(
            dto(uuid = "http", url = "http://example.com/stream"),
            dto(uuid = "hls", hls = true),
            dto(uuid = "flac", codec = "FLAC"),
            dto(uuid = "ok", name = "Rock FM", codec = "aac+"),
        ).toPlayableStations(StationCategory.COLOMBIA)

        assertEquals(listOf("ok"), stations.map { it.id })
        assertEquals("AAC+", stations.single().codec)
    }

    @Test
    fun `descarta nombres vacios y duplicados`() {
        val stations = listOf(
            dto(uuid = "a", name = "Salsa Radio"),
            dto(uuid = "b", name = "salsa radio"),
            dto(uuid = "c", name = ""),
        ).toPlayableStations(StationCategory.COLOMBIA)

        assertEquals(listOf("a"), stations.map { it.id })
    }

    @Test
    fun `ninguna emisora externa se presenta como la oficial`() {
        val stations = listOf(
            dto(uuid = "copy", name = "IU Digital Radio"),
            dto(uuid = "same-url", name = "Otra", url = OFFICIAL_RADIO_STREAM),
        ).toPlayableStations(StationCategory.COLOMBIA)

        assertTrue(stations.isEmpty())
    }

    @Test
    fun `bitrate cero se reporta como desconocido`() {
        val station = listOf(dto(bitrate = 0)).toPlayableStations(StationCategory.COLOMBIA).single()

        assertNull(station.bitrateKbps)
        assertEquals("MP3", station.qualityLabel)
    }

    @Test
    fun `el tipo de radio se deduce de las etiquetas o del filtro consultado`() {
        val stations = listOf(
            dto(uuid = "jazz", name = "Jazz FM", tags = "smooth jazz"),
            dto(uuid = "news", name = "Noticias", tags = "news,talk"),
        ).toPlayableStations(StationCategory.COLOMBIA)

        assertEquals(StationCategory.JAZZ, stations.first { it.id == "jazz" }.category)
        assertEquals(StationCategory.COLOMBIA, stations.first { it.id == "news" }.category)
    }

    @Test
    fun `los logos se piden siempre por https`() {
        val stations = listOf(
            dto(uuid = "http", name = "A", favicon = "http://example.com/logo.png"),
            dto(uuid = "https", name = "B", favicon = "https://example.com/logo.png"),
            dto(uuid = "none", name = "C", favicon = ""),
        ).toPlayableStations(StationCategory.POPULAR)

        assertEquals("https://example.com/logo.png", stations.first { it.id == "http" }.faviconUrl)
        assertEquals("https://example.com/logo.png", stations.first { it.id == "https" }.faviconUrl)
        assertNull(stations.first { it.id == "none" }.faviconUrl)
    }
}
