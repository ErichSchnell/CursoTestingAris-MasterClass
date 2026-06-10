package com.erichschnell.testingapp.presentation.productList.models

import com.erichschnell.testingapp.domain.models.ProductWithPromotion
import com.erichschnell.testingapp.domain.models.SortOption

sealed interface ProductListAction {
    data class FilterBy(
        val value: String?,
    ) : ProductListAction

    data class SortedBy(
        val value: SortOption,
    ) : ProductListAction

    data class ShowFilters(
        val value: Boolean,
    ) : ProductListAction

    data class ClickProdcut(
        val value: ProductWithPromotion,
    ) : ProductListAction
}
