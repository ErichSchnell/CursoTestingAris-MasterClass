package com.erichschnell.testingapp.domain.usecases

import com.erichschnell.testingapp.core.builders.product
import com.erichschnell.testingapp.domain.core.model.AppError
import com.erichschnell.testingapp.domain.repository.CartRepository
import com.erichschnell.testingapp.domain.repository.ProductRepository
import com.erichschnell.testingapp.fakes.FakeCartRepository
import com.erichschnell.testingapp.fakes.FakeProductRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class AddToCartUseCaseTest {

    @Test
    fun `zero quantity throws QuantityMustBePositive`() = runTest {
        //Given
        val fakeCartRepository = FakeCartRepository()
        val fakeProductRepository = FakeProductRepository()
        val useCase = AddToCartUseCase(fakeCartRepository, fakeProductRepository)

        //When
        val exception = runCatching { useCase("1", 0) }.exceptionOrNull()

        //Then
        assertTrue(exception is AppError.Validation.QuantityMustBePositive)
    }

    @Test
    fun `negative quantity throws QuantityMustBePositive`() = runTest {
        //Given
        val fakeCartRepository = FakeCartRepository()
        val fakeProductRepository = FakeProductRepository()
        val useCase = AddToCartUseCase(fakeCartRepository, fakeProductRepository)

        //When
        val exception = runCatching { useCase("1", -2) }.exceptionOrNull()

        //Then
        assertTrue(exception is AppError.Validation.QuantityMustBePositive)
    }

    @Test
    fun `non existing product throws NotFoundError`() = runTest {
        //Given
        val fakeCartRepository = FakeCartRepository()
        val fakeProductRepository = FakeProductRepository().apply {
            setProducts(emptyList())
        }
        val useCase = AddToCartUseCase(fakeCartRepository, fakeProductRepository)
        //When
        val exception = runCatching { useCase("2", 2) }.exceptionOrNull()

        //Then
        assertTrue(exception is AppError.NotFoundError)
    }

    @Test
    fun `insufficient stock throws InsufficientStock`() = runTest {
        //Given
        val product = product {
            withId("id-test-1")
            withStock(2)
        }
        val fakeCartRepository = FakeCartRepository()
        val fakeProductRepository = FakeProductRepository().apply {
            setProducts(listOf(product))
        }
        val useCase = AddToCartUseCase(fakeCartRepository, fakeProductRepository)

        //When
        val exception = runCatching {
            useCase(product.id, product.stock + 1)
        }.exceptionOrNull()

        //Then
        assertTrue(exception is AppError.Validation.InsufficientStock)
        assertEquals(product.stock, (exception as AppError.Validation.InsufficientStock).available)
    }

    @Test
    fun `successful case adds item to cart`() = runTest {
        //Given
        val product = product {
            withId("id-test-1")
            withStock(10)
        }
        val fakeCartRepository = FakeCartRepository()
        val fakeProductRepository = FakeProductRepository().apply {
            setProducts(listOf(product))
        }
        val useCase = AddToCartUseCase(fakeCartRepository, fakeProductRepository)

        //When
        useCase(product.id, 5)

        //Then
        val items = fakeCartRepository.getCartItems().first()
        assertEquals(product.id, items.first().productId)
        assertEquals(1, items.size)
        assertEquals(5, items.first().quantity)
    }

    @Test
    fun `add 1 item successful when unspecific quantity`() = runTest {
        //Given
        val product = product {
            withId("id-test-1")
            withStock(10)
        }
        val fakeCartRepository = FakeCartRepository()
        val fakeProductRepository = FakeProductRepository().apply {
            setProducts(listOf(product))
        }
        val useCase = AddToCartUseCase(fakeCartRepository, fakeProductRepository)

        //When
        useCase(product.id)

        //Then
        val items = fakeCartRepository.getCartItems().first()
        assertEquals(product.id, items.first().productId)
        assertEquals(1, items.size)
        assertEquals(1, items.first().quantity)
    }

    @Test
    fun `zero quantity doesn't call any repository method`() = runTest {
        //Given
        val cartRepository = mockk<CartRepository>()
        val productRepository = mockk<ProductRepository>()
        val useCase = AddToCartUseCase(cartRepository, productRepository)

        //When
        val exception = runCatching {
            useCase("1", 0)
        }.exceptionOrNull()

        //Then
        coVerify(exactly = 0) { productRepository.getProductById(any()) }
        coVerify(exactly = 0) { cartRepository.getCartItemById(any()) }
        coVerify(exactly = 0) { cartRepository.addToCart(any(), any()) }
    }

    @Test
    fun `call each method when is successful`() = runTest {
        //Given
        val product = product {
            withId("id-test-1")
            withStock(10)
        }
        val cartRepository = mockk<CartRepository>()
        val productRepository = mockk<ProductRepository>()
        val useCase = AddToCartUseCase(cartRepository, productRepository)

        coEvery { productRepository.getProductById(product.id) } returns flowOf(product)
        coEvery { cartRepository.getCartItemById(product.id) } returns null
        coEvery { cartRepository.addToCart(product.id, 3) } just Runs

        //When
        useCase(product.id, 3)

        //Then
        coVerify(exactly = 1) { productRepository.getProductById(product.id) }
        coVerify(exactly = 1) { cartRepository.getCartItemById(product.id) }
        coVerify(exactly = 1) { cartRepository.addToCart(product.id, 3) }
    }
}