package com.erichschnell.testingapp.productList.domain.repository

import com.erichschnell.testingapp.productList.domain.models.Promotion
import kotlinx.coroutines.flow.Flow

interface PromotionRepository {
    fun getActivePromotions(): Flow<List<Promotion>>
    suspend fun refreshPromotions()
}