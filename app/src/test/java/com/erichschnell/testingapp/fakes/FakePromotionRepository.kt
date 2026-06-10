package com.erichschnell.testingapp.fakes

import com.erichschnell.testingapp.domain.models.Promotion
import com.erichschnell.testingapp.domain.repository.PromotionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakePromotionRepository : PromotionRepository {
    private val promotions = MutableStateFlow<List<Promotion>>(emptyList())

    fun setPromotions(promotionsList: List<Promotion>) {
        promotions.value = promotionsList
    }

    override fun getActivePromotions(): Flow<List<Promotion>> = promotions.asStateFlow()

    override suspend fun refreshPromotions() {}
}
