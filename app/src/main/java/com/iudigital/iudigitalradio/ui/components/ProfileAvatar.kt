package com.iudigital.iudigitalradio.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.iudigital.iudigitalradio.ui.theme.AccentRing
import com.iudigital.iudigitalradio.ui.theme.BrandGradient

/** Foto de perfil circular con un anillo de colores; sin foto muestra un ícono de persona. */
@Composable
fun ProfileAvatar(
    image: Bitmap?,
    size: Dp,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val ringWidth = (size * 0.045f).coerceAtLeast(2.dp)
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClickLabel = "Cambiar foto", role = Role.Button, onClick = onClick)
                } else {
                    Modifier
                },
            )
            .background(Brush.sweepGradient(AccentRing))
            .padding(ringWidth)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface)
            .padding(ringWidth * 0.8f)
            .clip(CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        if (image != null) {
            val imageBitmap = remember(image) { image.asImageBitmap() }
            Image(
                bitmap = imageBitmap,
                contentDescription = "Foto de perfil",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("profile_photo"),
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BrandGradient)
                    .testTag("profile_placeholder"),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Person,
                    contentDescription = "Sin foto de perfil",
                    tint = Color.White,
                    modifier = Modifier.size(size * 0.5f),
                )
            }
        }
    }
}

/**
 * Avatar que abre las opciones de "Cambiar foto" al tocarlo. La insignia de cámara es solo
 * visual: se puede tocar cualquier parte de la foto, así es más fácil de presionar.
 */
@Composable
fun AvatarWithCameraButton(
    image: Bitmap?,
    size: Dp,
    onChangePhoto: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.testTag("camera_button")) {
        ProfileAvatar(image = image, size = size, onClick = onChangePhoto)
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(size * 0.34f)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Rounded.CameraAlt,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(size * 0.18f),
            )
        }
    }
}
