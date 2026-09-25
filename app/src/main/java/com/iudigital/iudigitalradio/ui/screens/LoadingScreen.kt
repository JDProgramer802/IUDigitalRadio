package com.iudigital.iudigitalradio.ui.screens

import android.os.SystemClock
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.iudigital.iudigitalradio.R
import com.iudigital.iudigitalradio.ui.components.PulseRings
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull

/** Tiempo mínimo en pantalla para que el logo alcance a verse. */
private const val MIN_DURATION_MS = 1_800L

/** Tiempo máximo de espera: si Radio Browser tarda o falla, la app continúa igual. */
private const val MAX_WAIT_MS = 4_000L

/**
 * Pantalla de carga con el logo de IU Digital Radio.
 * La barra avanza hasta el 85 % mientras se consulta el catálogo de emisoras externas y se
 * completa cuando la consulta termina (o se agota [MAX_WAIT_MS]); luego pasa al Inicio, así
 * la app no se queda bloqueada si la API externa no responde.
 */
@Composable
fun LoadingScreen(
    isContentReady: Boolean,
    statusText: String,
    onFinished: () -> Unit,
) {
    val currentOnFinished by rememberUpdatedState(onFinished)
    val contentReady by rememberUpdatedState(isContentReady)
    var visible by remember { mutableStateOf(false) }
    var progressTarget by remember { mutableFloatStateOf(0f) }

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "loading-alpha",
    )
    val progress by animateFloatAsState(
        targetValue = progressTarget,
        animationSpec = tween(durationMillis = if (progressTarget < 1f) 1_500 else 300, easing = FastOutSlowInEasing),
        label = "loading-progress",
    )
    val logoScale by rememberInfiniteTransition(label = "logo-breathing").animateFloat(
        initialValue = 1f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(tween(durationMillis = 1_200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "logo-scale",
    )

    LaunchedEffect(Unit) {
        visible = true
        progressTarget = 0.85f
        val startedAt = SystemClock.elapsedRealtime()
        withTimeoutOrNull(MAX_WAIT_MS) { snapshotFlow { contentReady }.first { it } }
        val remaining = MIN_DURATION_MS - (SystemClock.elapsedRealtime() - startedAt)
        if (remaining > 0) delay(remaining)
        progressTarget = 1f
        delay(350)
        currentOnFinished()
    }

    val scheme = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(scheme.background, scheme.primaryContainer)))
            .testTag("loading_screen"),
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .alpha(alpha)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(modifier = Modifier.size(320.dp), contentAlignment = Alignment.Center) {
                PulseRings(isActive = true, color = scheme.primary, modifier = Modifier.fillMaxSize())
                // El logo va sobre una tarjeta blanca para verse igual en tema claro y oscuro.
                Surface(
                    shape = RoundedCornerShape(44.dp),
                    color = Color.White,
                    shadowElevation = 16.dp,
                    modifier = Modifier
                        .size(190.dp)
                        .scale(logoScale),
                ) {
                    Image(
                        painter = painterResource(R.drawable.logo_iudigital_radio),
                        contentDescription = "IU Digital Radio",
                        modifier = Modifier.padding(14.dp),
                    )
                }
            }
            Text(
                text = "Tu música, tu momento",
                style = MaterialTheme.typography.titleMedium,
                color = scheme.primary,
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(start = 24.dp, end = 24.dp, bottom = 32.dp)
                .alpha(alpha),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            LinearProgressIndicator(
                progress = { progress },
                trackColor = scheme.surfaceVariant,
                modifier = Modifier
                    .width(220.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(50)),
            )
            Text(
                text = statusText,
                style = MaterialTheme.typography.bodyMedium,
                color = scheme.onSurface,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "Institución Universitaria Digital de Antioquia",
                style = MaterialTheme.typography.bodySmall,
                color = scheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}
