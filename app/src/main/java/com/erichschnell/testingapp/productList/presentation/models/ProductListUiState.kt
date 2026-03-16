package com.erichschnell.testingapp.productList.presentation.models

import com.erichschnell.testingapp.productList.domain.models.Product

sealed class ProductListUiState {
    data object Loading: ProductListUiState()
    data class Error(val message: String): ProductListUiState()
    data class Success(
        val products: List<Product>,
//        val categories: List<>,
//        val selectedCategory: String,
//        val sortOption: String
    ): ProductListUiState()

}