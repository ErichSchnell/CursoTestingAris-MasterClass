package com.erichschnell.testingapp.settings.presentation.models

import com.erichschnell.testingapp.core.domain.model.ThemeMode

sealed interface SettingUiAction {
    data class SetInStockOnly(val state: Boolean): SettingUiAction
    data class SetShowTaxes(val state: Boolean): SettingUiAction
    data class SetThemeMode(val themeMode: ThemeMode): SettingUiAction
}