package com.iudigital.iudigitalradio.data.model

import com.iudigital.iudigitalradio.data.OFFICIAL_RADIO_STREAM
import com.iudigital.iudigitalradio.data.OfficialStation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RadioStationTest {

    private val station = RadioStation(
        id = "1",
        name = "Classic Rock Radio",
        streamUrl = "https://example.com/rock",
        country = "Colombia",
        tags = listOf("rock", "classic rock"),
    )

    @Test
    fun `la busqueda coincide por nombre, pais o genero sin importar mayusculas`() {
        assertTrue(station.matches("CLASSIC"))
        assertTrue(station.matches("colombia"))
        assertTrue(station.matches(" rock "))
        assertTrue(station.matches(""))
        assertFalse(station.matches("jazz"))
    }

    @Test
    fun `los detalles combinan pais y dos generos`() {
        assertEquals("Colombia · rock, classic rock", station.details)
    }

    @Test
    fun `la emisora oficial usa el stream publicado por IU Digital`() {
        assertTrue(OfficialStation.isOfficial)
        assertEquals("IU Digital Radio", OfficialStation.name)
        assertEquals(OFFICIAL_RADIO_STREAM, OfficialStation.streamUrl)
        assertTrue(OFFICIAL_RADIO_STREAM.startsWith("https://"))
    }
}
