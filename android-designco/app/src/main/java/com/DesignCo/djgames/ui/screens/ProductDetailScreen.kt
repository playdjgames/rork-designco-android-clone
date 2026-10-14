package com.DesignCo.djgames.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.DesignCo.djgames.data.Catalog
import com.DesignCo.djgames.ui.AppViewModel
import com.DesignCo.djgames.ui.components.CircleIconButton
import com.DesignCo.djgames.ui.components.CrownIcon
import com.DesignCo.djgames.ui.components.GoldButton
import com.DesignCo.djgames.ui.components.GoldPill
import com.DesignCo.djgames.ui.components.QuantityStepper
import com.DesignCo.djgames.ui.components.RemoteImage
import com.DesignCo.djgames.ui.components.SectionHeader
import com.DesignCo.djgames.ui.navigation.AppNavigator
import com.DesignCo.djgames.ui.theme.DC
import com.DesignCo.djgames.ui.theme.SerifFamily
import com.DesignCo.djgames.util.formatPrice

@Composable
fun ProductDetailScreen(productId: String, vm: AppViewModel, nav: AppNavigator) {
    val data by vm.data.collectAsStateWithLifecycle()
    val product = remember(productId) { Catalog.product(productId) }
    var quantity by rememberSaveable { mutableIntStateOf(1) }

    if (product == null) {
        Box(
            Modifier
                .fillMaxSize()
                .background(DC.Canvas),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("This product is no longer available", color = DC.Ivory)
                Spacer(Modifier.height(16.dp))
                GoldButton("Back to shop", onClick = nav::back, modifier = Modifier.width(220.dp))
            }
        }
        return
    }

    val related = remember(product) {
        Catalog.products.filter { it.id != product.id && it.category == product.category }
            .ifEmpty { Catalog.products.filter { it.id != product.id } }
            .take(4)
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(DC.Canvas)
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 140.dp)
        ) {
            Box {
                RemoteImage(
                    product.imageUrl,
                    product.name,
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(0.95f)
                )
                Box(
                    Modifier
                        .matchParentSize()
                        .background(
                            Brush.verticalGradient(
                                0f to Color.Black.copy(alpha = 0.45f),
                                0.25f to Color.Transparent,
                                0.75f to Color.Transparent,
                                1f to DC.Canvas
                            )
                        )
                )
            }
            Column(Modifier.padding(horizontal = 24.dp)) {
                if (product.isNew) {
                    GoldPill("NEW ARRIVAL")
                    Spacer(Modifier.height(12.dp))
                }
                Text(
                    product.category.label.uppercase(),
                    fontSize = 12.sp,
                    letterSpacing = 3.sp,
                    color = DC.Gold,
                    fontWeight = FontWeight.SemiBold
                )
                Text(product.name, fontFamily = SerifFamily, fontSize = 36.sp, lineHeight = 42.sp, color = DC.Ivory)
                Text(product.tagline, style = MaterialTheme.typography.bodyLarge, color = DC.Muted)
                Spacer(Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(formatPrice(product.price), fontSize = 28.sp, fontWeight = FontWeight.Bold, color = DC.Gold)
                    Spacer(Modifier.width(12.dp))
                    Text(product.size, style = MaterialTheme.typography.bodyMedium, color = DC.Muted, modifier = Modifier.padding(bottom = 5.dp))
                }
                if (data.isMember) {
                    Row(Modifier.padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(CrownIcon, null, tint = DC.Gold, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "Member price ${formatPrice(product.price * (1 - Catalog.MEMBER_DISCOUNT))} at checkout",
                            style = MaterialTheme.typography.bodyMedium,
                            color = DC.Ivory
                        )
                    }
                }
                Spacer(Modifier.height(22.dp))
                HorizontalDivider(color = DC.Divider)
                Spacer(Modifier.height(18.dp))
                Text("About", style = MaterialTheme.typography.titleMedium, color = DC.Ivory)
                Text(
                    product.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = DC.Ivory.copy(alpha = 0.75f),
                    modifier = Modifier.padding(top = 6.dp)
                )
                Spacer(Modifier.height(18.dp))
                Text("How to use", style = MaterialTheme.typography.titleMedium, color = DC.Ivory)
                Text(
                    product.howToUse,
                    style = MaterialTheme.typography.bodyLarge,
                    color = DC.Ivory.copy(alpha = 0.75f),
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
            SectionHeader(
                "You may also like",
                modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 30.dp, bottom = 12.dp)
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(related, key = { it.id }) { p ->
                    ProductTile(
                        product = p,
                        onOpen = { nav.openProduct(p.id) },
                        onAdd = { vm.addToCart(p) },
                        modifier = Modifier.width(180.dp)
                    )
                }
            }
        }

        Row(
            Modifier
                .statusBarsPadding()
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircleIconButton(Icons.AutoMirrored.Filled.ArrowBack, "Back", nav::back, size = 48.dp)
            Spacer(Modifier.weight(1f))
            CircleIconButton(Icons.Outlined.ShoppingBag, "Shopping bag", nav::openCart, badgeCount = data.cartCount, size = 48.dp)
        }

        Row(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Color.Transparent, DC.Canvas, DC.Canvas)))
                .navigationBarsPadding()
                .padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            QuantityStepper(quantity, onChange = { quantity = it.coerceIn(1, 20) })
            Spacer(Modifier.width(14.dp))
            GoldButton(
                "Add · ${formatPrice(product.price * quantity)}",
                onClick = {
                    vm.addToCart(product, quantity)
                    quantity = 1
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}
