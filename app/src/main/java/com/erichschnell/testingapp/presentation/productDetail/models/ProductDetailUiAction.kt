package com.erichschnell.testingapp.presentation.productDetail.models

sealed interface ProductDetailUiAction {
    data object AddToCart : ProductDetailUiAction
}
