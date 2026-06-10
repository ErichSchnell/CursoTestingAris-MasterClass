package com.erichschnell.testingapp.domain.repository

import com.erichschnell.testingapp.domain.models.Promotion
import kotlinx.coroutines.flow.Flow

interface PromotionRepository {
    fun getActivePromotions(): Flow<List<Promotion>>

    suspend fun refreshPromotions()
}
