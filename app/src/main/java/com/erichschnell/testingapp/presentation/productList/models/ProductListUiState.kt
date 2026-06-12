package com.erichschnell.testingapp.presentation.productList.models

import com.erichschnell.testingapp.domain.models.ProductWithPromotion
import com.erichschnell.testingapp.domain.models.SortOption

sealed interface ProductListUiState {
    data object Loading : ProductListUiState

    data class Error(
        val message: String,
    ) : ProductListUiState

    data class Success(
        val products: List<ProductWithPromotion>,
        val categories: List<String>,
        val selectedCategory: String?,
        val sortOption: SortOption,
    ) : ProductListUiState
}
