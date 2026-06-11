package com.erichschnell.testingapp.presentation.settings.screen

import androidx.activity.ComponentActivity
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.erichschnell.testingapp.domain.core.model.ThemeMode
import com.erichschnell.testingapp.presentation.core.CoreTestTag
import com.erichschnell.testingapp.presentation.settings.models.SettingUiAction
import com.erichschnell.testingapp.presentation.settings.models.SettingUiState
import com.erichschnell.testingapp.presentation.settings.models.SettingsStr
import com.erichschnell.testingapp.presentation.settings.models.SettingsTestTags
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SettingScreenTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    private fun createSettingScreen(
        modifier: Modifier = Modifier,
        state: SettingUiState = SettingUiState(),
        onAction: (SettingUiAction) -> Unit = {},
        onBack: () -> Unit = {},
    ) {
        composeRule.setContent {
            SuccessContent(
                modifier = modifier,
                state = state,
                onAction = onAction,
                onBack = onBack,
            )
        }
    }

    @Test
    fun givenDefaultSettingsState_whenRendered_thenShowsFilterAndAppearanceSection() {
        createSettingScreen(state = SettingUiState())

        composeRule.onNodeWithText(SettingsStr.TITLE).assertIsDisplayed()
        composeRule.onNodeWithText(SettingsStr.CATEGORY_FILTERS_AND_VISUALIZATION).assertIsDisplayed()
        composeRule.onNodeWithText(SettingsStr.SHOW_IN_STOCK_ONLY).assertIsDisplayed()
        composeRule.onNodeWithText(SettingsStr.SHOW_IN_STOCK_ONLY_DESCRIPTION).assertIsDisplayed()
        composeRule.onNodeWithText(SettingsStr.SHOW_WITH_TAXES).assertIsDisplayed()
        composeRule.onNodeWithText(SettingsStr.SHOW_WITH_TAXES_DESCRIPTION).assertIsDisplayed()
        composeRule.onNodeWithText(SettingsStr.CATEGORY_APPEARANCE).assertIsDisplayed()
        composeRule.onNodeWithText(SettingsStr.THEME_MODE).assertIsDisplayed()
        composeRule.onNodeWithText(SettingsStr.THEME_MODE_DESCRIPTION).assertIsDisplayed()
        composeRule.onNodeWithText(SettingsStr.THEME_SISTEMA).assertIsDisplayed()
        composeRule.onNodeWithText(SettingsStr.THEME_LIGHT).assertIsDisplayed()
        composeRule.onNodeWithText(SettingsStr.THEME_DARK).assertIsDisplayed()

        composeRule.onNodeWithTag(SettingsTestTags.SHOW_IN_STOCK_ONLY).assertIsOff()
        composeRule.onNodeWithTag(SettingsTestTags.SHOW_WITH_TAXES).assertIsOff()

        composeRule.onNodeWithTag(SettingsTestTags.THEME_SYSTEM).assertIsSelected()
        composeRule.onNodeWithTag(SettingsTestTags.THEME_DARK).assertIsNotSelected()
        composeRule.onNodeWithTag(SettingsTestTags.THEME_LIGHT).assertIsNotSelected()
    }

    @Test
    fun givenInStockOnlyFalse_whenRendered_thenSwitchIsOff() {
        createSettingScreen(state = SettingUiState(inStockOnly = false))
        composeRule.onNodeWithTag(SettingsTestTags.SHOW_IN_STOCK_ONLY).assertIsOff()
    }

    @Test
    fun givenInStockOnlyTrue_whenRendered_thenSwitchIsTrue() {
        createSettingScreen(state = SettingUiState(inStockOnly = true))
        composeRule.onNodeWithTag(SettingsTestTags.SHOW_IN_STOCK_ONLY).assertIsOn()
    }

    @Test
    fun givenShowTaxesFalse_whenRendered_thenSwitchIsOff() {
        createSettingScreen(state = SettingUiState(showTaxes = false))
        composeRule.onNodeWithTag(SettingsTestTags.SHOW_WITH_TAXES).assertIsOff()
    }

    @Test
    fun givenShowTaxesTrue_whenRendered_thenSwitchIsTrue() {
        createSettingScreen(state = SettingUiState(showTaxes = true))
        composeRule.onNodeWithTag(SettingsTestTags.SHOW_WITH_TAXES).assertIsOn()
    }

    @Test
    fun givenThemeModeSystem_whenRendered_thenSelectedIsSystem() {
        createSettingScreen(state = SettingUiState(themeMode = ThemeMode.SYSTEM))
        composeRule.onNodeWithTag(SettingsTestTags.THEME_SYSTEM).assertIsSelected()
    }

    @Test
    fun givenThemeModeLight_whenRendered_thenSelectedIsLight() {
        createSettingScreen(state = SettingUiState(themeMode = ThemeMode.LIGHT))
        composeRule.onNodeWithTag(SettingsTestTags.THEME_LIGHT).assertIsSelected()
    }

    @Test
    fun givenThemeModeDark_whenRendered_thenSelectedIsDark() {
        createSettingScreen(state = SettingUiState(themeMode = ThemeMode.DARK))
        composeRule.onNodeWithTag(SettingsTestTags.THEME_DARK).assertIsSelected()
    }

    @Test
    fun givenSettingsRendered_whenBackClicked_thenOnBackIsCalled() {
        var backClicked = false

        createSettingScreen(
            onBack = { backClicked = true },
        )

        composeRule.onNodeWithTag(CoreTestTag.TOP_APP_BAR_BACK_BUTTON).performClick()

        composeRule.runOnIdle {
            assertTrue(backClicked)
        }
    }

    @Test
    fun givenSettingsRendered_whenClickedOnShowInStockOnly_thenInStockOnlyChanged() {
        var swtichClicked = false

        createSettingScreen(onAction = {
            if (it is SettingUiAction.SetInStockOnly) {
                swtichClicked = true
            }
        })

        composeRule.onNodeWithTag(SettingsTestTags.SHOW_IN_STOCK_ONLY).performClick()

        composeRule.runOnIdle {
            assertTrue(swtichClicked)
        }
    }

    @Test
    fun givenSettingsRendered_whenClickedShowTaxes_thenShowTaxesChanged() {
        var swtichClicked = false

        createSettingScreen(onAction = {
            if (it is SettingUiAction.SetShowTaxes) {
                swtichClicked = true
            }
        })

        composeRule.onNodeWithTag(SettingsTestTags.SHOW_WITH_TAXES).performClick()

        composeRule.runOnIdle {
            assertTrue(swtichClicked)
        }
    }

    @Test
    fun givenSettingsRendered_whenThemeModeClicked_thenThemeModeChanged() {
        var themeSelected: ThemeMode = ThemeMode.DARK

        createSettingScreen(onAction = {
            if (it is SettingUiAction.SetThemeMode) {
                themeSelected = it.themeMode
            }
        })

        composeRule.onNodeWithTag(SettingsTestTags.THEME_SYSTEM).performClick()
        assertEquals(ThemeMode.SYSTEM, themeSelected)

        composeRule.onNodeWithTag(SettingsTestTags.THEME_LIGHT).performClick()
        assertEquals(ThemeMode.LIGHT, themeSelected)

        composeRule.onNodeWithTag(SettingsTestTags.THEME_DARK).performClick()
        assertEquals(ThemeMode.DARK, themeSelected)
    }
}
