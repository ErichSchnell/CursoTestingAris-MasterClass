package com.erichschnell.testingapp.presentation.productList.models

sealed interface ProductListEvent {
    sealed interface Message {
        data class Text(
            val value: String,
        ) : ProductListEvent
    }

    sealed interface Navigate {
        data class ProductDetail(
            val id: String,
        ) : ProductListEvent
    }
}
