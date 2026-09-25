package com.iudigital.iudigitalradio.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Nightlife
import androidx.compose.material.icons.rounded.Piano
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.icons.rounded.Whatshot
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.iudigital.iudigitalradio.data.model.StationCategory

/** Identidad visual de cada tipo de radio: degradado propio + ícono. */
@Immutable
data class CategoryStyle(val colors: List<Color>, val icon: ImageVector)

val StationCategory.style: CategoryStyle
    get() = when (this) {
        // Amarillo, azul y rojo de la bandera de Colombia.
        StationCategory.COLOMBIA -> CategoryStyle(
            listOf(Color(0xFFF4B400), Color(0xFF003893), Color(0xFFCE1126)),
            Icons.Rounded.Place,
        )
        StationCategory.POPULAR -> CategoryStyle(listOf(Color(0xFFFF8A00), Color(0xFFE52E71)), Icons.Rounded.Whatshot)
        StationCategory.ROCK -> CategoryStyle(listOf(Color(0xFF232526), Color(0xFFB31217)), Icons.Rounded.Bolt)
        StationCategory.POP -> CategoryStyle(listOf(Color(0xFFFC466B), Color(0xFF3F5EFB)), Icons.Rounded.Mic)
        StationCategory.SALSA -> CategoryStyle(listOf(Color(0xFFFF512F), Color(0xFFF09819)), Icons.Rounded.Nightlife)
        StationCategory.JAZZ -> CategoryStyle(listOf(Color(0xFF2C1A4D), Color(0xFF8E54E9)), Icons.Rounded.Piano)
        StationCategory.CLASSICAL -> CategoryStyle(listOf(Color(0xFF134E5E), Color(0xFF3BB2B8)), Icons.Rounded.LibraryMusic)
    }

/** Portada de un tipo de radio: degradado con círculos decorativos. */
@Composable
fun CategoryCover(category: StationCategory, modifier: Modifier = Modifier) {
    val colors = category.style.colors
    Box(
        modifier = modifier.drawBehind {
            drawRect(Brush.linearGradient(colors, start = Offset.Zero, end = Offset(size.width, size.height)))
            drawCircle(Color.White.copy(alpha = 0.14f), radius = size.minDimension * 0.62f, center = Offset(size.width, 0f))
            drawCircle(Color.White.copy(alpha = 0.08f), radius = size.minDimension * 0.42f, center = Offset(0f, size.height))
        },
    )
}

/** Tarjeta seleccionable de un tipo de radio, con su portada e ícono. */
@Composable
fun CategoryCard(
    category: StationCategory,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    width: Dp = 140.dp,
    height: Dp = 96.dp,
) {
    val scale by animateFloatAsState(targetValue = if (isSelected) 1f else 0.95f, label = "category-scale")
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(22.dp),
        border = if (isSelected) BorderStroke(3.dp, MaterialTheme.colorScheme.primary) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 6.dp else 1.dp),
        modifier = modifier
            .size(width, height)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .semantics { selected = isSelected }
            .testTag("category_${category.name}"),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            CategoryCover(category = category, modifier = Modifier.fillMaxSize())
            Icon(
                imageVector = category.style.icon,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.3f),
                modifier = Modifier
                    .size(height * 0.95f)
                    .align(Alignment.TopEnd)
                    .offset(x = height * 0.2f, y = -height * 0.08f)
                    .rotate(-16f),
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.3f)))),
            )
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (isSelected) {
                    Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                }
                Text(
                    text = category.label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
            }
        }
    }
}

/** Carrusel horizontal con todos los tipos de radio. */
@Composable
fun CategoryCarousel(
    selected: StationCategory,
    onCategorySelected: (StationCategory) -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(if (compact) 8.dp else 12.dp),
    ) {
        items(items = StationCategory.entries, key = { it.name }) { category ->
            CategoryCard(
                category = category,
                isSelected = category == selected,
                onClick = { onCategorySelected(category) },
                width = if (compact) 116.dp else 140.dp,
                height = if (compact) 72.dp else 96.dp,
            )
        }
    }
}
