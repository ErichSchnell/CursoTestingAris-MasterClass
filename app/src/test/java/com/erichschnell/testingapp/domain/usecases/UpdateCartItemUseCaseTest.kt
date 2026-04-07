package com.erichschnell.testingapp.domain.usecases

import com.erichschnell.testingapp.builders.cartItem
import com.erichschnell.testingapp.builders.product
import com.erichschnell.testingapp.domain.core.model.AppError
import com.erichschnell.testingapp.domain.repository.CartRepository
import com.erichschnell.testingapp.domain.repository.ProductRepository
import com.erichschnell.testingapp.fakes.FakeCartRepository
import com.erichschnell.testingapp.fakes.FakeProductRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class UpdateCartItemUseCaseTest {

    @Test
    fun `given negative quantity when invoke then throw QuantityMustBePositive`() = runTest {
        //Given
        val cartRepository = mockk<CartRepository>()
        val productRepository = mockk<ProductRepository>()
        val useCase = UpdateCartItemUseCase(cartRepository, productRepository)

        //When
        val exception = runCatching { useCase("1", -1) }.exceptionOrNull()

        //Then
        assertTrue(exception is AppError.Validation.QuantityMustBePositive)
        coVerify(exactly = 0) { cartRepository.removeCartItem(any()) }
        coVerify(exactly = 0) { productRepository.getProductById(any()) }
        coVerify(exactly = 0) { cartRepository.updateQuantity(any(), any()) }
    }

    @Test
    fun `given zero quantity when invoke then remove item from cart`() = runTest {
        //Given
        val product = product { withId("1") }
        val cartItem = cartItem { withProductId(product.id); withQuantity(1) }
        val fakeCartRepository = FakeCartRepository().apply {
            setCartItems(listOf(cartItem))
        }
        val fakeProductRepository = FakeProductRepository()
        val useCase = UpdateCartItemUseCase(fakeCartRepository, fakeProductRepository)

        //When
        useCase(product.id, 0)

        //Then
        val items = fakeCartRepository.getCartItems().first()
        assertTrue(items.isEmpty())
    }

    @Test
    fun `given productById null when invoke then return NotFoundError`() = runTest {
        //Given
        val product = product {
            withId("1")
            withStock(10)
        }
        val fakeProductRepository = FakeProductRepository().apply {
            setProducts(listOf(product))
        }
        val fakeCartRepository = FakeCartRepository()
        val useCase = UpdateCartItemUseCase(fakeCartRepository, fakeProductRepository)

        //When
        val exception = runCatching { useCase("1", 20) }.exceptionOrNull()

        //Then
        assertTrue(exception is AppError.Validation.InsufficientStock)
        assertEquals(10, (exception as AppError.Validation.InsufficientStock).available)
    }

    @Test
    fun `given update successful when invoke then cart is updated`() = runTest {
        //Given
        val product = product {
            withId("product-1")
            withStock(10)
        }
        val cartItem = cartItem { withProductId(product.id); withQuantity(1) }
        val fakeProductRepository = FakeProductRepository().apply {
            setProducts(listOf(product))
        }
        val fakeCartRepository = FakeCartRepository().apply {
            setCartItems(listOf(cartItem))
        }
        val useCase = UpdateCartItemUseCase(fakeCartRepository, fakeProductRepository)

        //When
        useCase(product.id, 5)

        //Then
        val items = fakeCartRepository.getCartItems().first()
        assertEquals(1, items.size)
        assertEquals(cartItem.productId, items.first().productId)
        assertEquals(5, items.first().quantity)
    }

}