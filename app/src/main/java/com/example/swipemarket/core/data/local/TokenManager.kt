package com.example.swipemarket.core.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

// Una única instancia de DataStore por proceso para el archivo de autenticación.
private val Context.authPrefsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "auth_prefs"
)

/**
 * Almacenamiento local seguro de los tokens de autenticación.
 *
 * Expone la lectura como [Flow] para que la UI o la capa de red puedan
 * reaccionar a cambios (login, logout o refresco de tokens).
 */
class TokenManager(context: Context) {

    private val dataStore = context.applicationContext.authPrefsDataStore

    val jwtToken: Flow<String?> = dataStore.data
        .catch { error ->
            if (error is IOException) {
                emit(emptyPreferences())
            } else {
                throw error
            }
        }
        .map { preferences -> preferences[JwtTokenKey] }

    val cryptoToken: Flow<String?> = dataStore.data
        .catch { error ->
            if (error is IOException) {
                emit(emptyPreferences())
            } else {
                throw error
            }
        }
        .map { preferences -> preferences[CryptoTokenKey] }

    /** Rol persistido del usuario (`Buyer` / `Seller`) para elegir el inicio. */
    val userRole: Flow<String?> = dataStore.data
        .catch { error ->
            if (error is IOException) {
                emit(emptyPreferences())
            } else {
                throw error
            }
        }
        .map { preferences -> preferences[UserRoleKey] }

    suspend fun saveTokens(jwtToken: String, cryptoToken: String) {
        dataStore.edit { preferences ->
            preferences[JwtTokenKey] = jwtToken
            preferences[CryptoTokenKey] = cryptoToken
        }
    }

    suspend fun saveJwtToken(token: String) {
        dataStore.edit { preferences ->
            preferences[JwtTokenKey] = token
        }
    }

    suspend fun saveCryptoToken(token: String) {
        dataStore.edit { preferences ->
            preferences[CryptoTokenKey] = token
        }
    }

    suspend fun saveUserRole(role: String) {
        dataStore.edit { preferences ->
            preferences[UserRoleKey] = role
        }
    }

    suspend fun getJwtToken(): String? = jwtToken.first()

    suspend fun getCryptoToken(): String? = cryptoToken.first()

    suspend fun getUserRole(): String? = userRole.first()

    suspend fun clearTokens() {
        dataStore.edit { preferences -> preferences.clear() }
    }

    private companion object {
        val JwtTokenKey = stringPreferencesKey("jwt_token")
        val CryptoTokenKey = stringPreferencesKey("crypto_token")
        val UserRoleKey = stringPreferencesKey("user_role")
    }
}
