package com.erichschnell.testingapp.fakes

import com.erichschnell.testingapp.domain.core.model.ThemeMode
import com.erichschnell.testingapp.domain.models.SortOption
import com.erichschnell.testingapp.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeSettingRepository(): SettingsRepository {

    val _inStockOnlyFlow = MutableStateFlow(false)
    val _showTaxesFlow = MutableStateFlow(false)
    val _themeModeFlow = MutableStateFlow<ThemeMode>(ThemeMode.SYSTEM)
    val _selectedCategoryFlow = MutableStateFlow<String?>(null)
    val _filtersVisibleFlow = MutableStateFlow(true)
    val _sortOptionFlow = MutableStateFlow<SortOption>(SortOption.NONE)

    override val inStockOnly: Flow<Boolean> = _inStockOnlyFlow.asStateFlow()
    override val showTaxes: Flow<Boolean> = _showTaxesFlow.asStateFlow()
    override val themeMode: Flow<ThemeMode> = _themeModeFlow.asStateFlow()
    override val selectedCaregory: Flow<String?> = _selectedCategoryFlow.asStateFlow()
    override val filtersVisible: Flow<Boolean> = _filtersVisibleFlow.asStateFlow()
    override val sortOption: Flow<SortOption> = _sortOptionFlow.asStateFlow()

    override suspend fun setInStockOnly(value: Boolean) { _inStockOnlyFlow.value = value }
    override suspend fun setShowTaxes(value: Boolean) { _showTaxesFlow.value = value }
    override suspend fun setThemeMode(value: ThemeMode) { _themeModeFlow.value = value }
    override suspend fun setSelectedCaregory(value: String?) { _selectedCategoryFlow.value = value }
    override suspend fun setFiltersVisible(value: Boolean) { _filtersVisibleFlow.value = value }
    override suspend fun setSortOption(value: SortOption) { _sortOptionFlow.value = value }
}