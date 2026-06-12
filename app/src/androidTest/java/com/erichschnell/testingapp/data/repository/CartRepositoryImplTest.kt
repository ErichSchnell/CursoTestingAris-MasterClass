package com.erichschnell.testingapp.data.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.erichschnell.testingapp.domain.core.model.AppError
import com.erichschnell.testingapp.domain.repository.CartRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class CartRepositoryImplTest {
    @get:Rule(order = 1)
    val hilt = HiltAndroidRule(this)

    @Inject
    lateinit var cartRepository: CartRepository

    @Before
    fun setUp() {
        hilt.inject()
    }

    @Test
    fun givenCartItemsEmpty_whenGetCartItemsIsCalled_thenReturnsEmptyList() =
        runTest {
            val cartItems = cartRepository.getCartItems().first()
            assertTrue(cartItems.isEmpty())
        }

    @Test
    fun givenCartItems_whenGetCartItemsIsCalled_thenReturnsListOfCartItems() =
        runTest {
            cartRepository.addToCart("p1", 1)

            val cartItems = cartRepository.getCartItems().first()
            assertTrue(cartItems.size == 1)
            assertEquals("p1", cartItems.first().productId)
        }

    @Test
    fun givenCartItem_whenGetCartItemByIdIsCalled_thenReturnsCorrectCartItem() =
        runTest {
            cartRepository.addToCart("p1", 1)

            val cartItem = cartRepository.getCartItemById("p1")
            assertTrue(cartItem?.productId == "p1")
        }

    @Test
    fun givenCartItemNotExistInCart_whenGetCartItemByIdIsCalled_thenReturnsNull() =
        runTest {
            val cartItem = cartRepository.getCartItemById("p1")
            assertNull(cartItem)
        }

    @Test
    fun givenProductIdNotExistInCart_whenAddToCartIsCalled_thenCartItemIsAddedToCart() =
        runTest {
            cartRepository.addToCart("p1", 1)
            val cartItems = cartRepository.getCartItems().first()
            assertTrue(cartItems.size == 1)
            assertTrue(cartItems.first().productId == "p1")
            assertTrue(cartItems.first().quantity == 1)
        }

    @Test
    fun givenProductIdExistInCartAndQuantity_whenAddToCartIsCalled_thenCartItemIsUpdated() =
        runTest {
            cartRepository.addToCart("p1", 1)

            cartRepository.addToCart("p1", 1)
            val cartItems = cartRepository.getCartItems().first()
            assertTrue(cartItems.size == 1)
            assertTrue(cartItems.first().productId == "p1")
            assertTrue(cartItems.first().quantity == 2)
        }

    @Test
    fun givenProductIdExistInCart_whenRemoveCartItemIsCalled_thenCartItemIsRemoved() =
        runTest {
            cartRepository.addToCart("p1", 1)

            cartRepository.removeCartItem("p1")

            val cartItems = cartRepository.getCartItems().first()
            assertTrue(cartItems.isEmpty())
        }

    @Test(expected = AppError.NotFoundError::class)
    fun givenProductIdNotExistInCart_whenRemoveCartItemIsCalled_thenThrowsNotFoundError() =
        runTest {
            cartRepository.removeCartItem("p1")
        }

    @Test
    fun givenProductIdExistInCart_whenUpdateQuantityIsCalled_thenCartItemIsUpdated() =
        runTest {
            cartRepository.addToCart("p1", 1)

            cartRepository.updateQuantity("p1", 1)

            val cartItems = cartRepository.getCartItems().first()
            assertTrue(cartItems.size == 1)
            assertTrue(cartItems.first().productId == "p1")
            assertTrue(cartItems.first().quantity == 1)
        }

    @Test(expected = AppError.NotFoundError::class)
    fun givenProductIdNotExistInCart_whenUpdateQuantityIsCalled_thenThrowsNotFoundError() =
        runTest {
            cartRepository.updateQuantity("p1", 2)
        }

    @Test
    fun givenCartItems_whenClearCartIsCalled_thenCartIsEmpty() =
        runTest {
            cartRepository.addToCart("p1", 1)
            cartRepository.addToCart("p2", 1)

            val flowCartItems = cartRepository.getCartItems()

            var cartItems = flowCartItems.first()
            assertTrue(cartItems.size == 2)

            cartRepository.clearCart()

            cartItems = flowCartItems.first()
            assertTrue(cartItems.isEmpty())
        }
}
