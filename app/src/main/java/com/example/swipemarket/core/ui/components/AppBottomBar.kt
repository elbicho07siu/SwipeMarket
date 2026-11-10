package com.example.swipemarket.core.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.swipemarket.ui.theme.AccentBlueContainer
import com.example.swipemarket.ui.theme.AppSurface
import com.example.swipemarket.ui.theme.MutedText
import com.example.swipemarket.ui.theme.NavyBlue

/**
 * Menú inferior principal del marketplace.
 *
 * [selectedIndex] lo controla el NavHost según la ruta actual y
 * [onItemSelected] expone el índice tocado para navegar a la pestaña.
 */
@Composable
fun AppBottomBar(
    selectedIndex: Int = 0,
    onItemSelected: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val itemColors = NavigationBarItemDefaults.colors(
        selectedIconColor = NavyBlue,
        selectedTextColor = NavyBlue,
        indicatorColor = AccentBlueContainer,
        unselectedIconColor = MutedText,
        unselectedTextColor = MutedText
    )

    NavigationBar(
        modifier = modifier,
        containerColor = AppSurface,
        contentColor = NavyBlue
    ) {
        NavigationBarItem(
            selected = selectedIndex == 0,
            onClick = { onItemSelected(0) },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = "Explorar"
                )
            },
            label = { Text(text = "Explorar") },
            colors = itemColors
        )

        NavigationBarItem(
            selected = selectedIndex == 1,
            onClick = { onItemSelected(1) },
            icon = { SellIcon() },
            label = { Text(text = "Vender") },
            colors = itemColors
        )

        NavigationBarItem(
            selected = selectedIndex == 2,
            onClick = { onItemSelected(2) },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Email,
                    contentDescription = "Chats"
                )
            },
            label = { Text(text = "Chats") },
            colors = itemColors
        )

        NavigationBarItem(
            selected = selectedIndex == 3,
            onClick = { onItemSelected(3) },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Perfil"
                )
            },
            label = { Text(text = "Perfil") },
            colors = itemColors
        )
    }
}

/** Ícono de "agregar publicación": círculo con signo más. */
@Composable
private fun SellIcon(modifier: Modifier = Modifier) {
    val iconColor = LocalContentColor.current

    Canvas(modifier = modifier.size(24.dp)) {
        val strokeWidth = 1.8.dp.toPx()
        val radius = size.minDimension / 2f - strokeWidth
        val plusRadius = size.minDimension * 0.18f

        drawCircle(
            color = iconColor,
            radius = radius,
            style = Stroke(width = strokeWidth)
        )
        drawLine(
            color = iconColor,
            start = Offset(center.x - plusRadius, center.y),
            end = Offset(center.x + plusRadius, center.y),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = iconColor,
            start = Offset(center.x, center.y - plusRadius),
            end = Offset(center.x, center.y + plusRadius),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}
