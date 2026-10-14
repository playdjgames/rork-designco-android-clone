package com.DesignCo.djgames.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.DesignCo.djgames.ui.AppViewModel
import com.DesignCo.djgames.ui.components.DcCard
import com.DesignCo.djgames.ui.navigation.AppNavigator
import com.DesignCo.djgames.ui.navigation.Tab
import com.DesignCo.djgames.ui.theme.DC
import com.DesignCo.djgames.util.formatPrice
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OrdersScreen(vm: AppViewModel, nav: AppNavigator) {
    val data by vm.data.collectAsStateWithLifecycle()
    val dateFormat = remember { SimpleDateFormat("MMM d, yyyy · h:mm a", Locale.US) }

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
            ScreenTopBar("Order History", onBack = nav::back)
            if (data.orders.isEmpty()) {
                EmptyState(
                    title = "No orders yet",
                    body = "Products you order for studio pickup will appear here.",
                    action = "Start shopping",
                    onAction = { nav.openShop() },
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyColumn(
                    Modifier
                        .weight(1f)
                        .navigationBarsPadding(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(data.orders, key = { it.id }) { order ->
                        DcCard(Modifier.fillMaxWidth()) {
                            Row(Modifier.padding(18.dp), verticalAlignment = Alignment.Top) {
                                Icon(Icons.Outlined.Inventory2, null, tint = DC.Gold, modifier = Modifier.size(24.dp))
                                Spacer(Modifier.width(14.dp))
                                Column(Modifier.weight(1f)) {
                                    Row {
                                        Text(
                                            "Order ${order.id}",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = DC.Ivory,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text(
                                            formatPrice(order.total),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = DC.Gold
                                        )
                                    }
                                    Text(
                                        dateFormat.format(Date(order.placedAt)),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = DC.Muted
                                    )
                                    Text(
                                        order.summary,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = DC.Ivory.copy(alpha = 0.8f),
                                        modifier = Modifier.padding(top = 6.dp)
                                    )
                                    Text(
                                        "Ready for pickup",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = DC.Gold,
                                        modifier = Modifier.padding(top = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
