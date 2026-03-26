package com.erichschnell.testingapp.presentation.productDetail.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.erichschnell.testingapp.presentation.core.components.MarketTopAppBar
import com.erichschnell.testingapp.presentation.productDetail.components.AddToCartButton
import com.erichschnell.testingapp.presentation.productDetail.models.ProductDetailEvent
import com.erichschnell.testingapp.presentation.productDetail.models.ProductDetailUiAction
import com.erichschnell.testingapp.presentation.productDetail.screen.content.ProductDetailSuccessContent

@Composable
fun ProductDetailScreen(
    viewModel: ProductDetailViewModel,
    onBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect {
            when(it){
                ProductDetailEvent.Toast.InsufficientStock -> {
                    snackbarHostState.showSnackbar("No hay suficiente stock")
                }
                ProductDetailEvent.Toast.NetworkError -> {
                    snackbarHostState.showSnackbar("Error de red")
                }
                ProductDetailEvent.Toast.NotFoundError -> {
                    snackbarHostState.showSnackbar("Ocurrio un error inesperado")
                }
                is ProductDetailEvent.Toast.AddProductSuccess -> {
                    snackbarHostState.showSnackbar("Producto agregado al carrito")
                }
            }
        }
    }

    Scaffold(
        topBar = {
            MarketTopAppBar(
                title = uiState.item?.product?.name ?: "",
                onBackSelected = onBack
            )
        },
        bottomBar = {
            AddToCartButton(
                product = uiState.item?.product,
                isLoading = uiState.isLoading,
                addToCart = { viewModel.onAction(ProductDetailUiAction.AddToCart) }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddings ->
        if (uiState.isLoading) {
            LoadingContent()
        } else {
            ProductDetailSuccessContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddings)
                    .padding(16.dp),
                state = uiState,
                onAction = viewModel::onAction,
            )
        }
    }
}


@Composable
private fun LoadingContent() {
    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}