package com.erichschnell.testingapp.data.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.erichschnell.testingapp.core.mockwebserver.MockWebServerUrlHolder
import com.erichschnell.testingapp.core.mockwebserver.rules.MockWebServerRule
import com.erichschnell.testingapp.domain.repository.PromotionRepository
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
class PromotionRepositoryImplTest {

    @get:Rule(order = 0)
    val mockWebServer = MockWebServerRule()

    @get:Rule(order = 1)
    val hilt = HiltAndroidRule(this)

    @Inject
    lateinit var promotionRepository: PromotionRepository

    @Before
    fun setUp() {
        hilt.inject()
    }

    @After
    fun tearDown() {
        MockWebServerUrlHolder.baseUrl = "http://localhost:8080/"
    }

    private fun readJson( fileName: String): String {
        val context = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().context
        return context.assets.open(fileName).bufferedReader().use { it.readText() }
    }

    @Test
    fun givenActivePromotionJson_whenRefreshIsCalled_thenFlowEmitsActivePromotions() = runTest {
        val json = readJson("promotions_percent.json")
        mockWebServer.server.enqueue(MockResponse().setBody(json).setResponseCode(200))

        promotionRepository.refreshPromotions()

        val promotions = promotionRepository.getActivePromotions().first()
        assertTrue(promotions.isNotEmpty())
        assertEquals(8, promotions.size)
        assertEquals("promo1", promotions.find { it.id == "promo1"}?.id)

    }

    @Test
    fun givenEmptyActivePromotionJson_whenRefreshIsCalled_thenFlowEmitsEmptyList() = runTest {
        mockWebServer.server.enqueue(MockResponse().setBody("""{"promotions": []}""").setResponseCode(200))

        promotionRepository.refreshPromotions()

        val promotions = promotionRepository.getActivePromotions().first()
        assertTrue(promotions.isEmpty())
    }

    @Test
    fun givenBuyXPayYJson_whenRefreshIsCalled_thenDomainMapsQuantitiesCorrectly() = runTest {
        val json = readJson("promotions_buy_x_pay_y.json")
        mockWebServer.server.enqueue(MockResponse().setBody(json).setResponseCode(200))

        promotionRepository.refreshPromotions()

        val promotion = promotionRepository.getActivePromotions().first().first()
        assertNotNull(promotion)
        assertEquals(1.0, promotion.value, 0.0)
        assertEquals(2, promotion.buyQuantity)
    }

    @Test(expected = Exception::class)
    fun givenServerReturns500_whenRefreshIsCalled_thenItThrowsException() = runTest {
        mockWebServer.server.enqueue(MockResponse().setResponseCode(500))
        promotionRepository.refreshPromotions()
    }

    @Test
    fun givenPromotionsEndpoint_whenRefreshIsCalled_thenRequestISGetToCorrectPath() = runTest {
        val json = readJson("promotions_buy_x_pay_y.json")
        mockWebServer.server.enqueue(MockResponse().setBody(json).setResponseCode(200))
        promotionRepository.refreshPromotions()

        val request = mockWebServer.server.takeRequest()
        assertEquals("GET", request.method)
        assertTrue(request.path?.contains("data/promotions.json") == true)
    }


}