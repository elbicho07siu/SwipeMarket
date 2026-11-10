package com.example.swipemarket.core.data

import com.google.gson.annotations.SerializedName
import java.util.Locale

/**
 * Producto del marketplace, alineado con el JSON de la API.
 *
 * Los campos con nombres distintos en el backend se mapean mediante
 * [SerializedName] para conservar la convención de Kotlin.
 */
data class Product(
    val id: Int,
    val category: String,
    val title: String,
    val description: String,
    val price: Double,
    @SerializedName("seller_id") val sellerId: Int,
    @SerializedName("image_url") val imageUrl: String
)

/** Precio con formato monetario de dos decimales para la UI. */
val Product.formattedPrice: String
    get() = String.format(Locale.getDefault(), "$%.2f", price)
