package com.erichschnell.testingapp.cart.presentation.screen

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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.erichschnell.testingapp.cart.domain.models.CartSummary
import com.erichschnell.testingapp.cart.presentation.model.CartAction
import com.erichschnell.testingapp.cart.presentation.model.CartEvent
import com.erichschnell.testingapp.cart.presentation.model.CartItemWithPromotion
import com.erichschnell.testingapp.cart.presentation.model.CartUiState
import com.erichschnell.testingapp.cart.presentation.screen.content.SuccesssContent
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
            is CartUiState.Success -> SuccesssContent(paddings, state, viewModel::onAction)
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