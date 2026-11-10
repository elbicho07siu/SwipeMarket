package com.example.swipemarket.core.data.remote

import com.example.swipemarket.core.data.Product
import retrofit2.Response
import retrofit2.http.GET

/**
 * Contratos REST de catálogo de SwipeMarket.
 *
 * Los endpoints de autenticación viven en [AuthApiService].
 */
interface SwipeMarketApi {

    @GET("api/v1/products")
    suspend fun getProducts(): Response<List<Product>>
}
