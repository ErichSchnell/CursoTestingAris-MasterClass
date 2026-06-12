package com.erichschnell.testingapp.presentation.productDetail.models

import com.erichschnell.testingapp.domain.models.ProductWithPromotion

data class ProductDetailUiState(
    val item: ProductWithPromotion? = null,
    val isLoading: Boolean = true,
)
