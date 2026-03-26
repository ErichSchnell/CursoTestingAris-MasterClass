package com.erichschnell.testingapp.presentation.settings.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erichschnell.testingapp.domain.core.model.ThemeMode
import com.erichschnell.testingapp.domain.repository.SettingsRepository
import com.erichschnell.testingapp.presentation.settings.models.SettingUiAction
import com.erichschnell.testingapp.presentation.settings.models.SettingUiState
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
            settingsRepository.themeMode,
            settingsRepository.showTaxes
        ) { inStockOnly, themeMode, showTaxes ->
            _uiState.value = SettingUiState(
                inStockOnly = inStockOnly,
                showTaxes = showTaxes,
                themeMode = themeMode
            )
        }.launchIn(viewModelScope)
    }

    fun onAction(event: SettingUiAction){
        when(event){
            is SettingUiAction.SetInStockOnly -> setInStockOnly(event.state)
            is SettingUiAction.SetShowTaxes -> setShowTaxes(event.state)
            is SettingUiAction.SetThemeMode -> setThemeMode(event.themeMode)
        }
    }

    private fun setInStockOnly(state: Boolean) {
        viewModelScope.launch { settingsRepository.setInStockOnly(state) }
    }

    private fun setShowTaxes(state: Boolean) {
        viewModelScope.launch { settingsRepository.setShowTaxes(state) }
    }

    private fun setThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch { settingsRepository.setThemeMode(themeMode) }
    }

}