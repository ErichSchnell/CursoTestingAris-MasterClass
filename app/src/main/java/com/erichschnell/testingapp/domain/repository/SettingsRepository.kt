package com.erichschnell.testingapp.domain.repository

import com.erichschnell.testingapp.domain.core.model.ThemeMode
import com.erichschnell.testingapp.domain.models.SortOption
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