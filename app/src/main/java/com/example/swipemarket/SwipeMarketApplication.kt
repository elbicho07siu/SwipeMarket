package com.example.swipemarket

import android.app.Application
import com.example.swipemarket.core.data.remote.ApiClient

/**
 * Punto de inicialización de la capa de red.
 *
 * Deja `ApiClient` listo con el `TokenManager` antes de que cualquier
 * pantalla realice su primera petición.
 */
class SwipeMarketApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ApiClient.init(this)
    }
}
