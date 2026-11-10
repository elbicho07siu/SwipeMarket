package com.example.swipemarket.core.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.swipemarket.ui.theme.SwipeMarketTheme

/**
 * Logotipo textual de SwipeMarket.
 *
 * "Swipe" se pinta en Azul Acento y "Market" en Azul Marino. Se usan los
 * colores del tema para que el logotipo mantenga el contraste en modo oscuro
 * (en el tema claro equivalen exactamente a #1F4E8C y #16213E).
 */
@Composable
fun AppLogo(
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.headlineMedium,
    fontWeight: FontWeight = FontWeight.ExtraBold,
    swipeColor: Color = MaterialTheme.colorScheme.secondary,
    marketColor: Color = MaterialTheme.colorScheme.primary
) {
    val logo = buildAnnotatedString {
        withStyle(SpanStyle(color = swipeColor)) {
            append("Swipe")
        }
        withStyle(SpanStyle(color = marketColor)) {
            append("Market")
        }
    }

    Text(
        text = logo,
        modifier = modifier,
        style = style.copy(fontWeight = fontWeight),
        maxLines = 1
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFF1F4F9)
@Composable
private fun AppLogoPreview() {
    SwipeMarketTheme {
        AppLogo(modifier = Modifier.padding(16.dp))
    }
}
