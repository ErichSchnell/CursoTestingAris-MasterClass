package com.erichschnell.testingapp.productList.domain.repository

import com.erichschnell.testingapp.core.domain.model.ThemeMode
import com.erichschnell.testingapp.productList.domain.models.SortOption
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val inStockOnly: Flow<Boolean>
    val showTaxes: Flow<Boolean>
    val themeMode: Flow<ThemeMode>
    val selectedCaregory: Flow<String?>
    val filtersVisible: Flow<Boolean>
    val sortOption: Flow<SortOption>

    suspend fun setInStockOnly(value: Boolean)
    suspend fun setShowTaxes(value: Boolean)
    suspend fun setThemeMode(value: ThemeMode)
    suspend fun setSelectedCaregory(value: String?)
    suspend fun setFiltersVisible(value: Boolean)
    suspend fun setSortOption(value: SortOption)
}