package com.erichschnell.testingapp.data.remote

import com.erichschnell.testingapp.data.remote.reponse.ProductResponse
import com.erichschnell.testingapp.data.remote.reponse.ProductsResponse
import com.erichschnell.testingapp.domain.core.model.AppError
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class RemoteDataSourceTest {
    private val server = MockWebServer()
    private lateinit var remoteDataSource: RemoteDataSource
    private lateinit var json: Json

    @Before
    fun setUp() {
        server.start()
        json =
            Json {
                ignoreUnknownKeys = true
                isLenient = true
                coerceInputValues = true
            }
        val retrofit =
            Retrofit
                .Builder()
                .baseUrl(server.url("/"))
                .client(OkHttpClient())
                .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
                .build()
        val api = retrofit.create(MiniMarketApiService::class.java)
        remoteDataSource = RemoteDataSource(api)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `given empty json response when getProducts then returns empty list`() =
        runTest {
            server.enqueue(MockResponse().setBody("""{"products": []}""").setResponseCode(200))

            val result = remoteDataSource.getProducts()

            assertTrue(result.isSuccess)
            assertTrue(result.getOrThrow().isEmpty())
        }

    @Test
    fun `given valid json file when getProducts then returns mapped dtos`() =
        runTest {
            val jsonResource = ClassLoader.getSystemResource("products_success.json").readText()
            server.enqueue(MockResponse().setBody(jsonResource).setResponseCode(200))

            val result = remoteDataSource.getProducts()

            assertTrue(result.isSuccess)
            assertEquals(40, result.getOrThrow().size)
        }

    @Test
    fun `given serialized products when getProducts then data matches original object`() =
        runTest {
            val productResponse =
                ProductResponse(
                    id = "id-1",
                    name = "product-test",
                )
            val productsResponse = ProductsResponse(listOf(productResponse))
            val jsonResource = json.encodeToString(productsResponse)
            server.enqueue(MockResponse().setBody(jsonResource).setResponseCode(200))

            val result = remoteDataSource.getProducts()

            assertTrue(result.isSuccess)
            assertEquals(1, result.getOrThrow().size)
        }

    @Test
    fun `given 404 response when getProducts then restunrs NotFoundError`() =
        runTest {
            server.enqueue(MockResponse().setResponseCode(404))

            val result = remoteDataSource.getProducts()

            assertTrue(result.isFailure)
            assertEquals(AppError.NotFoundError, result.exceptionOrNull())
        }

    @Test
    fun `given malformed json when getProducts then restunrs UnknownError`() =
        runTest {
            server.enqueue(MockResponse().setBody("error").setResponseCode(200))

            val result = remoteDataSource.getProducts()

            assertTrue(result.isFailure)
            assertTrue(result.exceptionOrNull() is AppError.UnknownError)
        }

    @Test
    fun `given promotions request when getPromotions then calls correct endpoint`() =
        runTest {
            server.enqueue(MockResponse().setBody("""{"promotions": []}""").setResponseCode(200))

            remoteDataSource.getPromotions()
            val result = server.takeRequest()

            assertEquals("/data/promotions.json", result.path)
        }
}
