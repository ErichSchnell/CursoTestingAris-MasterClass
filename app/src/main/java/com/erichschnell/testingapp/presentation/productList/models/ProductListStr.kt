package com.erichschnell.testingapp.presentation.productList.models

object ProductListStr {
    const val EMPTY_CART = "No se encontraron productos"

    const val CATEGORIES = "Categorias"
    const val ALL_CATEGORIES = "Todas"
    const val ORDER_BY = "Ordenar por"
    const val ORDER_BY_PRICE_ASC = "Precio ↑"
    const val ORDER_BY_PRICE_DESC = "Precio ↓"
    const val ORDER_BY_DISCOUNT = "Descuento ↑↓"

    fun sizeProducts(size: Int) = "$size productos"
}
