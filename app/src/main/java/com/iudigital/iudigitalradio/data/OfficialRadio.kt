package com.iudigital.iudigitalradio.data

import com.iudigital.iudigitalradio.data.model.RadioStation

/** Página oficial de la emisora. */
const val OFFICIAL_RADIO_PAGE = "https://www.iudigital.edu.co/index.php/radio"

/**
 * Stream oficial de IU Digital Radio.
 *
 * Cómo se obtuvo (verificado el 21/09/2026):
 * 1. [OFFICIAL_RADIO_PAGE] embebe el reproductor titulado "Radio IU Digital":
 *    https://streamingcwsradio30.com/cp/widgets/player/single/?p=8316
 * 2. Ese reproductor carga el audio desde esta URL.
 * 3. El servidor responde con `icy-name: IU Digital Radio`, `content-type: audio/aacp`, 128 kbps.
 *
 * Si la institución cambia de proveedor de streaming, solo hay que actualizar esta constante.
 */
const val OFFICIAL_RADIO_STREAM = "https://streamingcwsradio30.com:8316/stream"

val OfficialStation = RadioStation(
    id = "iudigital-oficial",
    name = "IU Digital Radio",
    streamUrl = OFFICIAL_RADIO_STREAM,
    isOfficial = true,
    country = "Colombia",
    codec = "AAC+",
    bitrateKbps = 128,
)
