package com.iudigital.iudigitalradio.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

private const val DEFAULT_VIBRATION_MS = 45L

/**
 * RF-05: vibración corta al usar los controles del reproductor.
 * Usa VibratorManager en Android 12+ y Vibrator en versiones anteriores.
 */
fun performHapticFeedback(context: Context, durationMs: Long = DEFAULT_VIBRATION_MS) {
    val vibrator = context.getVibrator() ?: return
    if (!vibrator.hasVibrator()) return

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(durationMs)
    }
}

private fun Context.getVibrator(): Vibrator? =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getSystemService(VibratorManager::class.java)?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }
