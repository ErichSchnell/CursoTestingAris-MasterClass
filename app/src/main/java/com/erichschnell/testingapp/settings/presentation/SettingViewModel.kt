package com.erichschnell.testingapp.settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erichschnell.testingapp.core.domain.model.ThemeMode
import com.erichschnell.testingapp.productList.domain.repository.SettingsRepository
import com.erichschnell.testingapp.settings.presentation.models.SettingUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(SettingUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        combine(
            settingsRepository.inStockOnly,
            settingsRepository.themeMode
        ) { inStockOnly, themeMode ->
            _uiState.value = SettingUiState(
                inStockOnly = inStockOnly,
                themeMode = themeMode
            )
        }.launchIn(viewModelScope)
    }

    fun setInStockOnly(state: Boolean) {
        viewModelScope.launch { settingsRepository.setInStockOnly(state) }
    }

    fun setThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch { settingsRepository.setThemeMode(themeMode) }
    }

}