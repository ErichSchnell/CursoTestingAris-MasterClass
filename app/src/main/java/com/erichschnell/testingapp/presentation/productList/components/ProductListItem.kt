package com.erichschnell.testingapp.presentation.productList.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.erichschnell.testingapp.R
import com.erichschnell.testingapp.domain.models.ProductPromotion
import com.erichschnell.testingapp.domain.models.ProductWithPromotion
import com.erichschnell.testingapp.presentation.productList.models.ProductListStr
import com.erichschnell.testingapp.presentation.productList.models.ProductListTestTags
import java.util.Locale

@Composable
fun ProductListEmpty(modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("🔍", style = MaterialTheme.typography.displayMedium)
            Text(
                ProductListStr.EMPTY_CART,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.tertiary,
            )
        }
    }
}

@Composable
fun ProductListItems(
    products: List<ProductWithPromotion>,
    onClick: (ProductWithPromotion) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.testTag(ProductListTestTags.PRODUCTS_LIST),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(products) { product ->
            ProductItem(product) {
                onClick(product)
            }
        }
    }
}

@Composable
private fun ProductItem(
    item: ProductWithPromotion,
    onClick: () -> Unit,
) {
    val product = item.product
    val promotion = item.promotion
    val promoBadge =
        when (promotion) {
            is ProductPromotion.BuyXPayY -> promotion.label
            is ProductPromotion.Percent -> promotion.label
            null -> null
        }

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .testTag(ProductListTestTags.productListProductsWithPromotion(product.id))
                .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(
                modifier =
                    Modifier
                        .size(88.dp)
                        .clip(RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center,
            ) {
                if (!item.product.imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = product.imageUrl,
                        placeholder = painterResource(R.drawable.ic_launcher_foreground),
                        contentDescription = product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize(),
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.Image,
                        contentDescription = product.name,
                        modifier = Modifier.size(33.dp),
                    )
                }

                if (promoBadge != null) {
                    Box(
                        modifier =
                            Modifier
                                .align(Alignment.TopStart)
                                .padding(16.dp)
                                .background(
                                    MaterialTheme.colorScheme.error,
                                    RoundedCornerShape(4.dp),
                                ).padding(horizontal = 6.dp, 2.dp),
                    ) {
                        Text(
                            promoBadge,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                if (product.description.isNotBlank()) {
                    Text(
                        product.description,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (promotion is ProductPromotion.Percent) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Text(
                                    "Antes",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Text(
                                    text = String.format(Locale.getDefault(), "%.2f", product.price),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textDecoration = TextDecoration.LineThrough,
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                Text(
                                    "Ahora",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Text(
                                    text = String.format(Locale.getDefault(), "%.2f", promotion.discountedPrice),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            }
                        }
                    } else {
                        Text(
                            text = String.format(Locale.getDefault(), "%.2f", product.price),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}
