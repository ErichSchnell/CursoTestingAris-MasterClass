package com.erichschnell.testingapp.cart.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.erichschnell.testingapp.cart.domain.models.CartSummary
import com.erichschnell.testingapp.cart.presentation.model.CartEvent
import com.erichschnell.testingapp.cart.presentation.model.CartItemWithPromotion
import com.erichschnell.testingapp.cart.presentation.model.CartUiState
import com.erichschnell.testingapp.core.presentation.components.MarketTopAppBar
import com.erichschnell.testingapp.core.presentation.components.QuantitySelector
import com.erichschnell.testingapp.productList.domain.models.ProductPromotion
import java.text.NumberFormat
import java.util.Currency

@Composable
fun CartScreen(
    viewModel: CartViewModel = hiltViewModel(),
    onBack: () -> Unit,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when(event){
                is CartEvent.ShowMessage -> snackbarHostState.showSnackbar(event.message)
                else -> {}
            }
        }
    }

    Scaffold(
        topBar = { MarketTopAppBar(title = "Carrito", onBackSelected = onBack) },
        snackbarHost = { SnackbarHost(snackbarHostState)}
    ) { paddings ->
        when(val state = uiState){
            is CartUiState.Error -> ErrorContent(paddings, state.message)
            CartUiState.Loading -> LoadingContent(paddings)
            is CartUiState.Success -> SuccesssContent(paddings, state, viewModel::onEvent)
        }


    }
}

@Composable
private fun ErrorContent(
    paddings: PaddingValues,
    message: String,
    onRetry: () -> Unit = {}
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(paddings)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Error: $message",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = { onRetry() }) {
            Text(
                text = "Reintentar"
            )
        }
    }
}

@Composable
private fun LoadingContent(paddings: PaddingValues) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(paddings)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun SuccesssContent(
    paddings: PaddingValues,
    state: CartUiState.Success,
    onEvent: (CartEvent) -> Unit
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
                    onIncreseQuantity = {id, quantity -> onEvent(CartEvent.Input.IncreaseQuantity(id, quantity)) },
                    onDecreseQuantity = {id, quantity -> onEvent(CartEvent.Input.DecreaseQuantity(id, quantity)) },
                    onRemove = {id -> onEvent(CartEvent.Action.RemoveCartItem(id))}
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

@Composable
private fun CartSummaryCard(
    modifier: Modifier = Modifier,
    summary: CartSummary,
    currencyFormatter: NumberFormat
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            Modifier.fillMaxWidth().padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            Text(
                text = "Resumen del carrito",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold
            )

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Subtotal",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = currencyFormatter.format(summary.subtotal),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
            if (summary.discountsTotal > 0) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
//                verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Descuento",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error,
                    )
                    Text(
                        text = currencyFormatter.format(summary.discountsTotal),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(.2f),
                thickness = 1.dp
            )

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "Total",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = currencyFormatter.format(summary.finalTotal),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold,
                )
            }

        }
    }
}

@Composable
fun CartItemCard(
    modifier: Modifier,
    currencyFormatter: NumberFormat,
    itemWithProduct: CartItemWithPromotion,
    onIncreseQuantity: (String, Int) -> Unit,
    onDecreseQuantity: (String, Int) -> Unit,
    onRemove: (String) -> Unit
) {
    val product = itemWithProduct.item.product
    val promotion = itemWithProduct.item.promotion
    val cartItem = itemWithProduct.cartItem

    val unitPrice = when(promotion){
        is ProductPromotion.Percent -> promotion.discountedPrice
        is ProductPromotion.BuyXPayY -> product.price
        null -> product.price
    }

    val hasDiscount = promotion is ProductPromotion.Percent
    val itemTotal = unitPrice * cartItem.quantity

    val dismissState = rememberSwipeToDismissBoxState()
    LaunchedEffect(dismissState.currentValue) {
        if(dismissState.currentValue == SwipeToDismissBoxValue.StartToEnd){
            onRemove(cartItem.productId)
            dismissState.snapTo(SwipeToDismissBoxValue.Settled)
        }
    }

    SwipeToDismissBox(
        modifier = modifier,
        state = dismissState,
        backgroundContent = {
            Box(
                Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.Delete,
                    contentDescription = "Eliminar",
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
            }
        },
        enableDismissFromEndToStart = false,
    ) {
        Card(
            Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainerLow)
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max)
                    .padding(8.dp)
            ) {
                AsyncImage(
                    modifier = Modifier
                        .weight(1.5f)
                        .fillMaxHeight(),
                    model = product.imageUrl,
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                )
                Spacer(Modifier.width(24.dp))
                Column(
                    modifier = Modifier.weight(3f), verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (hasDiscount){
                            Text(
                                text = currencyFormatter.format(product.price),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textDecoration = TextDecoration.LineThrough
                            )
                            Text(
                                text = "${currencyFormatter.format(unitPrice)} c/u",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Text(
                                text = "${currencyFormatter.format(unitPrice)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Text(
                        text = "Total: ${currencyFormatter.format(itemTotal)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    QuantitySelector(
                        modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(8.dp)),
                        quantity = cartItem.quantity.toString(),
                        canDecrease = cartItem.quantity > 1,
                        canIncrease = cartItem.quantity < product.stock,
                        onIncreaseQuantity = { onIncreseQuantity(product.id, cartItem.quantity) },
                        onDecreaseQuantity = { onDecreseQuantity(product.id, cartItem.quantity) }
                    )
                }
            }
        }
    }
}