package com.erichschnell.testingapp.data.local.database.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.erichschnell.testingapp.core.builders.productEntity
import com.erichschnell.testingapp.data.local.database.MiniMarketDataBase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProductDaoTest {
    private lateinit var database: MiniMarketDataBase
    private lateinit var dao: ProductDao

    @Before
    fun setUp() {
        database =
            Room
                .inMemoryDatabaseBuilder(
                    ApplicationProvider.getApplicationContext(),
                    MiniMarketDataBase::class.java,
                ).build()
        dao = database.productDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    /*
    ------------------------------------------------------------------
    - given empty database when getAllProducts then emits empty list
    - givenAListOfProducts_whenGetAllProducts_thenEmitsTheList
    - givenAListOfProductsId_whenCallGetProductsByIds_thenEmitsTheListOfProducts
    - givenAProductId_whenCallGetProductById_thenEmitsTheProduct
    - givenAProductIdInexistent_whenCallGetProductById_thenEmitsNull
    - givenAListOfProducts_whenCallClearProducts_thenCleanDatabase
    - givenAListOfProducts_whenCallReplaceAll_thenCleanDatabaseAndInsertsNewProducts
    ------------------------------------------------------------------
     */

    @Test
    fun givenEmptyDatabase_whenGetAllProducts_thenEmitsEmprtyList() =
        runTest {
            val products = dao.getAllProducts().first()
            assertTrue(products.isEmpty())
        }

    @Test
    fun givenAListOfProducts_whenGetAllProducts_thenEmitsTheListOfProducts() =
        runTest {
            val productEntity = productEntity { withId("p1-id") }
            dao.insertProducts(listOf(productEntity))

            val products = dao.getAllProducts().first()

            assertEquals(1, products.size)
            assertEquals("p1-id", products[0].id)
        }

    @Test
    fun givenAListOfProductsId_whenCallGetProductsByIds_thenEmitsTheListOfProducts() =
        runTest {
            val p1 = productEntity { withId("p1-id") }
            val p2 = productEntity { withId("p2-id") }
            val p3 = productEntity { withId("p3-id") }
            dao.insertProducts(listOf(p1, p2, p3))

            val products = dao.getProductsByIds(listOf(p1.id, p2.id)).first()

            assertEquals(2, products.size)
            assertEquals("p1-id", products[0].id)
            assertEquals("p2-id", products[1].id)
        }

    @Test
    fun givenAProductId_whenCallGetProductById_thenEmitsTheProduct() =
        runTest {
            val p1 = productEntity { withId("p1-id") }
            dao.insertProducts(listOf(p1))

            val products = dao.getProductById(p1.id).first()

            assertNotNull(products)
            assertEquals("p1-id", products?.id)
        }

    @Test
    fun givenAProductIdInexistent_whenCallGetProductById_thenEmitsNull() =
        runTest {
            val products = dao.getProductById("other-id").first()

            assertNull(products)
        }

    @Test
    fun givenAListOfProducts_whenCallClearProducts_thenCleanDatabase() =
        runTest {
            val p1 = productEntity { withId("p1-id") }
            val p2 = productEntity { withId("p2-id") }
            val p3 = productEntity { withId("p3-id") }
            dao.insertProducts(listOf(p1, p2, p3))

            dao.clearProducts()

            val products = dao.getAllProducts().first()

            assertEquals(0, products.size)
        }

    @Test
    fun givenAListOfProducts_whenCallReplaceAll_thenCleanDatabaseAndInsertsNewProducts() =
        runTest {
            val oldP1 = productEntity { withId("old1-id") }
            val oldP2 = productEntity { withId("old2-id") }
            val oldP3 = productEntity { withId("old3-id") }

            val newP1 = productEntity { withId("new1-id") }
            val newP2 = productEntity { withId("new2-id") }
            dao.insertProducts(listOf(oldP1, oldP2, oldP3))

            dao.replaceAll(listOf(newP1, newP2))

            val products = dao.getAllProducts().first()

            assertEquals(2, products.size)
            assertEquals(newP1.id, products[0].id)
            assertEquals(newP2.id, products[1].id)
        }
}
