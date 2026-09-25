package com.iudigital.iudigitalradio.utils

import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/** Mensajes de error y de confirmación que se muestran al usuario, reunidos en un solo lugar. */
object UserMessages {
    const val NO_INTERNET = "No hay conexión a Internet."
    const val STREAM_ERROR = "No fue posible reproducir esta emisora."
    const val API_ERROR = "No fue posible cargar las emisoras externas."
    const val API_TIMEOUT = "Radio Browser tardó demasiado en responder. Intenta de nuevo."
    const val CAMERA_PERMISSION = "Necesitamos permiso para acceder a la cámara."
    const val CAMERA_CANCELLED = "No se tomó ninguna foto. Se conserva la foto anterior."
    const val CAMERA_UNAVAILABLE = "No fue posible abrir la cámara en este dispositivo."
    const val PHOTO_UPDATED = "Foto de perfil actualizada."
    const val PHOTO_REMOVED = "Se quitó la foto de perfil."
    const val GALLERY_CANCELLED = "No se seleccionó ninguna imagen. Se conserva la foto anterior."
    const val GALLERY_ERROR = "No fue posible cargar la imagen seleccionada."
}

private fun Throwable.causes(): Sequence<Throwable> = generateSequence(this) { it.cause }

/** Sin red el primer paso que falla es la resolución DNS o la ruta hacia el servidor. */
fun Throwable.isNoInternetError(): Boolean = causes().any { cause ->
    cause is UnknownHostException ||
        (cause is SocketException && cause.message.orEmpty().contains("unreachable", ignoreCase = true))
}

fun Throwable.isTimeoutError(): Boolean = causes().any { it is SocketTimeoutException }

fun Throwable.toStationsErrorMessage(): String = when {
    isNoInternetError() -> UserMessages.NO_INTERNET
    isTimeoutError() -> UserMessages.API_TIMEOUT
    else -> UserMessages.API_ERROR
}

fun Throwable.toPlaybackErrorMessage(): String =
    if (isNoInternetError()) UserMessages.NO_INTERNET else UserMessages.STREAM_ERROR
