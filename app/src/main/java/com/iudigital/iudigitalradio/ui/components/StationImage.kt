package com.iudigital.iudigitalradio.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import com.iudigital.iudigitalradio.R
import com.iudigital.iudigitalradio.data.model.RadioStation
import com.iudigital.iudigitalradio.data.model.StationCategory

/** Por debajo de este tamaño un logo se ve borroso; en ese caso se deja la portada del tipo de radio. */
private const val MIN_LOGO_SIZE_PX = 96
private const val LOGO_REQUEST_SIZE_PX = 256

/** Marca de IU Digital Radio (audífonos + ecualizador) sobre fondo blanco. */
@Composable
fun LogoMark(modifier: Modifier = Modifier, size: Dp = 48.dp, shape: Shape = CircleShape) {
    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(Color.White),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.logo_mark),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .padding(size * 0.12f),
        )
    }
}

/**
 * Imagen de una emisora:
 * - IU Digital Radio: su logo oficial.
 * - Otras emisoras: el logo que publican en Radio Browser; mientras carga, si falla o si es muy
 *   pequeño, se muestra la portada de su tipo de radio (Rock, Jazz, Salsa…).
 */
@Composable
fun StationImage(
    station: RadioStation,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    shape: Shape = RoundedCornerShape(size * 0.28f),
) {
    if (station.isOfficial) {
        LogoMark(modifier = modifier, size = size, shape = shape)
        return
    }
    val category = station.category ?: StationCategory.POPULAR
    Box(
        modifier = modifier
            .size(size)
            .clip(shape),
        contentAlignment = Alignment.Center,
    ) {
        CategoryCover(category = category, modifier = Modifier.fillMaxSize())
        Icon(
            imageVector = category.style.icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(size * 0.46f),
        )
        station.faviconUrl?.let { url ->
            StationLogo(url = url, padding = size * 0.12f, modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
private fun StationLogo(url: String, padding: Dp, modifier: Modifier = Modifier) {
    var isUsable by remember(url) { mutableStateOf(false) }
    val alpha by animateFloatAsState(targetValue = if (isUsable) 1f else 0f, label = "logo-alpha")
    AsyncImage(
        model = ImageRequest.Builder(LocalPlatformContext.current)
            .data(url)
            .size(LOGO_REQUEST_SIZE_PX)
            .build(),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        onSuccess = { state ->
            val image = state.result.image
            isUsable = minOf(image.width, image.height) >= MIN_LOGO_SIZE_PX
        },
        onError = { isUsable = false },
        modifier = modifier
            .alpha(alpha)
            .background(Color.White)
            .padding(padding),
    )
}
