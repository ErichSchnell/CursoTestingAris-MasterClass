package com.erichschnell.testingapp.core

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.erichschnell.testingapp.core.mockwebserver.MiniMarketApiDispatcher
import com.erichschnell.testingapp.core.mockwebserver.MockWebServerUrlHolder
import com.erichschnell.testingapp.core.mockwebserver.ProductErrorDispatcher
import com.erichschnell.testingapp.core.mockwebserver.rules.MockWebServerRule
import com.erichschnell.testingapp.core.utils.asAsset
import com.erichschnell.testingapp.data.local.database.MiniMarketDataBase
import com.erichschnell.testingapp.domain.core.model.AppError
import com.erichschnell.testingapp.domain.repository.ProductRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import kotlin.test.assertFailsWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class OfflineFirstIntegrationTest {
    private companion object {
        const val DEFAULT_PRODUCT_ASSET = "product_list_default.json"
        const val DEFAULT_PRODUCTS_SIZE = 40
        const val UPDATE_PRODUCT_ASSET = "product_list_variant.json"
        const val UPDATE_PRODUCT_SIZE = 20
    }

    @get:Rule(order = 0)
    val mockWebServer = MockWebServerRule()

    @get:Rule(order = 1)
    val hilt = HiltAndroidRule(this)

    @Inject
    lateinit var db: MiniMarketDataBase

    @Inject
    lateinit var productRepo: ProductRepository

    @Before
    fun setUp() =
        runTest {
            hilt.inject()
            db.clearAllTables()
        }

    @After
    fun tearDown() {
        MockWebServerUrlHolder.baseUrl = "http://localhost:8080/"
    }

    @Test
    fun givenSuccessfulRefresh_whenGetProducts_thenRoomContainsProducts() =
        runTest {
            serverProductsFromAsset(DEFAULT_PRODUCT_ASSET)

            productRepo.refreshProduct()

            val cachedProducts =
                productRepo.getProducts().first { productList ->
                    productList.size == DEFAULT_PRODUCTS_SIZE
                }

            assertEquals(DEFAULT_PRODUCTS_SIZE, cachedProducts.size)
        }

    @Test
    fun givenEmptyCacheAndFailedRefresh_whenGetProducts_thenEmitsEmptyList() =
        runTest {
            serverProductsError()

            assertFailsWith<AppError.NetworkError> {
                productRepo.refreshProduct()
            }

            val products = productRepo.getProducts().first { it.isEmpty() }

            assertTrue(products.isEmpty())
        }

    @Test
    fun givenProdcutsCached_whenRefreshProductsAndFail_thenEmitsOldProductsCached() =
        runTest {
            serverProductsFromAsset(DEFAULT_PRODUCT_ASSET)
            productRepo.refreshProduct()
            productRepo.getProducts().first { it.size == DEFAULT_PRODUCTS_SIZE }

            serverProductsError()
            assertFailsWith<AppError.NetworkError> {
                productRepo.refreshProduct()
            }

            val products = productRepo.getProducts().first { it.size == DEFAULT_PRODUCTS_SIZE }

            assertTrue(products.size == DEFAULT_PRODUCTS_SIZE)
        }

    @Test
    fun givenProdcutsCached_whenRefreshProductsGetNewPayload_thenRemoveOldProductsCachedAdnSaveNewProducts() =
        runTest {
            serverProductsFromAsset(DEFAULT_PRODUCT_ASSET)
            productRepo.refreshProduct()
            productRepo.getProducts().first { it.size == DEFAULT_PRODUCTS_SIZE }

            serverProductsFromAsset(UPDATE_PRODUCT_ASSET)
            productRepo.refreshProduct()

            val products = productRepo.getProducts().first { it.size == UPDATE_PRODUCT_SIZE }

            assertTrue(products.size == UPDATE_PRODUCT_SIZE)
        }

    private fun serverProductsFromAsset(assetName: String) {
        mockWebServer.server.dispatcher =
            MiniMarketApiDispatcher(
                productJson = assetName.asAsset(),
            )
    }

    private fun serverProductsError() {
        mockWebServer.server.dispatcher = ProductErrorDispatcher()
    }
}
