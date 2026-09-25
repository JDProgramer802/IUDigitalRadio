package com.iudigital.iudigitalradio.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

/** Preferencia de tema elegida en Configuración. */
enum class ThemeMode(val label: String) {
    SYSTEM("Sistema"),
    LIGHT("Claro"),
    DARK("Oscuro"),
}

private val LightColors = lightColorScheme(
    primary = BrandBlue,
    onPrimary = Color.White,
    primaryContainer = Blue50,
    onPrimaryContainer = Navy900,
    secondary = Navy800,
    onSecondary = Color.White,
    secondaryContainer = Blue100,
    onSecondaryContainer = Navy900,
    tertiary = LiveRed,
    onTertiary = Color.White,
    background = Background,
    onBackground = Navy900,
    surface = SurfaceWhite,
    onSurface = Navy900,
    surfaceVariant = SurfaceMuted,
    onSurfaceVariant = TextSecondary,
    surfaceContainerLowest = SurfaceWhite,
    surfaceContainerLow = SurfaceWhite,
    surfaceContainer = SurfaceWhite,
    surfaceContainerHigh = SurfaceMuted,
    surfaceContainerHighest = Blue50,
    outline = Outline,
    outlineVariant = OutlineSoft,
    error = ErrorRed,
    onError = Color.White,
)

private val DarkColors = darkColorScheme(
    primary = BrandBlueLight,
    onPrimary = Navy950,
    primaryContainer = Navy700,
    onPrimaryContainer = Blue50,
    secondary = Blue100,
    onSecondary = Navy900,
    secondaryContainer = Navy800,
    onSecondaryContainer = Blue50,
    tertiary = LiveRed,
    onTertiary = Color.White,
    background = DarkBackground,
    onBackground = Blue50,
    surface = DarkSurface,
    onSurface = Blue50,
    surfaceVariant = DarkSurfaceHigh,
    onSurfaceVariant = DarkTextSecondary,
    surfaceContainerLowest = DarkBackground,
    surfaceContainerLow = DarkSurface,
    surfaceContainer = DarkSurface,
    surfaceContainerHigh = DarkSurfaceHigh,
    surfaceContainerHighest = DarkSurfaceHigh,
    outline = DarkOutline,
    outlineVariant = DarkSurfaceHigh,
    error = Color(0xFFFF8A80),
    onError = Navy950,
)

private val AppShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
)

@Composable
fun IUDigitalRadioTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content: @Composable () -> Unit,
) {
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    // Íconos de las barras del sistema legibles aunque el tema de la app difiera del sistema.
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography,
        shapes = AppShapes,
        content = content,
    )
}
