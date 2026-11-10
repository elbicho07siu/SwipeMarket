package com.example.swipemarket.feature.auth.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.swipemarket.core.ui.components.AppButton
import com.example.swipemarket.core.ui.components.AppLogo
import com.example.swipemarket.core.ui.components.AppTextField
import com.example.swipemarket.ui.theme.SwipeMarketTheme
import java.text.Normalizer
import java.util.Locale

private val SimulatedZones = listOf(
    "Tuxtla Gutiérrez",
    "Chiapa de Corzo",
    "San Cristóbal",
    "Comitán",
    "Tapachula",
    "Ocosingo"
)

/**
 * Vista 1.4 - Registro, paso 2: selección de zona.
 */
@Composable
fun RegisterStep2Screen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onContinueClick: (zone: String) -> Unit = {}
) {
    val colorScheme = MaterialTheme.colorScheme
    val optionShape = RoundedCornerShape(12.dp)

    var query by rememberSaveable { mutableStateOf("") }
    var selectedZone by rememberSaveable { mutableStateOf<String?>(null) }

    val filteredZones = remember(query) {
        if (query.isBlank()) {
            SimulatedZones
        } else {
            val normalizedQuery = query.normalizeForSearch()
            SimulatedZones.filter { it.normalizeForSearch().contains(normalizedQuery) }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .systemBarsPadding()
            .imePadding()
            .padding(horizontal = 24.dp, vertical = 24.dp)
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

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "Paso 2 de 3: Tu zona",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Selecciona tu municipio. Los productos heredarán esta ubicación.",
            style = MaterialTheme.typography.bodyMedium,
            color = colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        AppTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = "Buscar municipio",
            leadingIcon = { SearchIcon() },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (filteredZones.isEmpty()) {
                item {
                    Text(
                        text = "No encontramos esa zona. Intenta con otro nombre.",
                        modifier = Modifier.padding(vertical = 24.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(items = filteredZones, key = { it }) { zone ->
                    val isSelected = zone == selectedZone
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(optionShape)
                            .background(
                                if (isSelected) {
                                    colorScheme.secondaryContainer.copy(alpha = 0.55f)
                                } else {
                                    colorScheme.surface
                                }
                            )
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) colorScheme.secondary else colorScheme.outlineVariant,
                                shape = optionShape
                            )
                            .selectable(
                                selected = isSelected,
                                role = Role.RadioButton,
                                onClick = { selectedZone = zone }
                            )
                            .heightIn(min = 56.dp)
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = null,
                            colors = RadioButtonDefaults.colors(
                                selectedColor = colorScheme.secondary,
                                unselectedColor = colorScheme.outline
                            )
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = zone,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            color = colorScheme.onSurface
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        AppButton(
            text = "Continuar",
            onClick = { selectedZone?.let(onContinueClick) },
            enabled = selectedZone != null,
            fullWidth = true
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun SearchIcon(modifier: Modifier = Modifier) {
    val iconColor = LocalContentColor.current

    Canvas(modifier = modifier.size(20.dp)) {
        val strokeWidth = 1.8.dp.toPx()
        val radius = size.minDimension * 0.30f

        drawCircle(
            color = iconColor,
            radius = radius,
            center = Offset(size.width * 0.42f, size.height * 0.42f),
            style = Stroke(width = strokeWidth)
        )
        drawLine(
            color = iconColor,
            start = Offset(size.width * 0.64f, size.height * 0.64f),
            end = Offset(size.width * 0.88f, size.height * 0.88f),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

private fun String.normalizeForSearch(): String =
    Normalizer.normalize(this, Normalizer.Form.NFD)
        .replace("\\p{M}+".toRegex(), "")
        .lowercase(Locale.ROOT)

@Preview(showBackground = true, backgroundColor = 0xFFF1F4F9, name = "Register - Paso 2")
@Composable
private fun RegisterStep2ScreenPreview() {
    SwipeMarketTheme {
        RegisterStep2Screen()
    }
}
