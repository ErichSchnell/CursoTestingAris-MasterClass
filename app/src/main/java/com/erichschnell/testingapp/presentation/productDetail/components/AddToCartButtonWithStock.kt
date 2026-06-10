package com.erichschnell.testingapp.presentation.productDetail.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.erichschnell.testingapp.presentation.productDetail.models.ProductDetailTestTags
import com.erichschnell.testingapp.presentation.productDetail.models.ProductDetailsStr

@Composable
fun AddToCartButtonWithStock(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    addToCart: () -> Unit = {}
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        tonalElevation = 2.dp
    ) {
        Box(
            Modifier.padding(16.dp)
        )  {
            Button(
                modifier = Modifier.fillMaxWidth().testTag(ProductDetailTestTags.ADD_TO_CART_BUTTON),
                enabled = !isLoading,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                onClick = addToCart
            ) {
                Icon(
                    Icons.Default.ShoppingCart,
                    contentDescription = ProductDetailsStr.ADD_PRODUCT,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = ProductDetailsStr.ADD_PRODUCT,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}