package com.erichschnell.testingapp.domain.ex

import com.erichschnell.testingapp.core.builders.promotion
import com.erichschnell.testingapp.domain.models.Promotion
import org.junit.Assert.*
import org.junit.Test
import java.time.Instant

class PromotionsExtensionsTest {

    @Test
    fun `given future promotion when active at then exclude`(){
        //Given
        val now = Instant.parse("2026-05-01T00:00:00Z")
        val promotion = listOf(
            promotion {
                withStartTime(now.plusSeconds(10))
                withEndTime(now.plusSeconds(100))
            }
        )

        //When
        val result = promotion.activeAt(now)

        //Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `given expired promotion when active at then exclude`(){
        //Given
        val now = Instant.parse("2026-05-01T00:00:00Z")
        val promotion = listOf(
            promotion {
                withStartTime(now.minusSeconds(100))
                withEndTime(now.minusSeconds(10))
            }
        )

        //When
        val result = promotion.activeAt(now)

        //Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun `given exact start time promotion when active at then return promotion`(){
        //Given
        val now = Instant.parse("2026-05-01T00:00:00Z")
        val promotion = listOf(
            promotion {
                withStartTime(now)
                withEndTime(now.plusSeconds(100))
            }
        )

        //When
        val result = promotion.activeAt(now)

        //Then
        assertEquals(1,result.size)
        assertEquals(promotion.first().id, result.first().id)
    }

    @Test
    fun `given exact finish time promotion when active at then return promotion`(){
        //Given
        val now = Instant.parse("2026-05-01T00:00:00Z")
        val promotion = listOf(
            promotion {
                withStartTime(now.minusSeconds(100))
                withEndTime(now)
            }
        )

        //When
        val result = promotion.activeAt(now)

        //Then
        assertEquals(1,result.size)
        assertEquals(promotion.first().id, result.first().id)
    }

    @Test
    fun `given empty list promotion when active at then return empty list`(){
        //Given
        val now = Instant.parse("2026-05-01T00:00:00Z")

        //When
        val result = emptyList<Promotion>().activeAt(now)

        //Then
        assertTrue(result.isEmpty())
    }
}