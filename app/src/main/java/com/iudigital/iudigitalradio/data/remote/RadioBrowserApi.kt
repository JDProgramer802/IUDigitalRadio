package com.iudigital.iudigitalradio.data.remote

import com.iudigital.iudigitalradio.BuildConfig
import com.iudigital.iudigitalradio.data.model.StationCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONException
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

/**
 * Cliente mínimo de Radio Browser, un servicio gratuito que no requiere clave de API.
 * Usa HttpURLConnection + org.json (incluidos en Android) para no agregar librerías.
 */
class RadioBrowserApi(
    private val servers: List<String> = DEFAULT_SERVERS,
    private val connectTimeoutMs: Int = 8_000,
    private val readTimeoutMs: Int = 10_000,
) {

    /** Consulta emisoras verificadas y con HTTPS; se ejecuta fuera del hilo principal. */
    suspend fun searchStations(category: StationCategory, limit: Int = 60): List<RadioBrowserStationDto> =
        withContext(Dispatchers.IO) {
            val query = buildQuery(category, limit)
            var lastError: IOException? = null
            // Si un servidor falla se intenta con el siguiente.
            for (server in servers) {
                try {
                    return@withContext parseStations(get("$server/json/stations/search?$query"))
                } catch (e: IOException) {
                    lastError = e
                } catch (e: JSONException) {
                    lastError = IOException("Respuesta inválida de $server", e)
                }
            }
            throw lastError ?: IOException("No hay servidores de Radio Browser configurados")
        }

    private fun get(url: String): String {
        val connection = URL(url).openConnection() as HttpURLConnection
        return try {
            connection.connectTimeout = connectTimeoutMs
            connection.readTimeout = readTimeoutMs
            connection.setRequestProperty("User-Agent", USER_AGENT)
            connection.setRequestProperty("Accept", "application/json")
            val code = connection.responseCode
            if (code !in 200..299) throw IOException("Radio Browser respondió HTTP $code")
            connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
        } finally {
            connection.disconnect()
        }
    }

    private fun buildQuery(category: StationCategory, limit: Int): String {
        val params = buildMap {
            put("hidebroken", "true")
            put("is_https", "true")
            put("order", "clickcount")
            put("reverse", "true")
            put("limit", limit.toString())
            category.tag?.let { put("tag", it) }
            category.countryCode?.let { put("countrycode", it) }
        }
        return params.entries.joinToString("&") { (key, value) ->
            "$key=${URLEncoder.encode(value, "UTF-8")}"
        }
    }

    companion object {
        val DEFAULT_SERVERS = listOf(
            "https://all.api.radio-browser.info",
            "https://de1.api.radio-browser.info",
        )

        // Radio Browser pide identificar a la aplicación cliente.
        private val USER_AGENT = "IUDigitalRadio/${BuildConfig.VERSION_NAME}"
    }
}
