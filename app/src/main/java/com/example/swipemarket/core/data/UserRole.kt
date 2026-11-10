package com.example.swipemarket.core.data

import java.util.Locale

/**
 * Rol del usuario dentro de SwipeMarket.
 *
 * El backend puede devolverlo en inglés (`Buyer` / `Seller`) o en español
 * (`COMPRADOR` / `VENDEDOR`), por eso la conversión normaliza el valor.
 */
enum class UserRole {
    Buyer,
    Seller;

    companion object {
        fun fromApi(raw: String?): UserRole = when (raw?.trim()?.uppercase(Locale.ROOT)) {
            "SELLER", "VENDEDOR" -> Seller
            else -> Buyer
        }
    }
}
