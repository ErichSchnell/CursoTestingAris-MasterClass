package com.erichschnell.testingapp.cart.presentation

import android.R.attr.onClick
import android.R.attr.text
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.erichschnell.testingapp.cart.domain.models.CartItem
import com.erichschnell.testingapp.cart.presentation.model.CartEvent
import com.erichschnell.testingapp.cart.presentation.model.CartItemWithPromotion
import com.erichschnell.testingapp.cart.presentation.model.CartUiState
import com.erichschnell.testingapp.core.presentation.components.MarketTopAppBar
import com.erichschnell.testingapp.core.presentation.components.QuantitySelector
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
    Box(
        Modifier
            .padding(paddings)
            .fillMaxSize()
            .padding(16.dp),
    ) {
        if (state.cartItems.isEmpty()){
            CartItemsEmptyContent()
        } else {
            CartItemsContent(
                cartItems = state.cartItems,
                onIncreseQuantity = {id, quantity -> onEvent(CartEvent.Input.IncreaseQuantity(id, quantity)) },
                onDecreseQuantity = {id, quantity -> onEvent(CartEvent.Input.DecreaseQuantity(id, quantity)) }
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
    cartItems: List<CartItemWithPromotion>,
    onIncreseQuantity: (String, Int) -> Unit,
    onDecreseQuantity: (String, Int) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(cartItems){itemWithProduct ->
            CartItemCard(
                itemWithProduct = itemWithProduct,
                onIncreseQuantity = onIncreseQuantity,
                onDecreseQuantity = onDecreseQuantity,
                onRemove = {},
            )
        }
    }
}

@Composable
fun CartItemCard(
    itemWithProduct: CartItemWithPromotion,
    onIncreseQuantity: (String, Int) -> Unit,
    onDecreseQuantity: (String, Int) -> Unit,
    onRemove: () -> Unit
) {
    val product = itemWithProduct.product
    val cartItem = itemWithProduct.cartItem

    val currencyFormatter = remember {
        NumberFormat.getCurrencyInstance().apply {
            currency = Currency.getInstance("USD")
        }
    }

    Card(
        Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            AsyncImage(
                modifier = Modifier.weight(1f),
                model = product.imageUrl,
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
            )
            Column(
                modifier = Modifier.weight(3f)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Total: ${currencyFormatter.format(product.price)}",
                    style = MaterialTheme.typography.titleMedium
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