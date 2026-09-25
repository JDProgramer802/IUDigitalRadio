package com.iudigital.iudigitalradio.data.model

/**
 * Tipos de radio del catálogo externo. Cada uno se traduce en una consulta a Radio Browser
 * y tiene su propia portada en la interfaz.
 */
enum class StationCategory(
    val label: String,
    val tag: String? = null,
    val countryCode: String? = null,
) {
    COLOMBIA(label = "Colombia", countryCode = "CO"),
    POPULAR(label = "Populares"),
    ROCK(label = "Rock", tag = "rock"),
    POP(label = "Pop", tag = "pop"),
    SALSA(label = "Salsa", tag = "salsa"),
    JAZZ(label = "Jazz", tag = "jazz"),
    CLASSICAL(label = "Clásica", tag = "classical");

    companion object {
        /** Deduce el género de una emisora a partir de sus etiquetas de Radio Browser. */
        fun fromTags(tags: List<String>): StationCategory? {
            val normalized = tags.map { it.lowercase() }
            return entries.firstOrNull { category ->
                val tag = category.tag ?: return@firstOrNull false
                normalized.any { it.contains(tag) }
            }
        }
    }
}
