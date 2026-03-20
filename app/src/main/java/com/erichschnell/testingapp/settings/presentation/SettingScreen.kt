package com.erichschnell.testingapp.settings.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.erichschnell.testingapp.core.domain.model.ThemeMode
import com.erichschnell.testingapp.core.presentation.components.MarketTopAppBar
import com.erichschnell.testingapp.settings.presentation.components.CategorySettingCard
import com.erichschnell.testingapp.settings.presentation.components.RowSettingSwitch
import com.erichschnell.testingapp.settings.presentation.models.SettingUiAction
import com.erichschnell.testingapp.settings.presentation.models.SettingUiState

@Composable
fun SettingScreen(
    viewModel: SettingViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { MarketTopAppBar(title = "Ajustes", onBackSelected = { onBack() }) }
    ) { paddings ->
        SuccessContent(
            modifier = Modifier.fillMaxSize().padding(paddings).padding(16.dp),
            state = uiState,
            onAction = viewModel::onAction
        )
    }
}

@Composable
fun SuccessContent(
    modifier: Modifier = Modifier,
    state: SettingUiState,
    onAction: (SettingUiAction) -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FiltersAndVisualization(
            showInStock = state.inStockOnly,
            onShowInStockChange = { onAction(SettingUiAction.SetInStockOnly(it)) },
            showTaxes = state.showTaxes,
            onShowTaxesChange = { onAction(SettingUiAction.SetShowTaxes(it)) }
        )
        SettingThemeApp(
            themeSelected = state.themeMode,
            onThemeSelected = { onAction(SettingUiAction.SetThemeMode(it)) }
        )
    }
}

@Composable
private fun FiltersAndVisualization(
    modifier: Modifier = Modifier,
    showInStock: Boolean,
    onShowInStockChange: (Boolean) -> Unit,
    showTaxes: Boolean,
    onShowTaxesChange: (Boolean) -> Unit
) {
    CategorySettingCard(
        modifier = modifier,
        icon = Icons.Default.Info,
        title = "Filtros y visualización"
    ) {
        RowSettingSwitch(
            title = "Solo productos en Stock",
            description = "Muestrame únicamente productos disponibles",
            checked = showInStock,
            onCheckedChange = onShowInStockChange
        )
        RowSettingSwitch(
            title = "Mostrar impuestos incluídos",
            description = "Incluir impuestos en los precios mostrados",
            checked = showTaxes,
            onCheckedChange = onShowTaxesChange
        )
    }
}

@Composable
fun SettingThemeApp(modifier: Modifier = Modifier, themeSelected: ThemeMode, onThemeSelected: (ThemeMode) -> Unit) {

    CategorySettingCard(
        modifier = modifier,
        icon = Icons.Default.DarkMode,
        title = "Apariencia"
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                "Tema de la aplicación",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                "Elige entre modo claro, oscuro y sistema",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant

            )
            Spacer(Modifier.height(4.dp))

            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(0,3),
                    onClick = { onThemeSelected(ThemeMode.SYSTEM) },
                    selected = themeSelected == ThemeMode.SYSTEM,
                    label = { Text("Sistema") },
                )
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(1,3),
                    onClick = { onThemeSelected(ThemeMode.LIGHT) },
                    selected = themeSelected == ThemeMode.LIGHT,
                    label = { Text("Claro") },
                )
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(2,3),
                    onClick = { onThemeSelected(ThemeMode.DARK) },
                    selected = themeSelected == ThemeMode.DARK,
                    label = { Text("Oscuro") },
                )
            }
        }
    }
}