package com.erichschnell.testingapp.presentation.settings.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.erichschnell.testingapp.domain.core.model.ThemeMode
import com.erichschnell.testingapp.presentation.core.components.MarketTopAppBar
import com.erichschnell.testingapp.presentation.settings.components.CategorySettingCard
import com.erichschnell.testingapp.presentation.settings.components.RowSettingSwitch
import com.erichschnell.testingapp.presentation.settings.models.SettingUiAction
import com.erichschnell.testingapp.presentation.settings.models.SettingUiState
import com.erichschnell.testingapp.presentation.settings.models.SettingsStr
import com.erichschnell.testingapp.presentation.settings.models.SettingsTestTags

@Composable
fun SettingScreen(
    viewModel: SettingViewModel = hiltViewModel(),
    onBack: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SuccessContent(
        state = uiState,
        onAction = viewModel::onAction,
        onBack = onBack,
    )
}

@Composable
fun SuccessContent(
    modifier: Modifier = Modifier,
    state: SettingUiState,
    onAction: (SettingUiAction) -> Unit,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = { MarketTopAppBar(title = SettingsStr.TITLE, onBackSelected = { onBack() }) },
    ) { paddings ->
        Column(
            modifier =
                modifier
                    .fillMaxSize()
                    .padding(paddings)
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            FiltersAndVisualization(
                showInStock = state.inStockOnly,
                onShowInStockChange = { onAction(SettingUiAction.SetInStockOnly(it)) },
                showTaxes = state.showTaxes,
                onShowTaxesChange = { onAction(SettingUiAction.SetShowTaxes(it)) },
            )
            SettingThemeApp(
                themeSelected = state.themeMode,
                onThemeSelected = { onAction(SettingUiAction.SetThemeMode(it)) },
            )
        }
    }
}

@Composable
private fun FiltersAndVisualization(
    modifier: Modifier = Modifier,
    showInStock: Boolean,
    onShowInStockChange: (Boolean) -> Unit,
    showTaxes: Boolean,
    onShowTaxesChange: (Boolean) -> Unit,
) {
    CategorySettingCard(
        modifier = modifier,
        icon = Icons.Default.Info,
        title = SettingsStr.CATEGORY_FILTERS_AND_VISUALIZATION,
    ) {
        RowSettingSwitch(
            title = SettingsStr.SHOW_IN_STOCK_ONLY,
            description = SettingsStr.SHOW_IN_STOCK_ONLY_DESCRIPTION,
            checked = showInStock,
            tagTest = SettingsTestTags.SHOW_IN_STOCK_ONLY,
            onCheckedChange = onShowInStockChange,
        )
        RowSettingSwitch(
            title = SettingsStr.SHOW_WITH_TAXES,
            description = SettingsStr.SHOW_WITH_TAXES_DESCRIPTION,
            checked = showTaxes,
            tagTest = SettingsTestTags.SHOW_WITH_TAXES,
            onCheckedChange = onShowTaxesChange,
        )
    }
}

@Composable
fun SettingThemeApp(
    modifier: Modifier = Modifier,
    themeSelected: ThemeMode,
    onThemeSelected: (ThemeMode) -> Unit,
) {
    CategorySettingCard(
        modifier = modifier,
        icon = Icons.Default.DarkMode,
        title = SettingsStr.CATEGORY_APPEARANCE,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                SettingsStr.THEME_MODE,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
            )
            Text(
                SettingsStr.THEME_MODE_DESCRIPTION,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(4.dp))

            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth(),
            ) {
                SegmentedButton(
                    modifier = Modifier.testTag(SettingsTestTags.THEME_SYSTEM),
                    shape = SegmentedButtonDefaults.itemShape(0, 3),
                    onClick = { onThemeSelected(ThemeMode.SYSTEM) },
                    selected = themeSelected == ThemeMode.SYSTEM,
                    label = { Text(SettingsStr.THEME_SISTEMA) },
                )
                SegmentedButton(
                    modifier = Modifier.testTag(SettingsTestTags.THEME_LIGHT),
                    shape = SegmentedButtonDefaults.itemShape(1, 3),
                    onClick = { onThemeSelected(ThemeMode.LIGHT) },
                    selected = themeSelected == ThemeMode.LIGHT,
                    label = { Text(SettingsStr.THEME_LIGHT) },
                )
                SegmentedButton(
                    modifier = Modifier.testTag(SettingsTestTags.THEME_DARK),
                    shape = SegmentedButtonDefaults.itemShape(2, 3),
                    onClick = { onThemeSelected(ThemeMode.DARK) },
                    selected = themeSelected == ThemeMode.DARK,
                    label = { Text(SettingsStr.THEME_DARK) },
                )
            }
        }
    }
}
// Pantalla pequeña (ideal para simular el emulador problemático del CI)
@Preview(name = "Small Screen", device = Devices.NEXUS_5)
// Pantalla normal
@Preview(name = "Phone", device = Devices.PIXEL_4)
// Pantalla grande/Tablet
@Preview(name = "Tablet", device = Devices.TABLET)
@Composable
private fun PreviewSuccessContent() {
    SuccessContent(
        modifier = Modifier,
        state = SettingUiState(),
        onAction = {},
        onBack = {},
    )
}
