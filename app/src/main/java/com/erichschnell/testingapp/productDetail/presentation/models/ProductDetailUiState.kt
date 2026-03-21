package com.erichschnell.testingapp.productDetail.presentation.models

import com.erichschnell.testingapp.productList.domain.models.ProductWithPromotion

data class ProductDetailUiState (
    val item: ProductWithPromotion? = null,
    val isLoading: Boolean = true
)