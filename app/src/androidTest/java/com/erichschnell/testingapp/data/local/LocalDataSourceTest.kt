package com.erichschnell.testingapp.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.erichschnell.testingapp.core.builders.cartItemEntity
import com.erichschnell.testingapp.core.builders.productEntity
import com.erichschnell.testingapp.data.local.database.MiniMarketDataBase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocalDataSourceTest {

    private lateinit var database: MiniMarketDataBase
    private lateinit var localDataSource: LocalDataSource

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MiniMarketDataBase::class.java
        ).build()
        localDataSource = LocalDataSource(
            database.productDao(),
            database.promotionDao(),
            database.cartItemDao()
        )

    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun givenSetOfProductsId_whenGetProductsByIds_thenEmitsListOfProducts() = runTest {
        val products = listOf(
            productEntity { withId("product-1") },
            productEntity { withId("product-2") }
        )
        localDataSource.saveProducts(products)

        val ids = setOf("product-1")
        val productsEntity = localDataSource.getProductsByIds(ids).first()
        assertEquals(1, productsEntity.size)
        assertEquals(products[0].id, productsEntity[0].id)
    }

    @Test
    fun givenSetOfEmpty_whenGetProductsByIds_thenEmitsEmptyList() = runTest {
        val ids = emptySet<String>()
        val productsEntity = localDataSource.getProductsByIds(ids).first()
        assertTrue(productsEntity.isEmpty())
    }

    @Test
    fun givenSetOfProductsIdAndProductNotExistent_whenGetProductsByIds_thenEmitsEmptyList() = runTest {
        val ids = setOf("product-1", "product-2")
        val products = localDataSource.getProductsByIds(ids).first()
        assertTrue(products.isEmpty())
    }

    @Test
    fun givenCartItem_whenInsertCartItem_thenCartItemIsInsertedAndReturnSuccess() = runTest {
        val cartItem = cartItemEntity { withProductId("product-1") }

        val result = localDataSource.insertCartItem(cartItem)
        assertTrue(result.isSuccess)

        val cartItems = localDataSource.getAllCartItems().first()
        assertEquals(1, cartItems.size)
        assertEquals("product-1", cartItems[0].productId)
    }

    @Test
    fun givenCartItem_whenUpdateCartItem_thenCartItemIsUpdatedAndReturnSuccess() = runTest {
        val cartItem = cartItemEntity { withProductId("product-1"); withQuantity(3) }
        val cartItem2 = cartItemEntity { withProductId(cartItem.productId); withQuantity(5) }
        localDataSource.insertCartItem(cartItem)

        val result = localDataSource.updateCartItem(cartItem2)
        assertTrue(result.isSuccess)

        val cartItems = localDataSource.getAllCartItems().first()
        assertEquals(1, cartItems.size)
        assertEquals("product-1", cartItems[0].productId)
        assertEquals(5, cartItems[0].quantity)
    }

    @Test
    fun givenCartItem_whenDeleteCartItem_thenCartItemIsDeletedAndReturnSuccess() = runTest {
        val cartItem = cartItemEntity { withProductId("product-1"); withQuantity(3) }
        localDataSource.insertCartItem(cartItem)

        val result = localDataSource.deleteCartItem(cartItem)
        val cartItems = localDataSource.getAllCartItems().first()

        assertTrue(result.isSuccess)
        assertTrue(cartItems.isEmpty())
    }

    @Test
    fun givenListOfCartItem_whenClearCartItem_thenCartItemIsClearedAndReturnSuccess() = runTest {
        val cartItem1 = cartItemEntity { withProductId("product-1"); withQuantity(3) }
        val cartItem2 = cartItemEntity { withProductId("product-2"); withQuantity(5) }
        val cartItem3 = cartItemEntity { withProductId("product-3"); withQuantity(8) }

        localDataSource.insertCartItem(cartItem1)
        localDataSource.insertCartItem(cartItem2)
        localDataSource.insertCartItem(cartItem3)


        val result = localDataSource.clearCartItem()
        val cartItems = localDataSource.getAllCartItems().first()

        assertTrue(result.isSuccess)
        assertTrue(cartItems.isEmpty())
    }

}