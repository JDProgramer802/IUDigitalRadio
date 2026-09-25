package com.iudigital.iudigitalradio.data.remote

import org.json.JSONArray

/** Campos de Radio Browser que usa la app (https://api.radio-browser.info). */
data class RadioBrowserStationDto(
    val stationUuid: String,
    val name: String,
    val urlResolved: String,
    val country: String,
    val tags: String,
    val codec: String,
    val bitrate: Int,
    val isHls: Boolean,
    val favicon: String = "",
)

internal fun parseStations(json: String): List<RadioBrowserStationDto> {
    val array = JSONArray(json)
    return List(array.length()) { index ->
        val item = array.getJSONObject(index)
        RadioBrowserStationDto(
            stationUuid = item.optString("stationuuid"),
            name = item.optString("name").trim(),
            urlResolved = item.optString("url_resolved").trim(),
            country = item.optString("country").trim(),
            tags = item.optString("tags"),
            codec = item.optString("codec").trim(),
            bitrate = item.optInt("bitrate"),
            isHls = item.optInt("hls") == 1,
            favicon = item.optString("favicon").trim(),
        )
    }
}
