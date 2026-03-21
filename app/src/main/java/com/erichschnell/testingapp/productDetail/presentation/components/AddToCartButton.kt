package com.erichschnell.testingapp.productDetail.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.erichschnell.testingapp.productList.domain.models.Product

@Composable
fun AddToCartButton(
    modifier: Modifier = Modifier,
    product: Product?,
    isLoading: Boolean,
    addToCart: () -> Unit = {}
) {
    product?.let {
        if (it.stock > 0) {
            AddToCartButtonWithStock(
                modifier = modifier,
                product = it,
                isLoading = isLoading,
                addToCart = addToCart
            )
        } else {
            AddToCartButtonNoStock(modifier)
        }
    }
}