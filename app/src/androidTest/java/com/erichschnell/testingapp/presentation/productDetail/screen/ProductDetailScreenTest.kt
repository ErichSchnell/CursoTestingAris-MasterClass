package com.erichschnell.testingapp.presentation.productDetail.screen

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.erichschnell.testingapp.core.mothers.ProductMother
import com.erichschnell.testingapp.domain.models.ProductPromotion
import com.erichschnell.testingapp.domain.models.ProductWithPromotion
import com.erichschnell.testingapp.presentation.core.CoreTestTag
import com.erichschnell.testingapp.presentation.productDetail.models.ProductDetailTestTags
import com.erichschnell.testingapp.presentation.productDetail.models.ProductDetailUiAction
import com.erichschnell.testingapp.presentation.productDetail.models.ProductDetailUiState
import com.erichschnell.testingapp.presentation.productDetail.models.ProductDetailsStr
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class ProductDetailScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private fun createScreen(
        uiState: ProductDetailUiState = ProductDetailUiState(
            item = ProductWithPromotion(
                product = ProductMother.bread(),
                promotion = null
            ),
            isLoading = false
        ),
        onBack: () -> Unit = {},
        onAction: (ProductDetailUiAction) -> Unit = {},
    ) {
        composeRule.setContent {
            ProductDetailScreenContent(
                uiState = uiState,
                onBack = onBack,
                onAction = onAction
            )
        }
    }

    @Test
    fun givenSuccessStateWithoutItemAndIsLoading_whenRendered_thenShowLoadingState() {
        createScreen(uiState = ProductDetailUiState(null, true))
        composeRule.onNodeWithTag(ProductDetailTestTags.LOADING).assertIsDisplayed()
    }

    @Test
    fun givenSuccessStateWithItem_whenRendered_thenShowSuccessState() {
        createScreen()

        composeRule.onNodeWithText(ProductMother.bread().category).assertIsDisplayed()
        composeRule.onNodeWithText(ProductMother.bread().description).assertIsDisplayed()
        composeRule.onNodeWithText(ProductDetailsStr.stockAvailable(ProductMother.bread().stock)).assertIsDisplayed()

        composeRule.onNodeWithTag(ProductDetailTestTags.ADD_TO_CART_BUTTON).assertIsDisplayed()
    }

    @Test
    fun givenRendered_whenAddToCartClicked_thenEmitCallback() {
        var addToCartCalled = false

        createScreen(
            onAction = {
                if (it == ProductDetailUiAction.AddToCart) {
                    addToCartCalled = true
                }
            }
        )

        composeRule.onNodeWithTag(ProductDetailTestTags.ADD_TO_CART_BUTTON).performClick()
        assertTrue(addToCartCalled)
    }

    @Test
    fun givenItemWithoutStock_whenRendered_thenWithoutStock() {

        createScreen(
            uiState = ProductDetailUiState(
                item = ProductWithPromotion(
                    product = ProductMother.bread().copy(stock = 0),
                    promotion = null
                ),
                isLoading = false
            )
        )

        composeRule.onNodeWithTag(ProductDetailTestTags.WITHOUT_STOCK).assertIsDisplayed()
        composeRule.onNodeWithText(ProductDetailsStr.WITHOUT_STOCK).assertIsDisplayed()
    }

    @Test
    fun givenRendered_whenClickOnBack_thenEmitCallback() {
        var emition = false
        createScreen(
            onBack = { emition = true }
        )

        composeRule.onNodeWithTag(CoreTestTag.TOP_APP_BAR_BACK_BUTTON).performClick()

        assertTrue(emition)
    }

    @Test
    fun givenItemWithPercentPromotion_whenRendered_thenShowPercentPromotion() {
        createScreen(
            uiState = ProductDetailUiState(
                item = ProductWithPromotion(
                    product = ProductMother.bread().copy(stock = 4),
                    promotion = ProductPromotion.Percent(
                        percent = 10.0,
                        discountedPrice = 10.0,
                        label = "10 porciento de descuento"
                    )
                ),
                isLoading = false
            )
        )

        composeRule.onNodeWithText(ProductDetailsStr.discoutPercent(10)).assertIsDisplayed()

    }

}