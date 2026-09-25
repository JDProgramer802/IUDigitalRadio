package com.iudigital.iudigitalradio.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.BrightnessAuto
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.DataSaverOn
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.Radio
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.iudigital.iudigitalradio.BuildConfig
import com.iudigital.iudigitalradio.data.OFFICIAL_RADIO_PAGE
import com.iudigital.iudigitalradio.data.model.RadioStation
import com.iudigital.iudigitalradio.ui.theme.AccentCyan
import com.iudigital.iudigitalradio.ui.theme.AccentViolet
import com.iudigital.iudigitalradio.ui.theme.BrandBlue
import com.iudigital.iudigitalradio.ui.theme.Navy800
import com.iudigital.iudigitalradio.ui.theme.ThemeMode

private const val RADIO_BROWSER_PAGE = "https://www.radio-browser.info"
private const val NOT_AVAILABLE = "No disponible en esta versión"

private val ThemeTile = listOf(AccentViolet, BrandBlue)
private val QualityTile = listOf(BrandBlue, AccentCyan)
private val DataTile = listOf(Color(0xFF10B981), AccentCyan)
private val NotificationsTile = listOf(Color(0xFFF59E0B), Color(0xFFEF4444))
private val InfoTile = listOf(Navy800, BrandBlue)
private val WorldTile = listOf(Color(0xFF6366F1), AccentViolet)

/**
 * Solo "Tema" es una opción funcional. Las demás se muestran deshabilitadas o como información,
 * para no presentar como funcional algo que la app no hace.
 */
@Composable
fun SettingsScreen(
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    selectedStation: RadioStation,
) {
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        SettingsGroup(title = "Apariencia") {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconTile(icon = Icons.Rounded.Palette, colors = ThemeTile)
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text(text = "Tema", style = MaterialTheme.typography.titleSmall)
                        Text(
                            text = "Elige cómo se ve la app",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(top = 14.dp),
                ) {
                    ThemeMode.entries.forEach { mode ->
                        ThemeOption(
                            mode = mode,
                            selected = mode == themeMode,
                            onClick = { onThemeModeChange(mode) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }

        SettingsGroup(title = "Reproducción") {
            SettingRow(
                icon = Icons.Rounded.GraphicEq,
                tileColors = QualityTile,
                title = "Calidad de audio",
                description = "La define cada emisora. Actual: ${selectedStation.qualityLabel ?: "no reportada"}",
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            SettingRow(
                icon = Icons.Rounded.DataSaverOn,
                tileColors = DataTile,
                title = "Ahorro de datos",
                description = NOT_AVAILABLE,
                enabled = false,
            ) {
                Switch(checked = false, onCheckedChange = null, enabled = false)
            }
        }

        SettingsGroup(title = "Notificaciones") {
            SettingRow(
                icon = Icons.Rounded.Notifications,
                tileColors = NotificationsTile,
                title = "Notificaciones",
                description = NOT_AVAILABLE,
                enabled = false,
            ) {
                Switch(checked = false, onCheckedChange = null, enabled = false)
            }
        }

        SettingsGroup(title = "Acerca de") {
            SettingRow(
                icon = Icons.Rounded.Info,
                tileColors = InfoTile,
                title = "Versión",
                description = BuildConfig.VERSION_NAME,
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            SettingRow(
                icon = Icons.Rounded.Radio,
                tileColors = QualityTile,
                title = "Emisora oficial",
                description = "IU Digital Radio · iudigital.edu.co/index.php/radio",
                onClick = { uriHandler.openUri(OFFICIAL_RADIO_PAGE) },
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            SettingRow(
                icon = Icons.Rounded.Public,
                tileColors = WorldTile,
                title = "Otras emisoras",
                description = "Catálogo comunitario Radio Browser. Esas emisoras no pertenecen a IU Digital.",
                onClick = { uriHandler.openUri(RADIO_BROWSER_PAGE) },
            )
        }
    }
}

@Composable
private fun SettingsGroup(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 6.dp),
        )
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            content()
        }
    }
}

@Composable
private fun SettingRow(
    icon: ImageVector,
    tileColors: List<Color>,
    title: String,
    description: String,
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconTile(icon = icon, colors = tileColors, modifier = Modifier.alpha(if (enabled) 1f else 0.45f))
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.titleSmall)
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        when {
            trailing != null -> trailing()
            onClick != null -> Icon(
                imageVector = Icons.AutoMirrored.Rounded.OpenInNew,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

@Composable
private fun IconTile(icon: ImageVector, colors: List<Color>, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(42.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Brush.linearGradient(colors)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
    }
}

@Composable
private fun ThemeOption(
    mode: ThemeMode,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scheme = MaterialTheme.colorScheme
    val icon = when (mode) {
        ThemeMode.SYSTEM -> Icons.Rounded.BrightnessAuto
        ThemeMode.LIGHT -> Icons.Rounded.LightMode
        ThemeMode.DARK -> Icons.Rounded.DarkMode
    }
    val shape = RoundedCornerShape(18.dp)
    Column(
        modifier = modifier
            .clip(shape)
            .border(width = if (selected) 2.dp else 1.dp, color = if (selected) scheme.primary else scheme.outlineVariant, shape = shape)
            .background(if (selected) scheme.primaryContainer else Color.Transparent)
            .selectable(selected = selected, onClick = onClick, role = Role.RadioButton)
            .padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = if (selected) scheme.primary else scheme.onSurfaceVariant)
        Text(
            text = mode.label,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) scheme.primary else scheme.onSurface,
        )
    }
}
