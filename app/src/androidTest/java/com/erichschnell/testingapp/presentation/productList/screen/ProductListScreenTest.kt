package com.erichschnell.testingapp.presentation.productList.screen

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onChildAt
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.erichschnell.testingapp.core.mothers.ProductMother
import com.erichschnell.testingapp.core.mothers.uistate.ProductListUiStateMother
import com.erichschnell.testingapp.domain.models.ProductWithPromotion
import com.erichschnell.testingapp.domain.models.SortOption
import com.erichschnell.testingapp.presentation.core.CoreStr
import com.erichschnell.testingapp.presentation.core.CoreTestTag
import com.erichschnell.testingapp.presentation.productList.models.ProductListAction
import com.erichschnell.testingapp.presentation.productList.models.ProductListStr
import com.erichschnell.testingapp.presentation.productList.models.ProductListTestTags
import com.erichschnell.testingapp.presentation.productList.models.ProductListUiState
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ProductListScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()
    
    private fun createProductListScreen(
        uiState: ProductListUiState = ProductListUiStateMother.success(),
        showFilters: Boolean = true,
        cartItemCount: Int = 0,
        onFilterClick: (Boolean) -> Unit = {},
        onSettingsSelected: () -> Unit = {},
        onCartSelected: () -> Unit = {},
        onAction: (ProductListAction) -> Unit = {}
    ){
        composeRule.setContent {
            ProductListScreenContent(
                uiState = uiState,
                showFilters = showFilters,
                cartItemCout = cartItemCount,
                onFilterClick = onFilterClick,
                onSettingsSelected = onSettingsSelected,
                onCartSelected = onCartSelected,
                onAction = onAction
            )
        }
    }

    @Test
    fun givenLoadingState_whenRendered_thenShowLoadingState() {
        createProductListScreen(uiState = ProductListUiState.Loading)
        composeRule.onNodeWithTag(ProductListTestTags.STATE_LOADING).assertIsDisplayed()
    }

    @Test
    fun givenErrorState_whenRendered_thenShowErrorState() {
        createProductListScreen(uiState = ProductListUiState.Error(""))
        composeRule.onNodeWithTag(ProductListTestTags.STATE_ERROR).assertIsDisplayed()
    }

    @Test
    fun givenSuccessState_whenRendered_thenShowSuccessState() {
        createProductListScreen(uiState = ProductListUiStateMother.success())

        composeRule.onNodeWithTag(ProductListTestTags.STATE_SUCCESS).assertIsDisplayed()
        composeRule.onNodeWithText(ProductListStr.sizeProducts(4)).assertIsDisplayed()

        composeRule.onNodeWithText(CoreStr.TITLE).assertIsDisplayed()
        composeRule.onNodeWithText(ProductListStr.CATEGORIES).assertIsDisplayed()
        composeRule.onNodeWithText(ProductListStr.ALL_CATEGORIES).assertIsDisplayed()
        composeRule.onNodeWithText(ProductListStr.ORDER_BY).assertIsDisplayed()
        composeRule.onNodeWithText(ProductListStr.ORDER_BY_PRICE_ASC).assertIsDisplayed()
        composeRule.onNodeWithText(ProductListStr.ORDER_BY_PRICE_DESC).assertIsDisplayed()
        composeRule.onNodeWithText(ProductListStr.ORDER_BY_DISCOUNT).assertIsDisplayed()

        composeRule.onNodeWithTag(ProductMother.bread().id).assertIsDisplayed()
        composeRule.onNodeWithTag(ProductMother.eggs().id).assertIsDisplayed()
        composeRule.onNodeWithTag(ProductMother.soda().id).assertIsDisplayed()
        composeRule.onNodeWithTag(ProductMother.milk().id).assertIsDisplayed()
    }

    @Test
    fun givenSuccessState_whenRendered_thenShowEmptyMessage() {
        createProductListScreen(uiState = ProductListUiStateMother.success(products = emptyList()))

        composeRule.onNodeWithTag(ProductListTestTags.STATE_SUCCESS).assertIsDisplayed()
        composeRule.onNodeWithText(ProductListStr.sizeProducts(0)).assertIsDisplayed()
        composeRule.onNodeWithText(ProductListStr.EMPTY_CART).assertIsDisplayed()
    }

    @Test
    fun givenCategorySelected_whenRendered_thenMarkChipWithCategorySelected() {
        val category = ProductMother.milk().category
        createProductListScreen(
            uiState = ProductListUiStateMother.success(
                selectedCategory = category
            )
        )

        composeRule.onNodeWithTag(
            ProductListTestTags.productListCategory(category)
        ).assertIsSelected()
    }

    @Test
    fun givenNotCategorySelected_whenRendered_thenMarkChipWithAllCategories() {
        createProductListScreen(
            uiState = ProductListUiStateMother.success(
                selectedCategory = null
            )
        )

        composeRule.onNodeWithTag(
            ProductListTestTags.productListCategory(null)
        ).assertIsSelected()
    }

    @Test
    fun givenNotSortOptionSelected_whenRendered_thenNotMarkSorterChips() {
        createProductListScreen(
            uiState = ProductListUiStateMother.success(
                sortOption = SortOption.NONE
            )
        )

        composeRule.onNodeWithTag(
            ProductListTestTags.productListSortOption(SortOption.PRICE_ASC)
        ).assertIsNotSelected()
        composeRule.onNodeWithTag(
            ProductListTestTags.productListSortOption(SortOption.PRICE_DESC)
        ).assertIsNotSelected()
        composeRule.onNodeWithTag(
            ProductListTestTags.productListSortOption(SortOption.DISCOUNT)
        ).assertIsNotSelected()
    }

    @Test
    fun givenSortOptionSelected_whenRendered_thenMarkChipWithSortOptionSelected() {
        createProductListScreen(
            uiState = ProductListUiStateMother.success(
                sortOption = SortOption.PRICE_ASC
            )
        )

        composeRule.onNodeWithTag(
            ProductListTestTags.productListSortOption(SortOption.PRICE_ASC)
        ).assertIsSelected()
        composeRule.onNodeWithTag(
            ProductListTestTags.productListSortOption(SortOption.PRICE_DESC)
        ).assertIsNotSelected()
        composeRule.onNodeWithTag(
            ProductListTestTags.productListSortOption(SortOption.DISCOUNT)
        ).assertIsNotSelected()
    }

    @Test
    fun givenRendered_whenSelectSortOption_thenMarkChipWithSortOptionSelected() {
        val sortOptionToSelect = SortOption.DISCOUNT
        var sortOptionSelected:SortOption = SortOption.NONE

        createProductListScreen(
            uiState = ProductListUiStateMother.success(sortOption = SortOption.NONE),
            onAction = {
                if (it is ProductListAction.SortedBy) {
                    sortOptionSelected = it.value
                }
            }
        )

        composeRule.onNodeWithTag(
            ProductListTestTags.productListSortOption(sortOptionToSelect)
        ).performClick()

        assertEquals(sortOptionToSelect, sortOptionSelected)
    }

    @Test
    fun givenRendered_whenSelectCategory_thenMarkChipWithCategorySelected() {
        val categoryToSelect = ProductMother.eggs().category
        var categorySelected:String? = null

        createProductListScreen(
            uiState = ProductListUiStateMother.success(selectedCategory = null),
            onAction = {
                if (it is ProductListAction.FilterBy) {
                    categorySelected = it.value
                }
            }
        )

        composeRule.onNodeWithTag(
            ProductListTestTags.productListCategory(categoryToSelect)
        ).performClick()

        assertEquals(categoryToSelect, categorySelected)
    }

    @Test
    fun givenNotShowFilterSelected_whenRendered_thenNotShowCardFilters() {
        createProductListScreen(
            uiState = ProductListUiStateMother.success(),
            showFilters = false
        )
        composeRule.onNodeWithTag(ProductListTestTags.FILTERS_MENU).assertDoesNotExist()
    }

    @Test
    fun givenShowFilterSelected_whenRendered_thenShowCardFilters() {
        createProductListScreen(
            uiState = ProductListUiStateMother.success(),
            showFilters = true
        )
        composeRule.onNodeWithTag(ProductListTestTags.FILTERS_MENU).assertIsDisplayed()
    }

    @Test
    fun givenRendered_whenShowFilterSelected_thenShowCardFilters() {
        var showFilters = false

        createProductListScreen(
            uiState = ProductListUiStateMother.success(),
            showFilters = false,
            onFilterClick = {showFilters = it }
        )

        composeRule.onNodeWithTag(CoreTestTag.SHOW_FILTERS).performClick()

        assertTrue(showFilters)
    }

    @Test
    fun givenRendered_whenShowFilterSelected_thenNotShowCardFilters() {
        var showFilters = true

        createProductListScreen(
            uiState = ProductListUiStateMother.success(),
            showFilters = true,
            onFilterClick = {showFilters = it }
        )

        composeRule.onNodeWithTag(CoreTestTag.SHOW_FILTERS).performClick()

        assertTrue(!showFilters)
    }

    @Test
    fun givenRendered_whenClickSettings_thenEmitSettingsCallBack() {
        var settingsClicked = false

        createProductListScreen(
            uiState = ProductListUiStateMother.success(),
            onSettingsSelected = { settingsClicked = true }
        )

        composeRule.onNodeWithTag(CoreTestTag.SETTINGS).performClick()

        assertTrue(settingsClicked)
    }

    @Test
    fun givenRendered_whenClickCart_thenEmitCartCallBack() {
        var cartClicked = false

        createProductListScreen(
            uiState = ProductListUiStateMother.success(),
            onCartSelected = { cartClicked = true }
        )

        composeRule.onNodeWithTag(CoreTestTag.CART).performClick()

        assertTrue(cartClicked)
    }

    @Test
    fun givenCartItemsOver0_whenRendered_thenShowBadgeWithCartItemCount() {
        val cartItemCount = 5
        createProductListScreen(
            uiState = ProductListUiStateMother.success(),
            cartItemCount = cartItemCount
        )
        composeRule.onNodeWithText("5").assertIsDisplayed()
    }

    @Test
    fun givenCartItemsSize0_whenRendered_thenNotShowBadgeWithCartItemCount() {
        val cartItemCount = 0
        createProductListScreen(
            uiState = ProductListUiStateMother.success(),
            cartItemCount = cartItemCount
        )
        composeRule.onNodeWithTag(CoreTestTag.CART_BADGE).assertDoesNotExist()
    }

    @Test
    fun givenCartItemsOver99_whenRendered_thenShowBadgeWith99() {
        val cartItemCount = 152
        createProductListScreen(
            uiState = ProductListUiStateMother.success(),
            cartItemCount = cartItemCount
        )
        composeRule.onNodeWithText(CoreStr.CART_SIZE_OVER_99).assertIsDisplayed()
    }

    @Test
    fun givenRendered_whenClickProduct_thenEmitNavigateToProductCallBack() {
        var productClicked: ProductWithPromotion? = null

        createProductListScreen(
            uiState = ProductListUiStateMother.success(),
            onAction = {
                if (it is ProductListAction.ClickProdcut) {
                    productClicked = it.value
                }
            }
        )

        composeRule.onNodeWithTag(ProductListTestTags.productListProductsWithPromotion(
            ProductMother.bread().id
        )).performClick()

        assertEquals(ProductMother.bread(), productClicked!!.product)

    }


    

}