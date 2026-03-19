package com.erichschnell.testingapp.core.presentation.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.erichschnell.testingapp.cart.presentation.CartScreen
import com.erichschnell.testingapp.detail.presentation.ProductDetailScreen
import com.erichschnell.testingapp.productList.presentation.ProductListScreen
import com.erichschnell.testingapp.settings.presentation.SettingScreen

@Composable
fun NavGraph() {
    val backStack = rememberNavBackStack(Screen.ProductList)
    val entries = entryProvider<NavKey> {
        entry<Screen.ProductList> {
            ProductListScreen(
                navigateToSettings = { backStack.add(Screen.Setting) },
                navigateToProductDetail = { backStack.add(Screen.ProductDetail(it)) },
                navigateToCart = { backStack.add(Screen.Cart) },
            )
        }
        entry<Screen.Cart> {
            CartScreen(
                onBack = { backStack.removeLastOrNull() }
            )
        }
        entry<Screen.Setting> {
            SettingScreen(
                onBack = { backStack.removeLastOrNull()}
            )
        }
        entry<Screen.ProductDetail> {
            ProductDetailScreen(
                productId = it.productId,
                onBack = { backStack.removeLastOrNull() }
            )
        }
    }

    NavDisplay(
        backStack = backStack,
        entryProvider = entries,
        onBack = {backStack.removeLastOrNull()}
    )
}