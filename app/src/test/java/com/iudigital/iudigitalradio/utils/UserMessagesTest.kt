package com.iudigital.iudigitalradio.utils

import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.IOException
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class UserMessagesTest {

    @Test
    fun `sin DNS se informa que no hay Internet`() {
        val error = IOException("fallo", UnknownHostException("de1.api.radio-browser.info"))

        assertEquals(UserMessages.NO_INTERNET, error.toStationsErrorMessage())
        assertEquals(UserMessages.NO_INTERNET, error.toPlaybackErrorMessage())
    }

    @Test
    fun `red inalcanzable se informa como sin Internet`() {
        val error = SocketException("Network is unreachable")

        assertEquals(UserMessages.NO_INTERNET, error.toStationsErrorMessage())
    }

    @Test
    fun `timeout de la API tiene su propio mensaje`() {
        assertEquals(UserMessages.API_TIMEOUT, SocketTimeoutException().toStationsErrorMessage())
    }

    @Test
    fun `otros errores muestran el mensaje general`() {
        assertEquals(UserMessages.API_ERROR, IOException("HTTP 503").toStationsErrorMessage())
        assertEquals(UserMessages.STREAM_ERROR, ConnectException("Connection refused").toPlaybackErrorMessage())
    }
}
