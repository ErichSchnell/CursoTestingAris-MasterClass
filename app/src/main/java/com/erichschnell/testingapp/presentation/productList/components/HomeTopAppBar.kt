package com.erichschnell.testingapp.presentation.productList.components

import android.R.attr.onClick
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.erichschnell.testingapp.presentation.core.CoreStr
import com.erichschnell.testingapp.presentation.core.CoreTestTag

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
    filtersVisible: Boolean,
    cartItemCount: Int,
    onFilterClick: (Boolean) -> Unit,
    onSettingsSelected: () -> Unit,
    onCartSelected: () -> Unit,
){
    TopAppBar(
        title = { Text(CoreStr.TITLE, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        actions = {
            IconButton(
                modifier = Modifier.testTag(CoreTestTag.SHOW_FILTERS),
                onClick = {onFilterClick(!filtersVisible)}
            ) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = if(filtersVisible) "Ocultar Filtros" else "Mostrar Filtros",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            IconButton(
                modifier = Modifier.testTag(CoreTestTag.SETTINGS),
                onClick = {onSettingsSelected()}
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Configuracion",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            BadgedBox(
                modifier = Modifier.padding(end = 8.dp),
                badge = {
                    if (cartItemCount > 0){
                        val text = if(cartItemCount > 99) CoreStr.CART_SIZE_OVER_99
                        else cartItemCount.toString()
                        Badge(modifier = Modifier.testTag(CoreTestTag.CART_BADGE)) {
                            Text(
                                text,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            ) {
                IconButton(
                    modifier = Modifier.testTag(CoreTestTag.CART),
                    onClick = {onCartSelected()}
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Carrito",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                }
            }
        }
    )
}