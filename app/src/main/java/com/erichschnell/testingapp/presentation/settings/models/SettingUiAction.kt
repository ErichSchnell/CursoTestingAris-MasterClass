package com.erichschnell.testingapp.presentation.settings.models

import com.erichschnell.testingapp.domain.core.model.ThemeMode

sealed interface SettingUiAction {
    data class SetInStockOnly(val state: Boolean): SettingUiAction
    data class SetShowTaxes(val state: Boolean): SettingUiAction
    data class SetThemeMode(val themeMode: ThemeMode): SettingUiAction
}