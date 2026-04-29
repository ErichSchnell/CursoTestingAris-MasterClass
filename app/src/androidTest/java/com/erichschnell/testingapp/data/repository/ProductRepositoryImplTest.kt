package com.erichschnell.testingapp.data.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.erichschnell.testingapp.core.mockwebserver.MockWebServerUrlHolder
import com.erichschnell.testingapp.core.mockwebserver.rules.MockWebServerRule
import com.erichschnell.testingapp.domain.repository.ProductRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ProductRepositoryImplTest {

    @get:Rule(order = 0)
    val mockWebServer = MockWebServerRule()

    @get:Rule(order = 1)
    val hilt = HiltAndroidRule(this)

    @Inject
    lateinit var productRepository: ProductRepository


    @Before
    fun setUp() {
        hilt.inject()
    }

    @After
    fun tearDown() {
        MockWebServerUrlHolder.baseUrl = "http://localhost:8080/"
    }
    
    private val productJson = """
        {"products": [
            {"id": "p1","name": "Arroz Integral 1kg","category": "Grains","priceCents": 150,"stock": 25,"imageUrl": "https://images.unsplash.com/photo-1586201327693-86619addc716"},
            {"id": "p2","name": "Aceite de Oliva 500ml","category": "Pantry","priceCents": 850,"stock": 12,"imageUrl": "https://images.unsplash.com/photo-1474979266404-7eaacbad88c5"}
        ]}
    """.trimIndent()

    @Test
    fun givenValidProductsJson_whenRefreshIsCalled_thenDatabaseEmitProductsFromRoom() = runTest {
        mockWebServer.server.enqueue(MockResponse().setBody(productJson).setResponseCode(200))

        productRepository.refreshProduct()

        val products = productRepository.getProducts().first()
        assertTrue(products.isNotEmpty())
        assertTrue(products.size == 2)
        assertEquals("Arroz Integral 1kg", products.find { it.id == "p1"}?.name)
        assertEquals("Aceite de Oliva 500ml", products.find { it.id == "p2"}?.name)

    }

    @Test
    fun givenEmptyProductJson_whenRefreshIsCalled_thenGetProductsEmitsEmptyList() = runTest {
        mockWebServer.server.enqueue(MockResponse().setBody("""{"products": []}""").setResponseCode(200))

        productRepository.refreshProduct()

        val products = productRepository.getProducts().first()
        assertTrue(products.isEmpty())
    }

    @Test
    fun givenProductJson_whenRefreshAndGetProductById_thenReturnsCorrectProduct() = runTest {
        mockWebServer.server.enqueue(MockResponse().setBody(productJson).setResponseCode(200))

        productRepository.refreshProduct()

        val product = productRepository.getProductById("p1").first()

        assertNotNull(product)
        assertTrue(product?.id == "p1")
        assertTrue(product?.name == "Arroz Integral 1kg")
    }

    @Test(expected = Exception::class)
    fun givenServerReturns500_whenRefreshIsCalled_thenItThrowsException() = runTest {
        mockWebServer.server.enqueue(MockResponse().setResponseCode(500))

        productRepository.refreshProduct()
    }

    @Test
    fun givenCachedProducts_whenRefreshWithNewProducts_thenFlowEmitsUpdatedData() = runTest {
        mockWebServer.server.enqueue(MockResponse().setBody(productJson).setResponseCode(200))
        productRepository.refreshProduct()

        val productJsonUpdated = """
        {"products": [
            {"id": "p1","name": "Arroz Comunete 1kg","category": "Grains","priceCents": 150,"stock": 25,"imageUrl": "https://images.unsplash.com/photo-1586201327693-86619addc716"}        ]}
    """.trimIndent()

        mockWebServer.server.enqueue(MockResponse().setBody(productJsonUpdated).setResponseCode(200))
        productRepository.refreshProduct()

        val products = productRepository.getProducts().first()
        assertTrue(products.isNotEmpty())
        assertTrue(products.size == 1)
        assertEquals("Arroz Comunete 1kg", products.find { it.id == "p1"}?.name)
    }

    @Test
    fun givenProductsEndpoint_whenRefreshIsCalled_thenRequestISGetToCorrectPath() = runTest {
        mockWebServer.server.enqueue(MockResponse().setBody(productJson).setResponseCode(200))
        productRepository.refreshProduct()

        val request = mockWebServer.server.takeRequest()
        assertEquals("GET", request.method)
        assertTrue(request.path?.contains("data/products.json") == true)
    }

}