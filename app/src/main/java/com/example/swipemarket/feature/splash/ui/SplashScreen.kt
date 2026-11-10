package com.example.swipemarket.feature.splash.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.swipemarket.core.ui.components.AppLogo
import com.example.swipemarket.ui.theme.SwipeMarketTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Pantalla de carga animada.
 *
 * @param animate Se desactiva únicamente en previews para mostrar el estado final.
 * @param onSplashFinished Se invoca cuando termina la espera de 2 segundos.
 */
@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    animate: Boolean = true,
    onSplashFinished: () -> Unit = {}
) {
    val colorScheme = MaterialTheme.colorScheme
    val scale = remember { Animatable(0.5f) }
    val opacity = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        if (!animate) {
            scale.snapTo(1f)
            opacity.snapTo(1f)
            return@LaunchedEffect
        }

        launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 700,
                    easing = FastOutSlowInEasing
                )
            )
        }
        launch {
            opacity.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 700)
            )
        }

        delay(2000)
        onSplashFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(240.dp)
                .graphicsLayer {
                    scaleX = scale.value
                    scaleY = scale.value
                    alpha = opacity.value
                },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = colorScheme.primaryContainer.copy(alpha = 0.60f),
                        shape = CircleShape
                    )
            )
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = CircleShape,
                        clip = false
                    )
                    .background(colorScheme.surface, CircleShape)
            )
            AppLogo(style = MaterialTheme.typography.headlineSmall)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF1F4F9, name = "Splash")
@Composable
private fun SplashScreenPreview() {
    SwipeMarketTheme {
        SplashScreen(animate = false)
    }
}
