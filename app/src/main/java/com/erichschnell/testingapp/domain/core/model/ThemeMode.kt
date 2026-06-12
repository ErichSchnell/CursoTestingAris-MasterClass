package com.erichschnell.testingapp.domain.core.model

sealed class ThemeMode(
    val id: Int,
) {
    data object SYSTEM : ThemeMode(0)

    data object LIGHT : ThemeMode(1)

    data object DARK : ThemeMode(2)
}
