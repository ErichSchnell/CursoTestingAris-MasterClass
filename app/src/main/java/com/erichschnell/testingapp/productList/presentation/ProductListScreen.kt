package com.erichschnell.testingapp.productList.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.erichschnell.testingapp.productList.presentation.components.FiltersMenu
import com.erichschnell.testingapp.productList.presentation.components.HomeTopAppBar
import com.erichschnell.testingapp.productList.presentation.components.ProductListEmpty
import com.erichschnell.testingapp.productList.presentation.components.ProductListItems
import com.erichschnell.testingapp.productList.presentation.models.ProductListAction
import com.erichschnell.testingapp.productList.presentation.models.ProductListEvent
import com.erichschnell.testingapp.productList.presentation.models.ProductListUiState

@Composable
fun ProductListScreen(
    productListViewModel: ProductListViewModel = hiltViewModel(),
    navigateToSettings: () -> Unit,
    navigateToCart: () -> Unit,
    navigateToProductDetail: (String) -> Unit
) {
    val uiState by productListViewModel.uiState.collectAsStateWithLifecycle()
    val showFilters by productListViewModel.showFilters.collectAsStateWithLifecycle()
    val cartItemCout by productListViewModel.cartItemCout.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        productListViewModel.events.collect { event ->
            when(event){
                is ProductListEvent.Message.Text -> snackbarHostState.showSnackbar(event.value)
                is ProductListEvent.Navigate.ProductDetail -> navigateToProductDetail(event.id)
            }
        }
    }

    Scaffold(
        topBar = { HomeTopAppBar(
            filtersVisible = showFilters,
            cartItemCount = cartItemCout,
            onFilterClick = { productListViewModel.onAction(ProductListAction.ShowFilters(it)) },
            onSettingsSelected = {navigateToSettings()},
            onCartSelected = {navigateToCart()}
        ) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        when(val state = uiState) {
            ProductListUiState.Loading -> {
                LoadingContent(modifier = Modifier.padding(padding))
            }
            is ProductListUiState.Error -> {
                ErrorContent(modifier = Modifier.padding(padding), error = state.message)
            }
            is ProductListUiState.Success -> {
                SuccessContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    state = state,
                    showFilters = showFilters,
                    onAction = productListViewModel::onAction
                )
            }
        }
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center){
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorContent(modifier: Modifier = Modifier, error:String) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center){
        Text(error, fontSize = 32.sp, color = Color.Red)
    }
}

@Composable
private fun SuccessContent(
    modifier: Modifier = Modifier,
    state: ProductListUiState.Success,
    showFilters: Boolean,
    onAction: (ProductListAction) -> Unit,
) {
    Column (modifier){
        AnimatedVisibility(visible = showFilters) {
            FiltersMenu(
                state = state,
                onCategorySelected = { onAction(ProductListAction.FilterBy(it)) },
                onSortSelected = { onAction(ProductListAction.SortedBy(it)) }
            )
        }


        Text(
            "${state.products.size} productos",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )
        if (state.products.isEmpty()){
            ProductListEmpty()
        } else {
            ProductListItems(state.products){
                onAction(ProductListAction.ClickProdcut(it))
            }
        }
    }
}

