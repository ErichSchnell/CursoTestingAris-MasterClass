package com.erichschnell.testingapp.core.mothers.uistate

import com.erichschnell.testingapp.core.mothers.ProductMother
import com.erichschnell.testingapp.core.mothers.PromotionMother
import com.erichschnell.testingapp.domain.models.ProductWithPromotion
import com.erichschnell.testingapp.domain.models.SortOption
import com.erichschnell.testingapp.presentation.productList.models.ProductListUiState

object ProductListUiStateMother {
    fun success(
        products: List<ProductWithPromotion> =
            listOf(
                ProductWithPromotion(ProductMother.bread()),
                ProductWithPromotion(ProductMother.eggs(), PromotionMother().percent()),
                ProductWithPromotion(ProductMother.milk()),
                ProductWithPromotion(ProductMother.soda(), PromotionMother().buyXPayY()),
            ),
        categories: List<String> =
            listOf(
                ProductMother.bread().category,
                ProductMother.eggs().category,
                ProductMother.milk().category,
                ProductMother.soda().category,
            ),
        selectedCategory: String? = null,
        sortOption: SortOption = SortOption.NONE,
    ) = ProductListUiState.Success(
        products = products,
        categories = categories,
        selectedCategory = selectedCategory,
        sortOption = sortOption,
    )
}
