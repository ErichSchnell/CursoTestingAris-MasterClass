package com.erichschnell.testingapp.core.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun QuantitySelector(
    modifier: Modifier = Modifier,
    quantity: String,
    canIncrease: Boolean = true,
    canDecrease: Boolean = true,
    onIncreaseQuantity: () -> Unit = {},
    onDecreaseQuantity: () -> Unit = {},
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            modifier = Modifier.size(36.dp),
            onClick = onDecreaseQuantity,
            enabled = canDecrease
        )  {
            Icon(
                Icons.Default.Remove,
                contentDescription = "Restar cantidad",
                modifier = Modifier.size(24.dp)
            )
        }
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(36.dp)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()){
                Text(
                    text = quantity.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        IconButton(
            modifier = Modifier.size(36.dp),
            onClick = onIncreaseQuantity,
            enabled = canIncrease
        )  {
            Icon(
                Icons.Default.Add,
                contentDescription = "Restar cantidad",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}