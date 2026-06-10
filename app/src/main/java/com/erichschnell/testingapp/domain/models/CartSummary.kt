package com.erichschnell.testingapp.domain.models

data class CartSummary(
    val subtotal: Double,
    val discountsTotal: Double,
    val finalTotal: Double,
)
