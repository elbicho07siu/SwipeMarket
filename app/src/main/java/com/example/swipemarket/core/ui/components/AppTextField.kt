package com.example.swipemarket.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.swipemarket.ui.theme.SwipeMarketTheme

private val AppTextFieldShape = RoundedCornerShape(12.dp)

/**
 * Campo de texto reutilizable de SwipeMarket.
 *
 * Usa los colores de la marca a través del [MaterialTheme] y muestra, si existe,
 * el texto de error justo debajo del campo.
 *
 * @param errorMessage Si no es null, el campo entra en estado de error y este
 *   texto se muestra debajo en color de error.
 * @param supportingText Texto de ayuda inferior; se ignora cuando hay error.
 */
@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    errorMessage: String? = null,
    supportingText: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    val colorScheme = MaterialTheme.colorScheme
    val hasError = errorMessage != null
    val errorText = errorMessage
    val helperText = supportingText

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = MaterialTheme.typography.bodyLarge,
        label = label?.let { text -> { Text(text = text) } },
        placeholder = placeholder?.let { text -> { Text(text = text) } },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        supportingText = when {
            errorText != null -> {
                {
                    Text(
                        text = errorText,
                        color = colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            helperText != null -> {
                {
                    Text(
                        text = helperText,
                        color = colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            else -> null
        },
        isError = hasError,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        minLines = minLines,
        maxLines = maxLines,
        shape = AppTextFieldShape,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = colorScheme.onSurface,
            unfocusedTextColor = colorScheme.onSurface,
            disabledTextColor = colorScheme.onSurface.copy(alpha = 0.38f),
            errorTextColor = colorScheme.onSurface,
            focusedContainerColor = colorScheme.surface,
            unfocusedContainerColor = colorScheme.surface,
            disabledContainerColor = colorScheme.surfaceVariant.copy(alpha = 0.50f),
            errorContainerColor = colorScheme.surface,
            cursorColor = colorScheme.secondary,
            focusedBorderColor = colorScheme.secondary,
            unfocusedBorderColor = colorScheme.outline,
            disabledBorderColor = colorScheme.outline.copy(alpha = 0.50f),
            errorBorderColor = colorScheme.error,
            focusedLabelColor = colorScheme.secondary,
            unfocusedLabelColor = colorScheme.onSurfaceVariant,
            disabledLabelColor = colorScheme.onSurfaceVariant.copy(alpha = 0.50f),
            errorLabelColor = colorScheme.error,
            focusedPlaceholderColor = colorScheme.onSurfaceVariant,
            unfocusedPlaceholderColor = colorScheme.onSurfaceVariant,
            focusedLeadingIconColor = colorScheme.secondary,
            unfocusedLeadingIconColor = colorScheme.onSurfaceVariant,
            errorLeadingIconColor = colorScheme.error,
            focusedTrailingIconColor = colorScheme.secondary,
            unfocusedTrailingIconColor = colorScheme.onSurfaceVariant,
            errorTrailingIconColor = colorScheme.error
        )
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFF1F4F9)
@Composable
private fun AppTextFieldPreview() {
    SwipeMarketTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AppTextField(
                value = "",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                label = "Correo electrónico",
                placeholder = "tu@correo.com"
            )
            AppTextField(
                value = "correo-invalido",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                label = "Correo electrónico",
                errorMessage = "Ingresa un correo válido"
            )
            AppTextField(
                value = "Swipe123",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                label = "Contraseña",
                visualTransformation = PasswordVisualTransformation()
            )
        }
    }
}
