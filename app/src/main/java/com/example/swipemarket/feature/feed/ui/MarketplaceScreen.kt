package com.example.swipemarket.feature.feed.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.swipemarket.core.data.Product
import com.example.swipemarket.core.data.mockProducts
import com.example.swipemarket.core.ui.components.AppBottomBar
import com.example.swipemarket.core.ui.components.AppLogo
import com.example.swipemarket.core.ui.components.AppTextField
import com.example.swipemarket.core.ui.components.ProductCard
import com.example.swipemarket.ui.theme.SwipeMarketTheme

private val Categories = listOf("Todos", "Ropa", "Trastes", "Comida casera")

/**
 * Feed principal del marketplace.
 *
 * Búsqueda y filtros funcionan sobre [mockProducts] mientras no exista una
 * fuente de datos real.
 */
@Composable
fun MarketplaceScreen(
    modifier: Modifier = Modifier,
    onProductClick: (Product) -> Unit = {},
    onSwipeClick: () -> Unit = {},
    selectedTabIndex: Int = 0,
    onTabSelected: (Int) -> Unit = {}
) {
    val colorScheme = MaterialTheme.colorScheme

    var query by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf(Categories.first()) }
    var favoriteIds by rememberSaveable {
        mutableStateOf(emptyList<Int>())
    }

    val filteredProducts = remember(query, selectedCategory) {
        val normalizedQuery = query.trim()
        mockProducts.filter { product ->
            val matchesCategory = selectedCategory == "Todos" || product.category == selectedCategory
            val matchesQuery = normalizedQuery.isEmpty() ||
                product.title.contains(normalizedQuery, ignoreCase = true) ||
                product.category.contains(normalizedQuery, ignoreCase = true)

            matchesCategory && matchesQuery
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = colorScheme.background,
        bottomBar = {
            AppBottomBar(
                selectedIndex = selectedTabIndex,
                onItemSelected = onTabSelected
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.background)
                .padding(innerPadding)
                .imePadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, top = 16.dp, end = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppLogo(
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.headlineSmall
                )

                Surface(
                    onClick = onSwipeClick,
                    modifier = Modifier
                        .size(48.dp)
                        .semantics { contentDescription = "Abrir modo Swipe" },
                    shape = CircleShape,
                    color = colorScheme.surface,
                    contentColor = colorScheme.primary,
                    border = BorderStroke(1.dp, colorScheme.outlineVariant),
                    tonalElevation = 0.dp,
                    shadowElevation = 1.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        SwipeCardsIcon()
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            AppTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                placeholder = "Buscar en SwipeMarket",
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null
                    )
                },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items = Categories, key = { it }) { category ->
                    CategoryChip(
                        label = category,
                        isSelected = category == selectedCategory,
                        onClick = { selectedCategory = category }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(start = 24.dp, top = 4.dp, end = 24.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (filteredProducts.isEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Text(
                            text = "No encontramos productos con esos filtros.",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    items(items = filteredProducts, key = { it.id }) { product ->
                        ProductCard(
                            product = product,
                            isFavorite = product.id in favoriteIds,
                            onFavoriteClick = {
                                favoriteIds = if (product.id in favoriteIds) {
                                    favoriteIds - product.id
                                } else {
                                    favoriteIds + product.id
                                }
                            },
                            onClick = { onProductClick(product) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    Surface(
        onClick = onClick,
        modifier = modifier.heightIn(min = 40.dp),
        shape = CircleShape,
        color = if (isSelected) colorScheme.primary else colorScheme.surface,
        contentColor = if (isSelected) colorScheme.onPrimary else colorScheme.onSurfaceVariant,
        border = if (isSelected) {
            null
        } else {
            BorderStroke(1.dp, colorScheme.outlineVariant)
        },
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
private fun SwipeCardsIcon(modifier: Modifier = Modifier) {
    val iconColor = LocalContentColor.current

    Canvas(modifier = modifier.size(22.dp)) {
        val strokeWidth = 1.6.dp.toPx()
        val cornerRadius = CornerRadius(3.dp.toPx())

        drawRoundRect(
            color = iconColor.copy(alpha = 0.45f),
            topLeft = Offset(size.width * 0.04f, size.height * 0.18f),
            size = Size(size.width * 0.60f, size.height * 0.72f),
            cornerRadius = cornerRadius,
            style = Stroke(width = strokeWidth)
        )
        drawRoundRect(
            color = iconColor,
            topLeft = Offset(size.width * 0.32f, size.height * 0.10f),
            size = Size(size.width * 0.60f, size.height * 0.72f),
            cornerRadius = cornerRadius,
            style = Stroke(width = strokeWidth)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF1F4F9, name = "Marketplace")
@Composable
private fun MarketplaceScreenPreview() {
    SwipeMarketTheme {
        MarketplaceScreen()
    }
}
