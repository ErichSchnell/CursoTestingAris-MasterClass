package com.erichschnell.testingapp.presentation.productDetail.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.erichschnell.testingapp.domain.models.Product

@Composable
fun AddToCartButton(
    modifier: Modifier = Modifier,
    product: Product?,
    isLoading: Boolean,
    addToCart: () -> Unit = {},
) {
    product?.let {
        if (it.stock > 0) {
            AddToCartButtonWithStock(
                modifier = modifier,
                isLoading = isLoading,
                addToCart = addToCart,
            )
        } else {
            AddToCartButtonNoStock(modifier)
        }
    }
}
