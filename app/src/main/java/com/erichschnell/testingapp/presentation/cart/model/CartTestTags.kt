package com.erichschnell.testingapp.presentation.cart.model

object CartTestTags {
    const val STATE_SUCCESS = "cart_state_success"
    const val STATE_ERROR = "cart_state_error"
    const val STATE_LOADING = "cart_state_loading"

    const val SUMMARY_CARD = "cart_summary_card"

    const val EMPTY_CART = "cart_empty"

    fun cartItem(id: String) = "cart_item_$id"

    fun addQuantity(productId: String) = "cart_quantity_add_$productId"

    fun subtractQuantity(productId: String) = "cart_quantity_subtract_$productId"

    fun removeItem(productId: String) = "cart_remove_item_$productId"

    const val ERROR_RETRY = "cart_error_retry"
}
