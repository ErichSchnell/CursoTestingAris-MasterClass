package com.erichschnell.testingapp.cart.domain.models

data class CartSummary (
    val subtotal: Double,
    val discountsTotal: Double,
    val finalTotal: Double
)