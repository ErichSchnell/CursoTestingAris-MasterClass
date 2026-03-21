package com.erichschnell.testingapp.cart.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.erichschnell.testingapp.cart.presentation.model.CartItemWithPromotion
import com.erichschnell.testingapp.core.presentation.components.QuantitySelector
import com.erichschnell.testingapp.productList.domain.models.ProductPromotion
import java.text.NumberFormat

@Composable
fun CartItemCard(
    modifier: Modifier,
    currencyFormatter: NumberFormat,
    itemWithProduct: CartItemWithPromotion,
    onIncreseQuantity: (String, Int) -> Unit,
    onDecreseQuantity: (String, Int) -> Unit,
    onRemove: (String) -> Unit
) {
    val product = itemWithProduct.item.product
    val promotion = itemWithProduct.item.promotion
    val cartItem = itemWithProduct.cartItem

    val unitPrice = when(promotion){
        is ProductPromotion.Percent -> promotion.discountedPrice
        is ProductPromotion.BuyXPayY -> product.price
        null -> product.price
    }

    val hasDiscount = promotion is ProductPromotion.Percent
    val itemTotal = unitPrice * cartItem.quantity

    val dismissState = rememberSwipeToDismissBoxState()
    LaunchedEffect(dismissState.currentValue) {
        if(dismissState.currentValue == SwipeToDismissBoxValue.StartToEnd){
            onRemove(cartItem.productId)
            dismissState.snapTo(SwipeToDismissBoxValue.Settled)
        }
    }

    SwipeToDismissBox(
        modifier = modifier,
        state = dismissState,
        backgroundContent = {
            Box(
                Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.Delete,
                    contentDescription = "Eliminar",
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.align(Alignment.CenterStart)
                )
            }
        },
        enableDismissFromEndToStart = false,
    ) {
        Card(
            Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainerLow)
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max)
                    .padding(8.dp)
            ) {
                AsyncImage(
                    modifier = Modifier
                        .weight(1.5f)
                        .fillMaxHeight(),
                    model = product.imageUrl,
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                )
                Spacer(Modifier.width(24.dp))
                Column(
                    modifier = Modifier.weight(3f), verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (hasDiscount){
                            Text(
                                text = currencyFormatter.format(product.price),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textDecoration = TextDecoration.LineThrough
                            )
                            Text(
                                text = "${currencyFormatter.format(unitPrice)} c/u",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Text(
                                text = "${currencyFormatter.format(unitPrice)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Text(
                        text = "Total: ${currencyFormatter.format(itemTotal)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    QuantitySelector(
                        modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(8.dp)),
                        quantity = cartItem.quantity.toString(),
                        canDecrease = cartItem.quantity > 1,
                        canIncrease = cartItem.quantity < product.stock,
                        onIncreaseQuantity = { onIncreseQuantity(product.id, cartItem.quantity) },
                        onDecreaseQuantity = { onDecreseQuantity(product.id, cartItem.quantity) }
                    )
                }
            }
        }
    }
}