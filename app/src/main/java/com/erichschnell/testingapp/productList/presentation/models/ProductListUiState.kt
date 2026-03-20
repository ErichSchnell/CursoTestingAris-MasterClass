package com.erichschnell.testingapp.productList.presentation.models

import com.erichschnell.testingapp.productList.domain.models.Product
import com.erichschnell.testingapp.productList.domain.models.ProductWithPromotion
import com.erichschnell.testingapp.productList.domain.models.SortOption

sealed interface ProductListUiState {
    data object Loading: ProductListUiState
    data class Error(val message: String): ProductListUiState
    data class Success(
        val products: List<ProductWithPromotion>,
        val categories: List<String>,
        val selectedCategory: String?,
        val sortOption: SortOption
    ): ProductListUiState
}