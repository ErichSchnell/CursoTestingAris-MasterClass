package com.erichschnell.testingapp.productList.presentation.models

sealed class ProductListUiState {
    data object Loading: ProductListUiState()
    data class Error(val message: String): ProductListUiState()
    data class Success(
//        val products: List<>,
//        val categories: List<>,
        val selectedCategory: String,
//        val sortOption: String
    ): ProductListUiState()

}