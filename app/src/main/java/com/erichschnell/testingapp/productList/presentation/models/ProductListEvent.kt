package com.erichschnell.testingapp.productList.presentation.models

import com.erichschnell.testingapp.productList.domain.models.ProductWithPromotion
import com.erichschnell.testingapp.productList.domain.models.SortOption

sealed class ProductListEvent() {

    sealed class Message() {
        data class show(val value: String) : ProductListEvent()
    }

    sealed class ButtonClick() {
        data class FilterBy(val value: String?) : ProductListEvent()
        data class SortedBy(val value: SortOption) : ProductListEvent()
        data class ShowFilters(val value: Boolean) : ProductListEvent()
        data class ClickProdcut(val value: ProductWithPromotion) : ProductListEvent()

    }

    sealed class Navigate() {
        data class ProductDetail(val id: String) : ProductListEvent()
    }
}