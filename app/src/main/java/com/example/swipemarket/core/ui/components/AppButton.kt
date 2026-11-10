package com.example.swipemarket.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.swipemarket.ui.theme.SwipeMarketTheme

/** Variantes visuales disponibles para [AppButton]. */
enum class AppButtonVariant {
    Primary,
    Secondary
}

private val AppButtonShape = RoundedCornerShape(12.dp)
private val AppButtonMinHeight = 48.dp

/**
 * Botón reutilizable de SwipeMarket.
 *
 * - Bordes redondeados de 12dp.
 * - Área táctil mínima de 48dp.
 * - Variante [AppButtonVariant.Primary] (Azul Marino) y
 *   [AppButtonVariant.Secondary] (superficie con borde Azul Acento).
 * - Mientras [isLoading] es true muestra un [CircularProgressIndicator] y
 *   bloquea los clics.
 */
@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: AppButtonVariant = AppButtonVariant.Primary,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    fullWidth: Boolean = false,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    val colorScheme = MaterialTheme.colorScheme
    val isEnabled = enabled && !isLoading

    val containerColor = when (variant) {
        AppButtonVariant.Primary -> colorScheme.primary
        AppButtonVariant.Secondary -> colorScheme.surface
    }
    val contentColor = when (variant) {
        AppButtonVariant.Primary -> colorScheme.onPrimary
        AppButtonVariant.Secondary -> colorScheme.secondary
    }
    val disabledContainerColor = when (variant) {
        AppButtonVariant.Primary -> colorScheme.onSurface.copy(alpha = 0.12f)
        AppButtonVariant.Secondary -> colorScheme.surfaceVariant.copy(alpha = 0.50f)
    }
    val disabledContentColor = colorScheme.onSurface.copy(alpha = 0.38f)

    val border = when (variant) {
        AppButtonVariant.Primary -> null
        AppButtonVariant.Secondary -> BorderStroke(
            width = 1.dp,
            color = if (isEnabled) colorScheme.secondary else colorScheme.outline.copy(alpha = 0.50f)
        )
    }

    Button(
        onClick = onClick,
        modifier = modifier
            .then(if (fullWidth) Modifier.fillMaxWidth() else Modifier)
            .heightIn(min = AppButtonMinHeight),
        enabled = isEnabled,
        shape = AppButtonShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = disabledContainerColor,
            disabledContentColor = disabledContentColor
        ),
        border = border,
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp,
            focusedElevation = 0.dp,
            hoveredElevation = 0.dp,
            disabledElevation = 0.dp
        ),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = LocalContentColor.current,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
            } else if (leadingIcon != null) {
                leadingIcon()
                Spacer(modifier = Modifier.width(8.dp))
            }

            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF1F4F9)
@Composable
private fun AppButtonPreview() {
    SwipeMarketTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AppButton(
                text = "Comprar ahora",
                onClick = {},
                fullWidth = true
            )
            AppButton(
                text = "Agregar al carrito",
                onClick = {},
                variant = AppButtonVariant.Secondary,
                fullWidth = true
            )
            AppButton(
                text = "Procesando",
                onClick = {},
                isLoading = true,
                fullWidth = true
            )
            AppButton(
                text = "No disponible",
                onClick = {},
                enabled = false,
                fullWidth = true
            )
        }
    }
}
