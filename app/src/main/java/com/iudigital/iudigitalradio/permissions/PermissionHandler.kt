package com.iudigital.iudigitalradio.permissions

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/** Utilidades de permisos en tiempo de ejecución (RF-03). */
object PermissionHandler {

    fun isGranted(context: Context, permission: String): Boolean =
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

    /**
     * Después de una negativa, indica si el sistema ya no volverá a mostrar el diálogo
     * ("No volver a preguntar"); en ese caso solo se puede conceder desde Configuración.
     */
    fun isPermanentlyDenied(context: Context, permission: String): Boolean {
        val activity = context.findActivity() ?: return false
        return !isGranted(context, permission) &&
            !ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
    }

    /** Abre la pantalla de información de la app para que el usuario conceda el permiso. */
    fun openAppSettings(context: Context) {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", context.packageName, null),
        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    private tailrec fun Context.findActivity(): Activity? = when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }
}
