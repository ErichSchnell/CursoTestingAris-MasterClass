package com.erichschnell.testingapp.presentation.cart.screen.content

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.erichschnell.testingapp.presentation.cart.components.CartItemCard
import com.erichschnell.testingapp.presentation.cart.components.CartSummaryCard
import com.erichschnell.testingapp.presentation.cart.model.CartAction
import com.erichschnell.testingapp.presentation.cart.model.CartItemWithPromotion
import com.erichschnell.testingapp.presentation.cart.model.CartUiState
import java.text.NumberFormat
import java.util.Currency

@Composable
fun SuccesssContent(
    paddings: PaddingValues,
    state: CartUiState.Success,
    onAction: (CartAction) -> Unit
) {

    val currencyFormatter = remember {
        NumberFormat.getCurrencyInstance().apply {
            currency = Currency.getInstance("USD")
        }
    }

    Column(
        Modifier
            .padding(paddings)
            .fillMaxSize()
            .padding(16.dp),
    ) {
        AnimatedContent(
            targetState = state.cartItems.isEmpty(),
            modifier = Modifier.weight(1f)
        ){ isEmpty ->
            if (isEmpty){
                CartItemsEmptyContent()
            } else {
                CartItemsContent(
                    modifier = Modifier,
                    cartItems = state.cartItems,
                    currencyFormatter = currencyFormatter,
                    onIncreseQuantity = {id, quantity -> onAction(CartAction.IncreaseQuantity(id, quantity)) },
                    onDecreseQuantity = {id, quantity -> onAction(CartAction.DecreaseQuantity(id, quantity)) },
                    onRemove = {id -> onAction(CartAction.RemoveCartItem(id))}
                )
            }
        }
        if (state.cartItems.isNotEmpty()) {
            CartSummaryCard(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                summary = state.summary,
                currencyFormatter = currencyFormatter
            )
        }
    }
}

@Composable
private fun CartItemsEmptyContent(modifier: Modifier = Modifier) {
    Column(
        modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = "👛", style = MaterialTheme.typography.displayLarge)
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Tu carrito está vacío",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.secondary,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Agrega productos para comenzar a comprar",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun CartItemsContent(
    modifier: Modifier = Modifier,
    currencyFormatter: NumberFormat,
    cartItems: List<CartItemWithPromotion>,
    onIncreseQuantity: (String, Int) -> Unit,
    onDecreseQuantity: (String, Int) -> Unit,
    onRemove: (String) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(cartItems, key = {it.item.product.id}){itemWithProduct ->
            CartItemCard(
                modifier = Modifier.animateItem(),
                currencyFormatter = currencyFormatter,
                itemWithProduct = itemWithProduct,
                onIncreseQuantity = onIncreseQuantity,
                onDecreseQuantity = onDecreseQuantity,
                onRemove = onRemove,
            )
        }
    }
}