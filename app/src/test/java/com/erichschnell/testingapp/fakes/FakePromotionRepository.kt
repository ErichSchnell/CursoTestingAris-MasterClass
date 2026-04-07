package com.erichschnell.testingapp.fakes

import com.erichschnell.testingapp.domain.models.Promotion
import com.erichschnell.testingapp.domain.repository.PromotionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakePromotionRepository: PromotionRepository {

    val _promotions = MutableStateFlow<List<Promotion>>(emptyList())

    fun setPromotions (promotions: List<Promotion>){ _promotions.value = promotions }

    override fun getActivePromotions(): Flow<List<Promotion>> = _promotions.asStateFlow()

    override suspend fun refreshPromotions() {}
}