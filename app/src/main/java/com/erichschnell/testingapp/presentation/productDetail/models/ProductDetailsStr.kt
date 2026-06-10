package com.erichschnell.testingapp.presentation.productDetail.models

object ProductDetailsStr {
    const val ADD_PRODUCT = "Agregar al carrito"
    const val WITHOUT_STOCK = "Sin stock"
    const val WITHOUT_STOCK_AVAILABLE = "Sin stock disponible"
    const val STOCK_AVAILABLE = "Stock disponible"

    fun stockAvailable(stock: Int) = "$stock unidades"
    fun discoutPercent(percent: Int) = "$percent% OFF"
}