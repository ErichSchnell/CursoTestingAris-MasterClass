package com.erichschnell.testingapp.core.mothers

import com.erichschnell.testingapp.core.builders.cartItem

object CartItemMother {
    fun bread(quantity: Int = 2) =
        cartItem {
            withProductId(ProductMother.bread().id)
            withQuantity(quantity)
        }

    fun milk(quantity: Int = 4) =
        cartItem {
            withProductId(ProductMother.milk().id)
            withQuantity(quantity)
        }

    fun eggs(quantity: Int = 5) =
        cartItem {
            withProductId(ProductMother.eggs().id)
            withQuantity(quantity)
        }

    fun soda(quantity: Int = 6) =
        cartItem {
            withProductId(ProductMother.soda().id)
            withQuantity(quantity)
        }
}
