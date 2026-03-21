package com.erichschnell.testingapp.productDetail.presentation.models

sealed interface ProductDetailUiAction {
    data object AddToCart : ProductDetailUiAction
}