package com.erichschnell.testingapp.presentation.cart.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.erichschnell.testingapp.domain.models.CartItem
import com.erichschnell.testingapp.domain.models.CartSummary
import com.erichschnell.testingapp.domain.models.Product
import com.erichschnell.testingapp.domain.models.ProductWithPromotion
import com.erichschnell.testingapp.presentation.cart.model.CartAction
import com.erichschnell.testingapp.presentation.cart.model.CartEvent
import com.erichschnell.testingapp.presentation.cart.model.CartItemWithPromotion
import com.erichschnell.testingapp.presentation.cart.model.CartStr
import com.erichschnell.testingapp.presentation.cart.model.CartTestTags
import com.erichschnell.testingapp.presentation.cart.model.CartUiState
import com.erichschnell.testingapp.presentation.cart.screen.content.CartSuccesssContent
import com.erichschnell.testingapp.presentation.core.components.MarketTopAppBar

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
            }
        }
    }

    CartScreenContent(
        uiState,
        snackbarHostState = snackbarHostState,
        onBack = onBack,
        onAction = viewModel::onAction
    )
}

@Composable
fun CartScreenContent(
    uiState: CartUiState,
    snackbarHostState:SnackbarHostState  = remember { SnackbarHostState() },
    onBack: () -> Unit,
    onAction: (CartAction) -> Unit
) {
    Scaffold(
        topBar = { MarketTopAppBar(title = CartStr.TITLE, onBackSelected = onBack) },
        snackbarHost = { SnackbarHost(snackbarHostState)}
    ) { paddings ->
        when(uiState){
            is CartUiState.Error -> ErrorContent(paddings, uiState.message, onBack)
            CartUiState.Loading -> LoadingContent(paddings)
            is CartUiState.Success -> CartSuccesssContent(paddings, uiState, onAction)
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
            .padding(16.dp)
            .testTag(CartTestTags.STATE_ERROR),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Error: $message",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(Modifier.height(16.dp))
        Button(
            modifier = Modifier.testTag(CartTestTags.ERROR_RETRY),
            onClick = { onRetry() }
        ) {
            Text(
                text = CartStr.REINTENTAR
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
            .padding(16.dp)
            .testTag(CartTestTags.STATE_LOADING),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Preview
@Composable
private fun PreviewCartScreenSuccessWithItem() {
    CartScreenContent(
        uiState = CartUiState.Success(
            cartItems = listOf(
                CartItemWithPromotion(
                    cartItem = CartItem("id-bread",5),
                    item = ProductWithPromotion(
                        product = Product(
                            id = "id-bread",
                            name = "Pan",
                            description = "Pan de la casa",
                            price = 10.0,
                            category = "Panadería",
                            stock = 10
                        )
                    )
                )
            ),
            summary = CartSummary(5.0,4.0,8.0)
        ),
        onBack = {},
        onAction = {}
    )
}

@Preview
@Composable
private fun PreviewCartScreenSuccessWithoutItem() {
    CartScreenContent(
        uiState = CartUiState.Success(
            cartItems = emptyList(),
            summary = CartSummary(5.0,4.0,8.0)
        ),
        onBack = {},
        onAction = {}
    )
}

@Preview
@Composable
private fun PreviewCartScreenLoading() {
    CartScreenContent(
        uiState = CartUiState.Loading,
        onBack = {},
        onAction = {}
    )
}

@Preview
@Composable
private fun PreviewCartScreenError() {
    CartScreenContent(
        uiState = CartUiState.Error(message = "Error"),
        onBack = {},
        onAction = {}
    )
}