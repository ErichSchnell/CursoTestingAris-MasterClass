package com.erichschnell.testingapp.settings.presentation.models

import com.erichschnell.testingapp.core.domain.model.ThemeMode

data class SettingUiState (
    val inStockOnly: Boolean = false,
    val themeMode: ThemeMode = ThemeMode.SYSTEM
)