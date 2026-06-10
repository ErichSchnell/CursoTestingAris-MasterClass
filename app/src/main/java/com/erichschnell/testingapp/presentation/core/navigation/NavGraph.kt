package com.erichschnell.testingapp.presentation.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.erichschnell.testingapp.presentation.cart.screen.CartScreen
import com.erichschnell.testingapp.presentation.productDetail.screen.ProductDetailScreen
import com.erichschnell.testingapp.presentation.productDetail.screen.ProductDetailViewModel
import com.erichschnell.testingapp.presentation.productList.screen.ProductListScreen
import com.erichschnell.testingapp.presentation.settings.screen.SettingScreen

@Composable
fun NavGraph() {
    val backStack = rememberNavBackStack(Screen.ProductList)
    val entries =
        entryProvider<NavKey> {
            entry<Screen.ProductList> {
                ProductListScreen(
                    navigateToSettings = { backStack.add(Screen.Setting) },
                    navigateToProductDetail = { backStack.add(Screen.ProductDetail(it)) },
                    navigateToCart = { backStack.add(Screen.Cart) },
                )
            }
            entry<Screen.Cart> {
                CartScreen(
                    onBack = { backStack.removeLastOrNull() },
                )
            }
            entry<Screen.Setting> {
                SettingScreen(
                    onBack = { backStack.removeLastOrNull() },
                )
            }
            entry<Screen.ProductDetail> {
                val viewModel = hiltViewModel<ProductDetailViewModel>()

                LaunchedEffect(Unit) {
                    viewModel.loadProduct(it.productId)
                }

                ProductDetailScreen(
                    viewModel = viewModel,
                    onBack = { backStack.removeLastOrNull() },
                )
            }
        }

    NavDisplay(
        backStack = backStack,
        entryProvider = entries,
        onBack = { backStack.removeLastOrNull() },
    )
}
