package com.example.swipemarket.feature.placeholder.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.swipemarket.core.ui.components.AppBottomBar

/**
 * Pantallas provisionales de las pestañas Vender, Chats y Perfil.
 *
 * Cumplen el requisito visual mientras se implementa la funcionalidad real.
 * Todas comparten el mismo [Scaffold] con la barra inferior conectada.
 */

@Composable
fun SellScreen(
    selectedTabIndex: Int = BottomTabIndex.Sell,
    onTabSelected: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    PlaceholderScreen(
        title = "Vender",
        subtitle = "Aquí podrás publicar y administrar tus productos.",
        selectedTabIndex = selectedTabIndex,
        onTabSelected = onTabSelected,
        modifier = modifier
    )
}

@Composable
fun ChatsScreen(
    selectedTabIndex: Int = BottomTabIndex.Chats,
    onTabSelected: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    PlaceholderScreen(
        title = "Chats",
        subtitle = "Tus conversaciones con vendedores y compradores aparecerán aquí.",
        selectedTabIndex = selectedTabIndex,
        onTabSelected = onTabSelected,
        modifier = modifier
    )
}

@Composable
fun ProfileScreen(
    selectedTabIndex: Int = BottomTabIndex.Profile,
    onTabSelected: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    PlaceholderScreen(
        title = "Perfil",
        subtitle = "Aquí verás tu información y la configuración de tu cuenta.",
        selectedTabIndex = selectedTabIndex,
        onTabSelected = onTabSelected,
        modifier = modifier
    )
}

@Composable
private fun PlaceholderScreen(
    title: String,
    subtitle: String,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.background)
                .padding(innerPadding)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/** Índices compartidos entre la barra inferior y las pantallas principales. */
object BottomTabIndex {
    const val Explore = 0
    const val Sell = 1
    const val Chats = 2
    const val Profile = 3
}
