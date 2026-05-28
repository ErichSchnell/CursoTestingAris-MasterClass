package com.erichschnell.testingapp.core.mothers

import com.erichschnell.testingapp.core.builders.product
import com.erichschnell.testingapp.domain.models.Product

object ProductMother {
    
    fun bread(stock: Int = 8) = product {
        withId("id-bread")
        withName("Pan")
        withDescription("Pan de la casa")
        withPrice(10.0)
        withCategory("Panadería")
        withStock(stock)
    }

    fun milk(stock: Int = 12) = product {
        withId("id-milk")
        withName("Leche")
        withDescription("Leche entera 1L")
        withPrice(25.0)
        withCategory("Lacteos")
        withStock(stock)
    }

    fun eggs(stock: Int = 20) = product {
        withId("id-eggs")
        withName("Huevos")
        withDescription("Docena de huevos")
        withPrice(45.0)
        withCategory("Almacén")
        withStock(stock)
    }

    fun soda(stock: Int = 15) = product {
        withId("id-soda")
        withName("Gaseosa")
        withDescription("Gaseosa cola 2.25L")
        withPrice(30.0)
        withCategory("Bebidas")
        withStock(stock)
    }
}