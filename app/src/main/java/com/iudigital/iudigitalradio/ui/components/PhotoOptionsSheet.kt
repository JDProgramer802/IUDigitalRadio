package com.iudigital.iudigitalradio.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.iudigital.iudigitalradio.ui.theme.AccentCyan
import com.iudigital.iudigitalradio.ui.theme.AccentViolet
import com.iudigital.iudigitalradio.ui.theme.BrandBlue
import kotlinx.coroutines.launch

/** Opciones para cambiar la foto de perfil: cámara, galería o quitarla. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoOptionsSheet(
    hasPhoto: Boolean,
    onTakePhoto: () -> Unit,
    onPickFromGallery: () -> Unit,
    onRemovePhoto: () -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    // Se cierra la hoja con su animación y luego se ejecuta la opción elegida.
    val choose: (() -> Unit) -> Unit = { action ->
        scope.launch { sheetState.hide() }.invokeOnCompletion {
            onDismiss()
            action()
        }
    }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(text = "Foto de perfil", style = MaterialTheme.typography.titleLarge)
            Text(
                text = "Toma una foto nueva o elige una de tu galería.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 6.dp),
            )
            PhotoOption(
                icon = Icons.Rounded.CameraAlt,
                title = "Tomar foto",
                description = "Usa la cámara del dispositivo",
                colors = listOf(BrandBlue, AccentCyan),
                onClick = { choose(onTakePhoto) },
                modifier = Modifier.testTag("option_camera"),
            )
            PhotoOption(
                icon = Icons.Rounded.PhotoLibrary,
                title = "Elegir de la galería",
                description = "Selecciona una imagen guardada",
                colors = listOf(AccentViolet, BrandBlue),
                onClick = { choose(onPickFromGallery) },
                modifier = Modifier.testTag("option_gallery"),
            )
            if (hasPhoto) {
                PhotoOption(
                    icon = Icons.Rounded.DeleteOutline,
                    title = "Quitar foto",
                    description = "Volver al ícono predeterminado",
                    colors = listOf(Color(0xFFEF4444), Color(0xFFF97316)),
                    onClick = { choose(onRemovePhoto) },
                    modifier = Modifier.testTag("option_remove"),
                )
            }
        }
    }
}

@Composable
private fun PhotoOption(
    icon: ImageVector,
    title: String,
    description: String,
    colors: List<Color>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Brush.linearGradient(colors)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = Color.White)
        }
        Spacer(Modifier.width(14.dp))
        Column {
            Text(text = title, style = MaterialTheme.typography.titleSmall)
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
