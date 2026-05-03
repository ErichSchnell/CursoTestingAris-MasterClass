package com.erichschnell.testingapp.data.local.database.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.erichschnell.testingapp.core.builders.promotionEntity
import com.erichschnell.testingapp.data.local.database.MiniMarketDataBase
import com.erichschnell.testingapp.domain.models.PromotionType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PromotionDaoTest {

    private lateinit var database: MiniMarketDataBase
    private lateinit var dao: PromotionDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MiniMarketDataBase::class.java
        ).build()
        dao = database.promotionDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    /*
    - givenEmptyDatabase_whenGetAllPromotions_thenEmitsEmptyList
    - givenAListOfPromotions_whenGetAllPromotions_thenEmitsTheList
    - givenAListOfPromotions_whenClearPromotions_thenCleanDatabase
    - givenAListOfPromotions_whenReplaceAll_thenCleanDatabaseAndInsertsNewPromotions
    - givenAListOfPromotionsExistent_whenInsertPromotions_thenUpdateAllPromotionsExistentAndInsertNewPromotions
    ------------------------------------------------------------------
     */

    @Test
    fun givenEmptyDatabase_whenGetAllPromotions_thenEmitsEmptyList() = runTest {
        val promotions = dao.getAllPromotions().first()
        assertTrue(promotions.isEmpty())
    }

    @Test
    fun givenAListOfPromotions_whenGetAllPromotions_thenEmitsTheList() = runTest {
        val promotion = promotionEntity { withId("promo-1") }
        dao.insertPromotions(listOf(promotion))

        val promotions = dao.getAllPromotions().first()
        assertEquals(1, promotions.size)
        assertEquals(promotion.id, promotions[0].id)
    }

    @Test
    fun givenAListOfPromotions_whenClearPromotions_thenCleanDatabase() = runTest {
        val promo1 = promotionEntity { withId("promo-1") }
        val promo2 = promotionEntity { withId("promo-2") }
        val promo3 = promotionEntity { withId("promo-3") }
        dao.insertPromotions(listOf(promo1,promo2,promo3))

        dao.clearPromotions()

        val promotions = dao.getAllPromotions().first()
        assertTrue(promotions.isEmpty())
    }

    @Test
    fun givenAListOfPromotions_whenReplaceAll_thenCleanDatabaseAndInsertsNewPromotions() = runTest {
        val oldPromo1 = promotionEntity { withId("old-promo-1"); withType(PromotionType.PERCENT.name) }
        dao.insertPromotions(listOf(oldPromo1))

        val newPromo1 = promotionEntity { withId("new-promo-1"); withType(PromotionType.BUY_X_PAY_Y.name) }
        val newPromo2 = promotionEntity { withId("new-promo-2"); withType(PromotionType.PERCENT.name) }

        dao.replaceAll(listOf(newPromo1,newPromo2))

        val promotions = dao.getAllPromotions().first()

        assertEquals(2, promotions.size)
        assertEquals(newPromo1.id, promotions[0].id)
        assertEquals(newPromo2.id, promotions[1].id)
    }

    @Test
    fun givenAListOfPromotionsExistent_whenInsertPromotions_thenUpdateAllPromotionsExistentAndInsertNewPromotions() = runTest {
        val oldPromo1 = promotionEntity { withId("old-promo-1"); withType(PromotionType.PERCENT.name) }
        dao.insertPromotions(listOf(oldPromo1))

        val newPromo1 = promotionEntity { withId(oldPromo1.id); withType(PromotionType.BUY_X_PAY_Y.name) }
        val newPromo2 = promotionEntity { withId("new-promo-2"); withType(PromotionType.PERCENT.name) }

        dao.insertPromotions(listOf(newPromo1,newPromo2))

        val promotions = dao.getAllPromotions().first()

        assertEquals(2, promotions.size)
        assertEquals(newPromo1.id, promotions[0].id)
        assertEquals(PromotionType.BUY_X_PAY_Y.name, promotions[0].type)
        assertEquals(newPromo2.id, promotions[1].id)
    }

}