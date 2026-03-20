package com.erichschnell.testingapp.productList.presentation.models

sealed interface ProductListEvent {

    sealed interface Message {
        data class Text(val value: String) : ProductListEvent
    }

    sealed interface Navigate {
        data class ProductDetail(val id: String) : ProductListEvent
    }
}