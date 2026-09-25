package com.iudigital.iudigitalradio.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

/**
 * Guarda la foto de perfil en el almacenamiento privado de la app para que sobreviva
 * al cierre del proceso. El archivo está excluido de las copias de seguridad.
 */
class ProfilePhotoStorage(context: Context) {

    private val file = File(context.filesDir, FILE_NAME)

    suspend fun load(): Bitmap? = withContext(Dispatchers.IO) {
        if (file.exists()) BitmapFactory.decodeFile(file.path) else null
    }

    suspend fun save(bitmap: Bitmap): Boolean = withContext(Dispatchers.IO) {
        try {
            file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        } catch (e: IOException) {
            false
        }
    }

    suspend fun delete(): Boolean = withContext(Dispatchers.IO) { file.delete() }

    private companion object {
        const val FILE_NAME = "profile_photo.png"
    }
}
