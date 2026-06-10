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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.erichschnell.testingapp.domain.models.Product
import com.erichschnell.testingapp.domain.models.ProductWithPromotion
import com.erichschnell.testingapp.presentation.core.components.MarketTopAppBar
import com.erichschnell.testingapp.presentation.productDetail.components.AddToCartButton
import com.erichschnell.testingapp.presentation.productDetail.models.ProductDetailEvent
import com.erichschnell.testingapp.presentation.productDetail.models.ProductDetailTestTags
import com.erichschnell.testingapp.presentation.productDetail.models.ProductDetailUiAction
import com.erichschnell.testingapp.presentation.productDetail.models.ProductDetailUiState
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
            when (it) {
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

    ProductDetailScreenContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onBack = onBack,
        onAction = viewModel::onAction,
    )
}

@Composable
fun ProductDetailScreenContent(
    uiState: ProductDetailUiState,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onBack: () -> Unit,
    onAction: (ProductDetailUiAction) -> Unit,
) {
    Scaffold(
        topBar = {
            MarketTopAppBar(
                title = uiState.item?.product?.name ?: "",
                onBackSelected = onBack,
            )
        },
        bottomBar = {
            AddToCartButton(
                product = uiState.item?.product,
                isLoading = uiState.isLoading,
                addToCart = { onAction(ProductDetailUiAction.AddToCart) },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddings ->
        if (uiState.isLoading) {
            LoadingContent()
        } else {
            ProductDetailSuccessContent(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(paddings)
                        .padding(16.dp),
                state = uiState,
            )
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        Modifier.fillMaxSize().testTag(ProductDetailTestTags.LOADING),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Preview
@Composable
private fun PreviewProductDetailScreenSuccess() {
    val item =
        ProductWithPromotion(
            product =
                Product(
                    id = "id-bread",
                    name = "Pan",
                    description = "Pan de casa",
                    price = 10.2,
                    category = "Panaderia",
                    stock = 8,
                    imageUrl = null,
                ),
            promotion = null,
        )
    ProductDetailScreenContent(
        uiState = ProductDetailUiState(item = item, isLoading = false),
        onBack = { },
        onAction = { },
    )
}

@Preview
@Composable
private fun PreviewProductDetailScreenLoading() {
    ProductDetailScreenContent(
        uiState = ProductDetailUiState(),
        onBack = { },
        onAction = { },
    )
}
