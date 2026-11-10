package com.example.swipemarket.feature.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.swipemarket.core.ui.components.AppButton
import com.example.swipemarket.core.ui.components.AppButtonVariant
import com.example.swipemarket.core.ui.components.AppLogo
import com.example.swipemarket.ui.theme.SwipeMarketTheme

/**
 * Vista 1.1 - Bienvenida.
 *
 * Punto de entrada del flujo de autenticación. Presenta la marca, el lema de la
 * app y las acciones para crear una cuenta o iniciar sesión.
 */
@Composable
fun WelcomeScreen(
    modifier: Modifier = Modifier,
    onCreateAccountClick: () -> Unit = {},
    onLoginClick: () -> Unit = {}
) {
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .systemBarsPadding()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))

        // Héroe: logotipo sobre una composición circular de la paleta.
        Box(
            modifier = Modifier.size(240.dp),
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

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Descubre lo que te gusta, deslizando.",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.weight(1.15f))

        AppButton(
            text = "Crear cuenta",
            onClick = onCreateAccountClick,
            fullWidth = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        AppButton(
            text = "Iniciar sesión",
            onClick = onLoginClick,
            variant = AppButtonVariant.Secondary,
            fullWidth = true
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF1F4F9, name = "Welcome")
@Composable
private fun WelcomeScreenPreview() {
    SwipeMarketTheme {
        WelcomeScreen()
    }
}
