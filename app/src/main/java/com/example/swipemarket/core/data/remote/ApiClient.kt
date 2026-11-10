package com.example.swipemarket.core.data.remote

import android.content.Context
import com.example.swipemarket.core.data.local.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Cliente Singleton de Retrofit para SwipeMarket.
 *
 * Antes de realizar peticiones hay que inicializarlo una sola vez con
 * `ApiClient.init(context)` (por ejemplo, en la clase `Application`).
 */
object ApiClient {

    /** Localhost del equipo anfitrión visto desde el emulador de Android. */
    const val BASE_URL = "http://192.168.100.5:3000/"
    private const val AUTHORIZATION_HEADER = "Authorization"

    @Volatile
    private var tokenManagerInstance: TokenManager? = null

    /** Inicializa el almacenamiento de tokens usado por el interceptor. */
    fun init(context: Context) {
        if (tokenManagerInstance != null) return

        synchronized(this) {
            if (tokenManagerInstance == null) {
                tokenManagerInstance = TokenManager(context.applicationContext)
            }
        }
    }

    /** Instancia compartida para leer y guardar tokens desde la capa de presentación. */
    val tokenManager: TokenManager
        get() = tokenManagerInstance
            ?: error("ApiClient.init(context) debe invocarse antes de usar TokenManager.")

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val authInterceptor = Interceptor { chain ->
        val manager = tokenManager
            ?: error("ApiClient.init(context) debe invocarse antes de realizar peticiones.")

        val token = runBlocking { manager.getJwtToken() }

        val request = chain.request()
            .newBuilder()
            .apply {
                if (!token.isNullOrBlank()) {
                    header(AUTHORIZATION_HEADER, "Bearer $token")
                }
            }
            .build()

        chain.proceed(request)
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: SwipeMarketApi by lazy {
        retrofit.create(SwipeMarketApi::class.java)
    }

    val authApi: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }
}
