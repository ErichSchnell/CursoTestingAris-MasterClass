package com.erichschnell.testingapp.presentation.cart.screen

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeRight
import com.erichschnell.testingapp.core.mothers.CartItemMother
import com.erichschnell.testingapp.core.mothers.ProductMother
import com.erichschnell.testingapp.core.mothers.uistate.CartUiMother
import com.erichschnell.testingapp.presentation.cart.model.CartAction
import com.erichschnell.testingapp.presentation.cart.model.CartStr
import com.erichschnell.testingapp.presentation.cart.model.CartTestTags
import com.erichschnell.testingapp.presentation.cart.model.CartUiState
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CartScreenTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private fun createScreen(
        uiState: CartUiState = CartUiMother.success(),
        onBack: () -> Unit = {},
        onAction: (CartAction) -> Unit = {},
    ) {
        composeRule.setContent {
            CartScreenContent(
                uiState = uiState,
                onBack = onBack,
                onAction = onAction,
            )
        }
    }

    @Test
    fun givenSuccessStateWithItems_whenRendered_thenShowSuccessState() {
        createScreen(CartUiMother.success())

        composeRule.onNodeWithTag(CartTestTags.STATE_SUCCESS).assertIsDisplayed()
        composeRule.onNodeWithText(CartStr.TITLE).assertIsDisplayed()
        composeRule.onNodeWithText(CartStr.SUMMARY_TITLE).assertIsDisplayed()
        composeRule.onNodeWithText(CartStr.SUMMARY_SUBTOTAL).assertIsDisplayed()
        composeRule.onNodeWithText(CartStr.SUMMARY_DISCOUNT).assertIsDisplayed()
        composeRule.onNodeWithText(CartStr.SUMMARY_TOTAL).assertIsDisplayed()

        composeRule.onNodeWithTag(CartTestTags.PRODUCTS_LIST)
            .performScrollToNode(hasText(ProductMother.bread().name))
        composeRule.onNodeWithText(ProductMother.bread().name)
            .assertIsDisplayed()

        composeRule.onNodeWithTag(CartTestTags.PRODUCTS_LIST)
            .performScrollToNode(hasText(ProductMother.eggs().name))
        composeRule.onNodeWithText(ProductMother.eggs().name)
            .assertIsDisplayed()

        composeRule.onNodeWithTag(CartTestTags.PRODUCTS_LIST)
            .performScrollToNode(hasText(ProductMother.milk().name))
        composeRule.onNodeWithText(ProductMother.milk().name)
            .assertIsDisplayed()

        composeRule.onNodeWithTag(CartTestTags.PRODUCTS_LIST)
            .performScrollToNode(hasText(ProductMother.soda().name))
        composeRule.onNodeWithText(ProductMother.soda().name)
            .assertIsDisplayed()
    }

    @Test
    fun givenSuccessStateWithoutItems_whenRendered_thenShowSuccessState() {
        createScreen(CartUiMother.success(cartItems = emptyList()))

        composeRule.onNodeWithTag(CartTestTags.STATE_SUCCESS).assertIsDisplayed()
        composeRule.onNodeWithTag(CartTestTags.EMPTY_CART).assertIsDisplayed()
        composeRule.onNodeWithText(CartStr.TITLE).assertIsDisplayed()
        composeRule.onNodeWithText(CartStr.ICON_EMPTY_CART).assertIsDisplayed()
        composeRule.onNodeWithText(CartStr.EMPTY_CART_SUBTITLE).assertIsDisplayed()
        composeRule.onNodeWithText(CartStr.EMPTY_CART_MESSAGE).assertIsDisplayed()

        composeRule.onNodeWithTag(CartTestTags.SUMMARY_CARD).assertDoesNotExist()
    }

    @Test
    fun givenLoadingStates_whenRendered_thenShowLoadingState() {
        createScreen(CartUiState.Loading)

        composeRule.onNodeWithTag(CartTestTags.STATE_LOADING).assertIsDisplayed()
    }

    @Test
    fun givenErrorStates_whenRendered_thenShowErrorState() {
        createScreen(CartUiState.Error("Esto es un Test"))

        composeRule.onNodeWithTag(CartTestTags.STATE_ERROR).assertIsDisplayed()
        composeRule.onNodeWithText(CartStr.REINTENTAR).assertIsDisplayed()
        composeRule.onNodeWithText("Error: Esto es un Test").assertIsDisplayed()
    }

    @Test
    fun givenSuccessRendered_whenClickAddItem_thenEmitCallBackWithAddAction() {
        var emition: Int? = null

        createScreen(
            uiState = CartUiMother.success(),
            onAction = {
                if (it is CartAction.IncreaseQuantity) {
                    emition = it.quantity + 1
                }
            },
        )

        composeRule
            .onNodeWithTag(
                CartTestTags.addQuantity(CartItemMother.bread().productId),
            ).performClick()

        assertEquals(CartItemMother.bread().quantity + 1, emition)
    }

    @Test
    fun givenSuccessRendered_whenClickSubtractItem_thenEmitCallBackWithSubtractAction() {
        var emition: Int? = null

        createScreen(
            uiState = CartUiMother.success(),
            onAction = {
                if (it is CartAction.DecreaseQuantity) {
                    emition = it.quantity - 1
                }
            },
        )

        composeRule
            .onNodeWithTag(
                CartTestTags.subtractQuantity(CartItemMother.bread().productId),
            ).performClick()

        assertEquals(CartItemMother.bread().quantity - 1, emition)
    }

    @Test
    fun givenSuccessRendered_whenSwipeRight_thenEmitCallBackWithRemoveAction() {
        var emition: String? = null

        createScreen(
            uiState = CartUiMother.success(),
            onAction = {
                if (it is CartAction.RemoveCartItem) {
                    emition = it.productId
                }
            },
        )

        composeRule
            .onNodeWithTag(
                CartTestTags.removeItem(CartItemMother.bread().productId),
            ).performTouchInput {
                swipeRight()
            }

        composeRule.waitUntil(timeoutMillis = 5_000) {
            emition != null
        }

        assertEquals(CartItemMother.bread().productId, emition)
    }

    @Test
    fun givenErrorRendered_whenClickRetry_thenEmitOnBack() {
        var emition = false

        createScreen(
            uiState = CartUiState.Error("Error"),
            onBack = { emition = true },
        )

        composeRule
            .onNodeWithTag(
                CartTestTags.ERROR_RETRY,
            ).performClick()

        assertTrue(emition)
    }
}
