package com.erichschnell.testingapp.productList.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.erichschnell.testingapp.core.domain.model.ThemeMode
import com.erichschnell.testingapp.productList.domain.models.SortOption
import com.erichschnell.testingapp.productList.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
): SettingsRepository {

    companion object {
        val IN_STOCK_ONLY_KEY = booleanPreferencesKey("IN_STOCK_ONLY_KEY")
        val FILTERS_VISIBLE_KEY = booleanPreferencesKey("FILTERS_VISIBLE_KEY")
        val SELECT_CATEGORY_KEY = stringPreferencesKey("SELECT_CATEGORY_KEY")
        val THEME_MODE_KEY = intPreferencesKey("THEME_MODE_KEY")
        val SORT_OPTION_KEY = stringPreferencesKey("SORT_OPTION_KEY")

    }
    private val dataStoreFlow = dataStore.data.catch { e ->
            if (e is IOException) {
                emit(emptyPreferences())
            } else {
                throw e
            }
        }

    override val inStock: Flow<Boolean> = dataStoreFlow.map { it[IN_STOCK_ONLY_KEY] ?: false }
    override val selectedCaregory: Flow<String?> = dataStoreFlow.map { it[SELECT_CATEGORY_KEY] }
    override val filtersVisible: Flow<Boolean> = dataStoreFlow.map { it[FILTERS_VISIBLE_KEY] ?: true }

    override val themeMode: Flow<ThemeMode> = dataStoreFlow.map {
        when(it[THEME_MODE_KEY]){
            ThemeMode.SYSTEM.id -> ThemeMode.SYSTEM
            ThemeMode.LIGHT.id -> ThemeMode.LIGHT
            ThemeMode.DARK.id -> ThemeMode.DARK
            else -> ThemeMode.SYSTEM
        }
    }

    override val sortOption: Flow<SortOption> = dataStoreFlow.map {
        val raw = it[SORT_OPTION_KEY] ?: SortOption.NONE.name
        runCatching { SortOption.valueOf(raw) }.getOrDefault(SortOption.NONE)
    }


    override suspend fun setInStockOnly(value: Boolean) {
        dataStore.edit { it[IN_STOCK_ONLY_KEY] = value }
    }
    override suspend fun setSelectedCaregory(value: String?) {
        dataStore.edit {
            if (value == null) {
                it.remove(SELECT_CATEGORY_KEY)
            } else {
                it[SELECT_CATEGORY_KEY] = value
            }
        }
    }
    override suspend fun setFiltersVisible(value: Boolean) {
        dataStore.edit { it[FILTERS_VISIBLE_KEY] = value }
    }
    override suspend fun setThemeMode(value: ThemeMode) {
        dataStore.edit { it[THEME_MODE_KEY] = value.id }
    }
    override suspend fun setSortOption(value: SortOption) {
        dataStore.edit { it[SORT_OPTION_KEY] = value.name }
    }
}