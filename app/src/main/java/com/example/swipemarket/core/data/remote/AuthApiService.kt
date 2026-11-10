package com.example.swipemarket.core.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Contratos de autenticación de SwipeMarket.
 *
 * Las respuestas se envuelven en [Response] para que la capa de datos pueda
 * inspeccionar el código HTTP y los errores del backend.
 */
interface AuthApiService {

    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/v1/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>
}

/** Cuerpo de la petición de inicio de sesión. */
data class LoginRequest(
    val email: String,
    val password: String
)

/** Cuerpo de la petición de registro. */
data class RegisterRequest(
    val fullName: String,
    val email: String,
    val password: String,
    val zone: String,
    val role: String = "Buyer"
)

/** Respuesta de autenticación con los tokens y el usuario autenticado. */
data class AuthResponse(
    val token: String,
    val cryptoToken: String,
    val user: UserDto
)

/** Información del usuario devuelta por el backend. */
data class UserDto(
    val id: Int,
    val full_name: String,
    val email: String,
    val role: String
)
