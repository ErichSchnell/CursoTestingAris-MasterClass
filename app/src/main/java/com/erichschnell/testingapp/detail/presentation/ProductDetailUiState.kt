package com.erichschnell.testingapp.detail.presentation

import com.erichschnell.testingapp.productList.domain.models.ProductWithPromotion

data class ProductDetailUiState (
    val item: ProductWithPromotion? = null,
    val isLoading: Boolean = true
)