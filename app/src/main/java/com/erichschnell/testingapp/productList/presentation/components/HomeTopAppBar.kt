package com.erichschnell.testingapp.productList.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
    filtersVisible: Boolean,
    onFilterClick: (Boolean) -> Unit,
    onSettingsSelected: () -> Unit,
    onCartSelected: () -> Unit
){
    TopAppBar(
        title = { Text("MiniMarket", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        actions = {
            IconButton(onClick = {onFilterClick(!filtersVisible)}) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = if(filtersVisible) "Ocultar Filtros" else "Mostrar Filtros",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            IconButton(onClick = {onSettingsSelected()}) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Configuracion",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            IconButton(onClick = {onCartSelected()}) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = "Carrito",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    )
}