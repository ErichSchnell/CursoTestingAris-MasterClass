package com.erichschnell.testingapp.data.local.database.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.erichschnell.testingapp.core.builders.cartItemEntity
import com.erichschnell.testingapp.data.local.database.MiniMarketDataBase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CartItemDaoTest {

    private lateinit var database: MiniMarketDataBase
    private lateinit var dao: CartItemDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MiniMarketDataBase::class.java
        ).build()
        dao = database.cartItemDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    /*
    - givenEmptyCart_whenGetAllCartItems_thenEmitsEmptyList
    - givenInsertCartItem_whenGetAllCartItems_thenEmitsListWithCartItem
    - givenInsertCartItem_whenGetCartItemById_thenEmitsTheCartItem
    - givenProductIdNotExistent_whenGetCartItemById_thenEmitsNull
    - givenACartItemExistent_whenInsertCartItem_thenUpdateCartItem
    - givenListOfCartItemExistent_whenClearCart_thenCleanDatabase
    -----------------------------------------------------
     */

    @Test
    fun givenEmptyCart_whenGetAllCartItems_thenEmitsEmptyList() = runTest {
        val cartItems = dao.getAllCartItems().first()
        assertTrue(cartItems.isEmpty())
    }

    @Test
    fun givenInsertCartItem_whenGetAllCartItems_thenEmitsListWithCartItem() = runTest {
        val cartItem = cartItemEntity { withProductId("ct1-id") }
        dao.insertCartItem(cartItem)

        val cartItems = dao.getAllCartItems().first()
        assertEquals(1,cartItems.size)
        assertEquals(cartItem.productId, cartItems[0].productId)
    }

    @Test
    fun givenInsertCartItem_whenGetCartItemById_thenEmitsTheCartItem() = runTest {
        val cartItem = cartItemEntity { withProductId("ct1-id") }
        dao.insertCartItem(cartItem)

        val cartItemsEntity = dao.getCartItemById(cartItem.productId)
        assertNotNull(cartItemsEntity)
        assertEquals(cartItem.productId, cartItemsEntity?.productId)
    }

    @Test
    fun givenProductIdNotExistent_whenGetCartItemById_thenEmitsNull() = runTest {
        val cartItemsEntity = dao.getCartItemById("some-id")
        assertNull(cartItemsEntity)
    }

    @Test
    fun givenACartItemExistent_whenInsertCartItem_thenUpdateCartItem() = runTest {
        val cartItem = cartItemEntity { withProductId("ct1-id"); withQuantity(5) }
        dao.insertCartItem(cartItem)

        val newCartItem = cartItem.copy(quantity = 2)
        dao.insertCartItem(newCartItem)

        val cartItemEntity = dao.getAllCartItems().first()
        assertEquals(1,cartItemEntity.size)
        assertEquals(cartItem.productId, cartItemEntity[0].productId)
        assertEquals(newCartItem.quantity, cartItemEntity[0].quantity)
    }

    @Test
    fun givenListOfCartItemExistent_whenClearCart_thenCleanDatabase() = runTest {
        val cartItem = cartItemEntity { withProductId("ct1-id"); withQuantity(5) }
        dao.insertCartItem(cartItem)

        dao.clearCart()

        val cartItemsEntity = dao.getAllCartItems().first()

        assertTrue(cartItemsEntity.isEmpty())
    }
}