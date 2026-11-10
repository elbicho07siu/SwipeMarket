package com.example.swipemarket.feature.auth.ui

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.swipemarket.core.data.UserRole
import com.example.swipemarket.core.data.remote.ApiClient
import com.example.swipemarket.core.data.remote.LoginRequest
import com.example.swipemarket.core.ui.components.AppButton
import com.example.swipemarket.core.ui.components.AppLogo
import com.example.swipemarket.core.ui.components.AppTextField
import com.example.swipemarket.ui.theme.SwipeMarketTheme
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

/**
 * Vista 1.2 - Inicio de sesión.
 *
 * El formulario llama directamente a [ApiClient.authApi]; al autenticarse
 * guarda los tokens en [ApiClient.tokenManager] y notifica [onLoginClick]
 * para que navegación avance al flujo principal.
 */
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onLoginClick: (role: UserRole) -> Unit = {},
    onForgotPasswordClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {}
) {
    val colorScheme = MaterialTheme.colorScheme
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun submitLogin() {
        if (isLoading) return

        val trimmedEmail = email.trim()
        if (trimmedEmail.isBlank() || password.isBlank()) {
            errorMessage = "Ingresa tu correo y contraseña."
            return
        }

        focusManager.clearFocus()
        isLoading = true
        errorMessage = null

        scope.launch {
            try {
                val response = ApiClient.authApi.login(
                    LoginRequest(email = trimmedEmail, password = password)
                )

                if (response.isSuccessful) {
                    val body = response.body()
                    val token = body?.token

                    if (body == null || token.isNullOrBlank()) {
                        errorMessage = "El servidor devolvió una respuesta inválida."
                    } else {
                        val role = UserRole.fromApi(body.user.role)

                        ApiClient.tokenManager.saveTokens(
                            jwtToken = token,
                            cryptoToken = body.cryptoToken
                        )
                        ApiClient.tokenManager.saveUserRole(role.name)
                        onLoginClick(role)
                    }
                } else {
                    val errorBody = runCatching {
                        response.errorBody()?.string()
                    }.getOrNull()

                    Log.e(
                        "Login",
                        "HTTP ${response.code()} ${response.message()} en " +
                            "${ApiClient.BASE_URL}api/v1/auth/login | errorBody=$errorBody"
                    )

                    errorMessage = when (response.code()) {
                        401 -> "Correo o contraseña incorrectos."
                        else -> "No pudimos iniciar sesión (código ${response.code()})."
                    }
                }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (error: Exception) {
                Log.e(
                    "Login",
                    "Error de red al llamar a ${ApiClient.BASE_URL}api/v1/auth/login",
                    error
                )
                errorMessage = "No se pudo conectar con el servidor. Revisa tu conexión."
            } finally {
                isLoading = false
            }
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
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            AppLogo(
                modifier = Modifier.padding(top = 8.dp),
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Bienvenido de nuevo",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Ingresa a tu cuenta para seguir descubriendo.",
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(28.dp))

            AppTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth(),
                label = "Correo electrónico",
                placeholder = "tu@correo.com",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(),
                label = "Contraseña",
                placeholder = "Ingresa tu contraseña",
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                ),
                singleLine = true,
                visualTransformation = if (isPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                trailingIcon = {
                    PasswordVisibilityToggle(
                        isVisible = isPasswordVisible,
                        onToggle = { isPasswordVisible = !isPasswordVisible }
                    )
                }
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Olvidé mi contraseña",
                modifier = Modifier
                    .align(Alignment.End)
                    .heightIn(min = 48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = onForgotPasswordClick)
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = colorScheme.secondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            AppButton(
                text = "Iniciar sesión",
                onClick = { submitLogin() },
                isLoading = isLoading,
                fullWidth = true
            )

            errorMessage?.let { message ->
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = message,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodySmall,
                    color = colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        Text(
            text = buildAnnotatedString {
                append("¿No tienes cuenta? ")
                withStyle(
                    SpanStyle(
                        color = colorScheme.secondary,
                        fontWeight = FontWeight.SemiBold
                    )
                ) {
                    append("Crear cuenta")
                }
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clip(RoundedCornerShape(12.dp))
                .clickable(role = Role.Button, onClick = onRegisterClick)
                .padding(horizontal = 12.dp, vertical = 12.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Icono de ojo dibujado con Canvas para no depender de material-icons-extended.
 */
@Composable
internal fun PasswordVisibilityToggle(
    isVisible: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val iconColor = LocalContentColor.current

    IconButton(
        onClick = onToggle,
        modifier = modifier.semantics {
            contentDescription = if (isVisible) {
                "Ocultar contraseña"
            } else {
                "Mostrar contraseña"
            }
        }
    ) {
        Canvas(modifier = Modifier.size(24.dp)) {
            val strokeWidth = 1.8.dp.toPx()
            val width = size.width
            val height = size.height

            val eyeOutline = Path().apply {
                moveTo(width * 0.06f, height * 0.50f)
                cubicTo(
                    width * 0.25f, height * 0.18f,
                    width * 0.75f, height * 0.18f,
                    width * 0.94f, height * 0.50f
                )
                cubicTo(
                    width * 0.75f, height * 0.82f,
                    width * 0.25f, height * 0.82f,
                    width * 0.06f, height * 0.50f
                )
                close()
            }

            drawPath(
                path = eyeOutline,
                color = iconColor,
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            drawCircle(
                color = iconColor,
                radius = width * 0.11f,
                center = center
            )

            if (!isVisible) {
                drawLine(
                    color = iconColor,
                    start = Offset(width * 0.16f, height * 0.86f),
                    end = Offset(width * 0.84f, height * 0.14f),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF1F4F9, name = "Login")
@Composable
private fun LoginScreenPreview() {
    SwipeMarketTheme {
        LoginScreen()
    }
}
