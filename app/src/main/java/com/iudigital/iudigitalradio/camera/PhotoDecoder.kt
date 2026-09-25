package com.iudigital.iudigitalradio.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import kotlin.math.roundToInt

/**
 * Convierte la imagen elegida en la galería en un Bitmap pequeño para el perfil.
 * Las fotos de galería pueden tener varios megapíxeles, por eso se reducen hasta que su lado
 * mayor quede cerca de [MAX_SIZE_PX] píxeles.
 */
object PhotoDecoder {

    private const val MAX_SIZE_PX = 720

    suspend fun decode(context: Context, uri: Uri): Bitmap? = withContext(Dispatchers.IO) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                decodeWithImageDecoder(context, uri)
            } else {
                decodeLegacy(context, uri)
            }
        } catch (e: IOException) {
            null
        } catch (e: SecurityException) {
            null
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    /** ImageDecoder ya aplica la rotación EXIF de la foto. */
    @RequiresApi(Build.VERSION_CODES.P)
    private fun decodeWithImageDecoder(context: Context, uri: Uri): Bitmap {
        val source = ImageDecoder.createSource(context.contentResolver, uri)
        return ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
            decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            val scale = MAX_SIZE_PX.toFloat() / maxOf(info.size.width, info.size.height)
            if (scale < 1f) {
                decoder.setTargetSize(
                    (info.size.width * scale).roundToInt(),
                    (info.size.height * scale).roundToInt(),
                )
            }
        }
    }

    /** Android 7–8: se reduce con inSampleSize y se corrige la rotación leyendo el EXIF. */
    private fun decodeLegacy(context: Context, uri: Uri): Bitmap? {
        val resolver = context.contentResolver
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null

        var sampleSize = 1
        while (maxOf(bounds.outWidth, bounds.outHeight) / (sampleSize * 2) >= MAX_SIZE_PX) {
            sampleSize *= 2
        }
        val options = BitmapFactory.Options().apply { inSampleSize = sampleSize }
        val bitmap = resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, options) }
            ?: return null

        val degrees = resolver.openInputStream(uri)?.use { stream ->
            when (ExifInterface(stream).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                else -> 0f
            }
        } ?: 0f
        if (degrees == 0f) return bitmap
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}
