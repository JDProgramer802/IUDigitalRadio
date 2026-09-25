package com.iudigital.iudigitalradio.camera

import android.Manifest
import android.content.ActivityNotFoundException
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import com.iudigital.iudigitalradio.permissions.PermissionHandler

/** Resultado del flujo "Cambiar foto". */
sealed interface CameraResult {
    data class PhotoTaken(val bitmap: Bitmap) : CameraResult
    data object Cancelled : CameraResult
    data class PermissionDenied(val permanently: Boolean) : CameraResult
    data object Unavailable : CameraResult
}

/**
 * RF-02 + RF-03: devuelve una función que inicia el flujo de la cámara.
 *
 * 1. Si falta el permiso CAMERA, lo solicita.
 * 2. Con el permiso concedido abre la cámara con [ActivityResultContracts.TakePicturePreview].
 * 3. Entrega el Bitmap (o el motivo por el que no hay foto) en [onResult].
 */
@Composable
fun rememberCameraLauncher(onResult: (CameraResult) -> Unit): () -> Unit {
    val context = LocalContext.current
    val currentOnResult by rememberUpdatedState(onResult)

    val takePictureLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview(),
    ) { bitmap: Bitmap? ->
        // Un resultado nulo significa que el usuario canceló o volvió sin tomar la foto.
        currentOnResult(if (bitmap != null) CameraResult.PhotoTaken(bitmap) else CameraResult.Cancelled)
    }

    val openCamera = {
        try {
            takePictureLauncher.launch(null)
        } catch (e: ActivityNotFoundException) {
            currentOnResult(CameraResult.Unavailable)
        } catch (e: SecurityException) {
            currentOnResult(CameraResult.PermissionDenied(permanently = false))
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            openCamera()
        } else {
            val permanently = PermissionHandler.isPermanentlyDenied(context, Manifest.permission.CAMERA)
            currentOnResult(CameraResult.PermissionDenied(permanently))
        }
    }

    return remember(context) {
        {
            if (PermissionHandler.isGranted(context, Manifest.permission.CAMERA)) {
                openCamera()
            } else {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }
}
