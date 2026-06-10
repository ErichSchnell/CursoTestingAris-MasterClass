package com.erichschnell.testingapp.presentation.productList.models

import com.erichschnell.testingapp.domain.models.SortOption

object ProductListTestTags {
    const val STATE_LOADING = "product_list_state_loading"
    const val STATE_ERROR = "product_list_state_error"
    const val STATE_SUCCESS = "product_list_state_success"

    const val FILTERS_MENU = "filters_menu"

    fun productListProductsWithPromotion(productId: String) = "product_list_category_product_$productId"

    fun productListCategory(category: String?) = "product_list_category_${category ?: "all"}"

    fun productListSortOption(sortOption: SortOption) = "product_list_sort_option_${sortOption.name}"
}
