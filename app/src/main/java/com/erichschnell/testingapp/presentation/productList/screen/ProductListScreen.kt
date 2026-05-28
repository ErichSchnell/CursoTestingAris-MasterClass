package com.erichschnell.testingapp.presentation.productList.screen

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.erichschnell.testingapp.domain.models.SortOption
import com.erichschnell.testingapp.presentation.productList.components.FiltersMenu
import com.erichschnell.testingapp.presentation.productList.components.HomeTopAppBar
import com.erichschnell.testingapp.presentation.productList.components.ProductListEmpty
import com.erichschnell.testingapp.presentation.productList.components.ProductListItems
import com.erichschnell.testingapp.presentation.productList.models.ProductListAction
import com.erichschnell.testingapp.presentation.productList.models.ProductListEvent
import com.erichschnell.testingapp.presentation.productList.models.ProductListStr
import com.erichschnell.testingapp.presentation.productList.models.ProductListTestTags
import com.erichschnell.testingapp.presentation.productList.models.ProductListUiState

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

    ProductListScreenContent(
        uiState = uiState,
        showFilters = showFilters,
        cartItemCout = cartItemCout,
        snackbarHostState = snackbarHostState,
        onFilterClick = { productListViewModel.onAction(ProductListAction.ShowFilters(it)) },
        onSettingsSelected = { navigateToSettings() },
        onCartSelected = { navigateToCart() },
        onAction = productListViewModel::onAction
    )

}

@Composable
fun ProductListScreenContent(
    uiState: ProductListUiState,
    showFilters: Boolean,
    cartItemCout: Int,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    onFilterClick: (Boolean) -> Unit,
    onSettingsSelected: () -> Unit,
    onCartSelected: () -> Unit,
    onAction: (ProductListAction) -> Unit
) {
    Scaffold(
        topBar = { HomeTopAppBar(
            filtersVisible = showFilters,
            cartItemCount = cartItemCout,
            onFilterClick = onFilterClick,
            onSettingsSelected = onSettingsSelected,
            onCartSelected = onCartSelected
        ) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        when(uiState) {
            ProductListUiState.Loading -> {
                LoadingContent(modifier = Modifier.padding(padding))
            }
            is ProductListUiState.Error -> {
                ErrorContent(modifier = Modifier.padding(padding), error = uiState.message)
            }
            is ProductListUiState.Success -> {
                SuccessContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    state = uiState,
                    showFilters = showFilters,
                    onAction = onAction
                )
            }
        }
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize().testTag(ProductListTestTags.STATE_LOADING), contentAlignment = Alignment.Center){
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorContent(modifier: Modifier = Modifier, error:String) {
    Box(modifier.fillMaxSize().testTag(ProductListTestTags.STATE_ERROR), contentAlignment = Alignment.Center){
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
    Column (modifier.testTag(ProductListTestTags.STATE_SUCCESS)){
        AnimatedVisibility(visible = showFilters) {
            FiltersMenu(
                modifier = Modifier.testTag(ProductListTestTags.FILTERS_MENU),
                state = state,
                onCategorySelected = { onAction(ProductListAction.FilterBy(it)) },
                onSortSelected = { onAction(ProductListAction.SortedBy(it)) }
            )
        }


        Text(
            ProductListStr.sizeProducts(state.products.size),
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

@Preview
@Composable
private fun PreviewProductListProductListScreen() {
    val uiState = ProductListUiState.Success(
        products = emptyList(),
        categories = listOf("Farmacia","Carne","Frutas","Juegos"),
        selectedCategory = null,
        sortOption = SortOption.NONE
    )
    ProductListScreenContent(
        uiState = uiState,
        showFilters = true,
        cartItemCout = 5,
        onFilterClick = { },
        onSettingsSelected = { },
        onCartSelected = {  },
        onAction = { }
    )
}

