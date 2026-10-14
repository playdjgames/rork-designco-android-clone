package com.DesignCo.djgames.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.DesignCo.djgames.data.Catalog
import com.DesignCo.djgames.data.Order
import com.DesignCo.djgames.ui.AppViewModel
import com.DesignCo.djgames.ui.components.CircleIconButton
import com.DesignCo.djgames.ui.components.CrownIcon
import com.DesignCo.djgames.ui.components.DcBottomSheet
import com.DesignCo.djgames.ui.components.DcCard
import com.DesignCo.djgames.ui.components.GoldButton
import com.DesignCo.djgames.ui.components.QuantityStepper
import com.DesignCo.djgames.ui.components.RemoteImage
import com.DesignCo.djgames.ui.navigation.AppNavigator
import com.DesignCo.djgames.ui.navigation.Tab
import com.DesignCo.djgames.ui.theme.DC
import com.DesignCo.djgames.ui.theme.SerifFamily
import com.DesignCo.djgames.util.formatPrice

@Composable
fun CartScreen(vm: AppViewModel, nav: AppNavigator) {
    val data by vm.data.collectAsStateWithLifecycle()
    var placed by remember { mutableStateOf<Order?>(null) }
    val lines = remember(data.cart) {
        data.cart.mapNotNull { (id, qty) -> Catalog.product(id)?.let { it to qty } }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(DC.CanvasGradient)
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            ScreenTopBar("Your Bag", onBack = nav::back)
            if (lines.isEmpty()) {
                EmptyState(
                    title = "Your bag is empty",
                    body = "Discover serums, oils and grooming essentials crafted for the studio.",
                    action = "Browse the shop",
                    onAction = { nav.openShop() },
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyColumn(
                    Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(lines, key = { it.first.id }) { (product, qty) ->
                        DcCard(Modifier.fillMaxWidth()) {
                            Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                RemoteImage(
                                    product.imageUrl,
                                    product.name,
                                    Modifier
                                        .size(84.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .clickable { nav.openProduct(product.id) }
                                )
                                Spacer(Modifier.width(14.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(product.name, style = MaterialTheme.typography.titleMedium, color = DC.Ivory)
                                    Text(
                                        formatPrice(product.price * qty),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = DC.Gold
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    QuantityStepper(qty, onChange = { vm.setQuantity(product.id, it) })
                                }
                                IconButton(onClick = { vm.setQuantity(product.id, 0) }) {
                                    Icon(Icons.Outlined.DeleteOutline, "Remove ${product.name}", tint = DC.Muted)
                                }
                            }
                        }
                    }
                    item {
                        if (!data.isMember) {
                            DcCard(
                                Modifier.fillMaxWidth(),
                                onClick = nav::openClub
                            ) {
                                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(CrownIcon, null, tint = DC.Gold, modifier = Modifier.size(22.dp))
                                    Spacer(Modifier.width(12.dp))
                                    Text(
                                        "Club members save ${formatPrice(data.subtotal * Catalog.MEMBER_DISCOUNT)} on this order",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = DC.Ivory,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text("Join", color = DC.Gold, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                    item {
                        DcCard(Modifier.fillMaxWidth()) {
                            SummaryLine("Subtotal", formatPrice(data.subtotal))
                            if (data.isMember) SummaryLine("Club discount (10%)", "−${formatPrice(data.discount)}")
                            SummaryLine("Pickup at studio", "Free")
                            HorizontalDivider(color = DC.Divider)
                            SummaryLine("Total", formatPrice(data.total), emphasize = true)
                        }
                    }
                }
                Box(
                    Modifier
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    GoldButton("Place order · ${formatPrice(data.total)}", onClick = { placed = vm.checkout() })
                }
            }
        }
    }

    placed?.let { order ->
        DcBottomSheet(onDismiss = { placed = null }) {
            Column(
                Modifier.padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(DC.GoldGradient),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.CheckCircle, null, tint = DC.OnGold, modifier = Modifier.size(34.dp))
                }
                Text("Order placed", fontFamily = SerifFamily, fontSize = 30.sp, color = DC.Ivory, modifier = Modifier.padding(top = 14.dp))
                Text(
                    "Order ${order.id} · ${formatPrice(order.total)}\nReady for pickup at the studio within 2 hours.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = DC.Muted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
                )
                GoldButton("Done", onClick = {
                    placed = null
                    nav.back()
                })
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

/** Back button + serif title used on detail screens. */
@Composable
internal fun ScreenTopBar(title: String, onBack: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircleIconButton(Icons.AutoMirrored.Filled.ArrowBack, "Back", onBack, size = 48.dp)
        Spacer(Modifier.width(16.dp))
        Text(title, fontFamily = SerifFamily, fontSize = 30.sp, color = DC.Ivory)
    }
}

@Composable
internal fun EmptyState(
    title: String,
    body: String,
    action: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            Modifier
                .size(88.dp)
                .clip(CircleShape)
                .background(DC.Surface),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Outlined.ShoppingBag, null, tint = DC.Gold, modifier = Modifier.size(38.dp))
        }
        Spacer(Modifier.height(18.dp))
        Text(title, fontFamily = SerifFamily, fontSize = 26.sp, color = DC.Ivory, textAlign = TextAlign.Center)
        Text(
            body,
            style = MaterialTheme.typography.bodyLarge,
            color = DC.Muted,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
        )
        GoldButton(action, onClick = onAction)
    }
}
