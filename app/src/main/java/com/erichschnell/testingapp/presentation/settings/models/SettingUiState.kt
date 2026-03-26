package com.erichschnell.testingapp.presentation.settings.models

import com.erichschnell.testingapp.domain.core.model.ThemeMode

data class SettingUiState (
    val inStockOnly: Boolean = false,
    val showTaxes: Boolean = false,
    val themeMode: ThemeMode = ThemeMode.SYSTEM
)