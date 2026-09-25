package com.iudigital.iudigitalradio.data.model

/**
 * Emisora que la app puede reproducir.
 *
 * [isOfficial] es verdadero únicamente para IU Digital Radio; las emisoras obtenidas de
 * Radio Browser siempre se muestran como "Otras emisoras".
 */
data class RadioStation(
    val id: String,
    val name: String,
    val streamUrl: String,
    val isOfficial: Boolean = false,
    val country: String? = null,
    val tags: List<String> = emptyList(),
    val codec: String? = null,
    val bitrateKbps: Int? = null,
    /** Tipo de radio: define la portada que se muestra cuando la emisora no tiene logo. */
    val category: StationCategory? = null,
    /** Logo publicado por la emisora en Radio Browser (siempre HTTPS). */
    val faviconUrl: String? = null,
) {
    /** Texto como "AAC+ · 128 kbps", o null si la emisora no reporta esos datos. */
    val qualityLabel: String?
        get() = listOfNotNull(
            codec?.takeIf { it.isNotBlank() },
            bitrateKbps?.takeIf { it > 0 }?.let { "$it kbps" },
        ).joinToString(" · ").ifBlank { null }

    /** Descripción corta para listas: país y géneros. */
    val details: String
        get() = listOfNotNull(
            country?.takeIf { it.isNotBlank() },
            tags.take(2).joinToString(", ").ifBlank { null },
        ).joinToString(" · ")

    /** Búsqueda del catálogo por nombre, país o género, sin distinguir mayúsculas. */
    fun matches(query: String): Boolean {
        val text = query.trim()
        if (text.isEmpty()) return true
        return name.contains(text, ignoreCase = true) ||
            country?.contains(text, ignoreCase = true) == true ||
            tags.any { it.contains(text, ignoreCase = true) }
    }
}
