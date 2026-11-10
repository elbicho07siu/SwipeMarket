package com.example.swipemarket.feature.auth.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.swipemarket.core.ui.components.AppButton
import com.example.swipemarket.core.ui.components.AppLogo
import com.example.swipemarket.ui.theme.Amber
import com.example.swipemarket.ui.theme.SwipeMarketTheme

/** Rol con el que el usuario usará la aplicación. */
enum class AccountRole {
    Buyer,
    Seller
}

/**
 * Vista 1.5 - Registro, paso 3: selección de rol.
 */
@Composable
fun RegisterStep3Screen(
    modifier: Modifier = Modifier,
    initialRole: AccountRole? = null,
    onBackClick: () -> Unit = {},
    onCreateAccountClick: (AccountRole) -> Unit = {}
) {
    val colorScheme = MaterialTheme.colorScheme

    var selectedRole by rememberSaveable { mutableStateOf(initialRole) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .systemBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
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
            text = "Paso 3 de 3: ¿Qué quieres hacer?",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Elige cómo quieres usar SwipeMarket. Podrás cambiarlo más adelante.",
            style = MaterialTheme.typography.bodyMedium,
            color = colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(28.dp))

        RoleCard(
            title = "Quiero comprar",
            subtitle = "Cliente · Encuentra productos cerca de ti.",
            selected = selectedRole == AccountRole.Buyer,
            onSelect = { selectedRole = AccountRole.Buyer }
        )

        Spacer(modifier = Modifier.height(16.dp))

        RoleCard(
            title = "Quiero vender",
            subtitle = "Vendedor · Publica y vende tus productos.",
            selected = selectedRole == AccountRole.Seller,
            onSelect = { selectedRole = AccountRole.Seller },
            warning = "Requiere validación de identidad oficial"
        )

        Spacer(modifier = Modifier.height(28.dp))

        AppButton(
            text = "Crear cuenta",
            onClick = { selectedRole?.let(onCreateAccountClick) },
            enabled = selectedRole != null,
            fullWidth = true
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun RoleCard(
    title: String,
    subtitle: String,
    selected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
    warning: String? = null
) {
    val colorScheme = MaterialTheme.colorScheme
    val cardShape = RoundedCornerShape(12.dp)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(cardShape)
            .selectable(
                selected = selected,
                role = Role.RadioButton,
                onClick = onSelect
            ),
        shape = cardShape,
        color = if (selected) {
            colorScheme.secondaryContainer.copy(alpha = 0.55f)
        } else {
            colorScheme.surface
        },
        contentColor = colorScheme.onSurface,
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) colorScheme.secondary else colorScheme.outlineVariant
        )
    ) {
        Row(
            modifier = Modifier
                .heightIn(min = 108.dp)
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant
                )

                if (warning != null) {
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = warning,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = Amber
                    )
                }
            }

            Spacer(modifier = Modifier.size(16.dp))

            SelectionIndicator(selected = selected)
        }
    }
}

@Composable
private fun SelectionIndicator(selected: Boolean, modifier: Modifier = Modifier) {
    val color = if (selected) {
        MaterialTheme.colorScheme.secondary
    } else {
        MaterialTheme.colorScheme.outline
    }

    Box(
        modifier = modifier
            .size(24.dp)
            .border(width = 2.dp, color = color, shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(color = color, shape = CircleShape)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF1F4F9, name = "Register - Paso 3")
@Composable
private fun RegisterStep3ScreenPreview() {
    SwipeMarketTheme {
        RegisterStep3Screen(initialRole = AccountRole.Buyer)
    }
}
