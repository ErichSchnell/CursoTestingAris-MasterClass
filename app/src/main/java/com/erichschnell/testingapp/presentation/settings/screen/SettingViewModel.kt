package com.erichschnell.testingapp.presentation.settings.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erichschnell.testingapp.domain.core.model.ThemeMode
import com.erichschnell.testingapp.domain.repository.SettingsRepository
import com.erichschnell.testingapp.presentation.settings.models.SettingUiAction
import com.erichschnell.testingapp.presentation.settings.models.SettingUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
): ViewModel() {

    val uiState: StateFlow<SettingUiState> = combine(
        settingsRepository.inStockOnly,
        settingsRepository.themeMode,
        settingsRepository.showTaxes
    ) { inStockOnly, themeMode, showTaxes ->
        SettingUiState(inStockOnly = inStockOnly, showTaxes = showTaxes, themeMode = themeMode)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingUiState()
    )

    fun onAction(event: SettingUiAction) = viewModelScope.launch {
        when(event){
            is SettingUiAction.SetInStockOnly -> setInStockOnly(event.state)
            is SettingUiAction.SetShowTaxes -> setShowTaxes(event.state)
            is SettingUiAction.SetThemeMode -> setThemeMode(event.themeMode)
        }
    }

    private suspend fun setInStockOnly(state: Boolean) {
        settingsRepository.setInStockOnly(state)
    }

    private suspend fun setShowTaxes(state: Boolean) {
        settingsRepository.setShowTaxes(state)
    }

    private suspend fun setThemeMode(themeMode: ThemeMode) {
        settingsRepository.setThemeMode(themeMode)
    }

}