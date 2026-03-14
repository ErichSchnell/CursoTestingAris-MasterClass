package com.erichschnell.testingapp.productList.presentation.models

sealed interface ProductListEvent {
    data class showMessage(val message: String): ProductListEvent
}