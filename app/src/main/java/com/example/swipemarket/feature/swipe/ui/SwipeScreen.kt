package com.example.swipemarket.feature.swipe.ui

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.swipemarket.core.data.Product
import com.example.swipemarket.core.data.formattedPrice
import com.example.swipemarket.core.data.remote.ApiClient
import com.example.swipemarket.core.ui.components.AppButton
import com.example.swipemarket.core.ui.components.AppLogo
import com.example.swipemarket.ui.theme.SwipeMarketTheme
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

private const val SwipeThreshold = 300f
private const val OffscreenOffset = 1000f

/**
 * Pantalla principal de descubrimiento tipo swipe.
 *
 * Carga el catálogo real desde `GET api/v1/products` al entrar a la pantalla.
 * La cola visible vive en memoria y se consume con cada gesto o botón.
 */
@Composable
fun SwipeScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {}
) {
    val colorScheme = MaterialTheme.colorScheme
    val scope = rememberCoroutineScope()

    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val currentProduct = products.firstOrNull()

    suspend fun loadProducts() {
        isLoading = true
        errorMessage = null

        try {
            val response = ApiClient.api.getProducts()
            if (response.isSuccessful) {
                products = response.body().orEmpty()
            } else {
                val errorBody = runCatching {
                    response.errorBody()?.string()
                }.getOrNull()

                Log.e(
                    "SwipeProducts",
                    "HTTP ${response.code()} ${response.message()} en " +
                        "${ApiClient.BASE_URL}api/v1/products | errorBody=$errorBody"
                )

                errorMessage = when (response.code()) {
                    401 -> "Tu sesión expiró. Inicia sesión de nuevo."
                    else -> "No pudimos cargar los productos (código ${response.code()})."
                }
            }
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (error: Exception) {
            Log.e(
                "SwipeProducts",
                "Error de red al llamar a ${ApiClient.BASE_URL}api/v1/products",
                error
            )
            errorMessage = "No pudimos cargar los productos. Revisa tu conexión e inténtalo de nuevo."
        } finally {
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        loadProducts()
    }

    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }
    var isAnimating by remember { mutableStateOf(false) }

    fun dismissCard(targetX: Float) {
        if (isAnimating) return

        isAnimating = true
        scope.launch {
            offsetX.animateTo(
                targetValue = targetX,
                animationSpec = tween(durationMillis = 280)
            )
            products = products.drop(1)
            offsetX.snapTo(0f)
            offsetY.snapTo(0f)
            isAnimating = false
        }
    }

    fun springBackToCenter() {
        scope.launch {
            launch {
                offsetX.animateTo(
                    targetValue = 0f,
                    animationSpec = spring(
                        dampingRatio = 0.75f,
                        stiffness = Spring.StiffnessMediumLow
                    )
                )
            }
            launch {
                offsetY.animateTo(
                    targetValue = 0f,
                    animationSpec = spring(
                        dampingRatio = 0.75f,
                        stiffness = Spring.StiffnessMediumLow
                    )
                )
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .systemBarsPadding()
    ) {
        when {
            isLoading -> CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = colorScheme.primary
            )

            errorMessage != null -> SwipeErrorState(
                message = errorMessage.orEmpty(),
                onRetry = { scope.launch { loadProducts() } },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            )

            currentProduct == null -> SwipeEmptyState(
                onReset = {
                    scope.launch {
                        offsetX.snapTo(0f)
                        offsetY.snapTo(0f)
                        loadProducts()
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            )

            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    AppLogo(style = MaterialTheme.typography.headlineSmall)
                }

                Spacer(modifier = Modifier.height(16.dp))

                SwipeProductCard(
                    product = currentProduct,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .pointerInput(currentProduct.id) {
                            detectDragGestures(
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    if (!isAnimating) {
                                        scope.launch {
                                            offsetX.snapTo(offsetX.value + dragAmount.x)
                                            offsetY.snapTo(offsetY.value + dragAmount.y)
                                        }
                                    }
                                },
                                onDragEnd = {
                                    if (!isAnimating) {
                                        val currentX = offsetX.value
                                        when {
                                            currentX > SwipeThreshold -> {
                                                dismissCard(OffscreenOffset)
                                            }

                                            currentX < -SwipeThreshold -> {
                                                dismissCard(-OffscreenOffset)
                                            }

                                            else -> springBackToCenter()
                                        }
                                    }
                                },
                                onDragCancel = {
                                    if (!isAnimating) {
                                        springBackToCenter()
                                    }
                                }
                            )
                        }
                        .graphicsLayer {
                            translationX = offsetX.value
                            translationY = offsetY.value
                            rotationZ = offsetX.value / 20f
                        }
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(56.dp)) {
                    SwipeActionButton(
                        label = "Descartar",
                        icon = Icons.Filled.Close,
                        onClick = { dismissCard(-OffscreenOffset) },
                        containerColor = colorScheme.surface,
                        contentColor = colorScheme.primary,
                        border = BorderStroke(1.dp, colorScheme.outline)
                    )

                    SwipeActionButton(
                        label = "Me gusta",
                        icon = Icons.Filled.Favorite,
                        onClick = { dismissCard(OffscreenOffset) },
                        containerColor = colorScheme.secondaryContainer.copy(alpha = 0.55f),
                        contentColor = colorScheme.secondary,
                        border = BorderStroke(1.dp, colorScheme.secondary.copy(alpha = 0.60f))
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun SwipeProductCard(
    product: Product,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val cardShape = RoundedCornerShape(24.dp)

    Surface(
        modifier = modifier,
        shape = cardShape,
        color = colorScheme.surface,
        contentColor = colorScheme.onSurface,
        tonalElevation = 0.dp,
        shadowElevation = 10.dp
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Placeholder de imagen del producto.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(colorScheme.surfaceVariant)
            )

            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = product.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = product.formattedPrice,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.secondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(colorScheme.secondary, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = product.category,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun SwipeActionButton(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    border: BorderStroke? = null
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            onClick = onClick,
            modifier = Modifier.size(68.dp),
            shape = CircleShape,
            color = containerColor,
            contentColor = contentColor,
            border = border,
            tonalElevation = 0.dp,
            shadowElevation = 3.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    modifier = Modifier.size(30.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = contentColor
        )
    }
}

@Composable
private fun SwipeErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        AppButton(
            text = "Reintentar",
            onClick = onRetry
        )
    }
}

@Composable
private fun SwipeEmptyState(
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .background(colorScheme.primaryContainer, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "No hay más productos por tu zona",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Has visto todos los productos disponibles. Vuelve a buscar para descubrir más.",
            style = MaterialTheme.typography.bodyMedium,
            color = colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(28.dp))

        AppButton(
            text = "Volver a buscar",
            onClick = onReset,
            fullWidth = true
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF1F4F9, name = "Swipe")
@Composable
private fun SwipeScreenPreview() {
    SwipeMarketTheme {
        SwipeScreen()
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF1F4F9, name = "Swipe - Vacío")
@Composable
private fun SwipeEmptyStatePreview() {
    SwipeMarketTheme {
        SwipeEmptyState(
            onReset = {},
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        )
    }
}
