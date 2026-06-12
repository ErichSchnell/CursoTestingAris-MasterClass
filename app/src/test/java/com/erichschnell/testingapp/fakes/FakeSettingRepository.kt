package com.erichschnell.testingapp.fakes

import com.erichschnell.testingapp.domain.core.model.ThemeMode
import com.erichschnell.testingapp.domain.models.SortOption
import com.erichschnell.testingapp.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeSettingRepository : SettingsRepository {
    private val inStockOnlyFlow = MutableStateFlow(false)
    private val showTaxesFlow = MutableStateFlow(false)
    private val themeModeFlow = MutableStateFlow<ThemeMode>(ThemeMode.SYSTEM)
    private val selectedCategoryFlow = MutableStateFlow<String?>(null)
    private val filtersVisibleFlow = MutableStateFlow(true)
    private val sortOptionFlow = MutableStateFlow<SortOption>(SortOption.NONE)

    override val inStockOnly: Flow<Boolean> = inStockOnlyFlow.asStateFlow()
    override val showTaxes: Flow<Boolean> = showTaxesFlow.asStateFlow()
    override val themeMode: Flow<ThemeMode> = themeModeFlow.asStateFlow()
    override val selectedCaregory: Flow<String?> = selectedCategoryFlow.asStateFlow()
    override val filtersVisible: Flow<Boolean> = filtersVisibleFlow.asStateFlow()
    override val sortOption: Flow<SortOption> = sortOptionFlow.asStateFlow()

    override suspend fun setInStockOnly(value: Boolean) {
        inStockOnlyFlow.value = value
    }

    override suspend fun setShowTaxes(value: Boolean) {
        showTaxesFlow.value = value
    }

    override suspend fun setThemeMode(value: ThemeMode) {
        themeModeFlow.value = value
    }

    override suspend fun setSelectedCaregory(value: String?) {
        selectedCategoryFlow.value = value
    }

    override suspend fun setFiltersVisible(value: Boolean) {
        filtersVisibleFlow.value = value
    }

    override suspend fun setSortOption(value: SortOption) {
        sortOptionFlow.value = value
    }

    override suspend fun clear() {
    }
}
