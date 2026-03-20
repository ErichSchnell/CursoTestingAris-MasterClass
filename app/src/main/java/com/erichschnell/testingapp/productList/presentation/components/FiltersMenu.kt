package com.erichschnell.testingapp.productList.presentation.components

import android.R.attr.category
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.erichschnell.testingapp.productList.domain.models.SortOption
import com.erichschnell.testingapp.productList.domain.models.SortOption.DISCOUNT
import com.erichschnell.testingapp.productList.domain.models.SortOption.NONE
import com.erichschnell.testingapp.productList.domain.models.SortOption.PRICE_ASC
import com.erichschnell.testingapp.productList.domain.models.SortOption.PRICE_DESC
import com.erichschnell.testingapp.productList.presentation.models.ProductListUiState

@Composable
fun FiltersMenu(
    modifier: Modifier = Modifier,
    state: ProductListUiState.Success,
    onCategorySelected: (String?) -> Unit,
    onSortSelected: (SortOption) -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Categorias",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(
                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FilterChip(
                    selected = state.selectedCategory == null,
                    onClick = { onCategorySelected(null) },
                    label = { Text("Todas", style = MaterialTheme.typography.labelSmall) }
                )
                state.categories.forEach { category ->
                    FilterChip(
                        selected = category.equals(state.selectedCategory, ignoreCase = true),
                        onClick = { onCategorySelected(category) },
                        label = { Text(category, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }
            HorizontalDivider()
            Text(
                "Ordenar por",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(
                Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FilterChip(
                    selected = state.sortOption == PRICE_ASC,
                    onClick = { onSortSelected(PRICE_ASC) },
                    label = { Text("Precio ↑", style = MaterialTheme.typography.labelSmall) }
                )
                FilterChip(
                    selected = state.sortOption == PRICE_DESC,
                    onClick = { onSortSelected(PRICE_DESC) },
                    label = { Text("Precio ↓", style = MaterialTheme.typography.labelSmall) }
                )
                FilterChip(
                    selected = state.sortOption == DISCOUNT,
                    onClick = { onSortSelected(DISCOUNT) },
                    label = { Text("Descuento ↑↓", style = MaterialTheme.typography.labelSmall) }
                )
            }
        }
    }
}