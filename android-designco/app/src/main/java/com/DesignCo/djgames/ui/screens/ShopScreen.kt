package com.DesignCo.djgames.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.DesignCo.djgames.data.Catalog
import com.DesignCo.djgames.data.Product
import com.DesignCo.djgames.data.ProductCategory
import com.DesignCo.djgames.ui.AppViewModel
import com.DesignCo.djgames.ui.components.CircleIconButton
import com.DesignCo.djgames.ui.components.GoldPill
import com.DesignCo.djgames.ui.components.RemoteImage
import com.DesignCo.djgames.ui.components.dcFieldColors
import com.DesignCo.djgames.ui.navigation.AppNavigator
import com.DesignCo.djgames.ui.navigation.tabBarClearance
import com.DesignCo.djgames.ui.theme.DC
import com.DesignCo.djgames.util.formatPrice
import kotlinx.coroutines.delay

private enum class SortOrder(val label: String) {
    Featured("Featured"),
    PriceLow("Price: low to high"),
    PriceHigh("Price: high to low"),
    Name("Name A–Z")
}

/** Shop, pushed from Home or Profile: search, categories, sort and the product grid. */
@Composable
fun ShopScreen(vm: AppViewModel, nav: AppNavigator) {
    val data by vm.data.collectAsStateWithLifecycle()
    var query by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf<ProductCategory?>(null) }
    var sort by rememberSaveable { mutableStateOf(SortOrder.Featured) }
    var menuOpen by remember { mutableStateOf(false) }
    val focus = LocalFocusManager.current

    val products = remember(query, category, sort) {
        val q = query.trim()
        Catalog.products
            .filter { category == null || it.category == category }
            .filter { q.isEmpty() || it.name.contains(q, true) || it.tagline.contains(q, true) || it.category.label.contains(q, true) }
            .let { list ->
                when (sort) {
                    SortOrder.Featured -> list
                    SortOrder.PriceLow -> list.sortedBy { it.price }
                    SortOrder.PriceHigh -> list.sortedByDescending { it.price }
                    SortOrder.Name -> list.sortedBy { it.name }
                }
            }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(DC.CanvasGradient)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = tabBarClearance()),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item(span = { GridItemSpan(2) }) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircleIconButton(
                        icon = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Back",
                        onClick = nav::back,
                        size = 46.dp
                    )
                    Text(
                        "Shop",
                        style = MaterialTheme.typography.titleMedium,
                        color = DC.Ivory,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box {
                            CircleIconButton(
                                icon = Icons.Outlined.MoreHoriz,
                                contentDescription = "Sort",
                                onClick = { menuOpen = true },
                                size = 46.dp
                            )
                            DropdownMenu(
                                expanded = menuOpen,
                                onDismissRequest = { menuOpen = false },
                                containerColor = DC.SurfaceHigh
                            ) {
                                SortOrder.entries.forEach { s ->
                                    DropdownMenuItem(
                                        text = { Text(s.label, color = DC.Ivory) },
                                        trailingIcon = {
                                            if (s == sort) Icon(Icons.Filled.Check, null, tint = DC.Gold)
                                        },
                                        onClick = {
                                            sort = s
                                            menuOpen = false
                                        }
                                    )
                                }
                                DropdownMenuItem(
                                    text = { Text("Order history", color = DC.Ivory) },
                                    onClick = {
                                        menuOpen = false
                                        nav.openOrders()
                                    }
                                )
                            }
                        }
                        CircleIconButton(
                            icon = Icons.Outlined.ShoppingBag,
                            contentDescription = "Shopping bag",
                            onClick = nav::openCart,
                            badgeCount = data.cartCount,
                            size = 46.dp
                        )
                    }
                }
            }
            item(span = { GridItemSpan(2) }) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    placeholder = { Text("Search products...") },
                    leadingIcon = { Icon(Icons.Outlined.Search, null) },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { query = "" }) {
                                Icon(Icons.Filled.Close, "Clear search", tint = DC.Muted)
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp),
                    colors = dcFieldColors(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = androidx.compose.foundation.text.KeyboardActions(onSearch = { focus.clearFocus() })
                )
            }
            item(span = { GridItemSpan(2) }) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    item { CategoryChip("All", category == null) { category = null } }
                    items(ProductCategory.entries) { c ->
                        CategoryChip(c.label, category == c) { category = c }
                    }
                }
            }
            item(span = { GridItemSpan(2) }) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(DC.Surface)
                        .border(1.dp, DC.Border, RoundedCornerShape(16.dp))
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.Info, null, tint = DC.Gold, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "Online checkout is coming soon. You can still browse and ask about products at your next visit.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = DC.Muted
                    )
                }
            }
            if (products.isEmpty()) {
                item(span = { GridItemSpan(2) }) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Outlined.Search, null, tint = DC.Gold, modifier = Modifier.size(40.dp))
                        Spacer(Modifier.height(12.dp))
                        Text("Nothing matches \"$query\"", style = MaterialTheme.typography.titleMedium, color = DC.Ivory)
                        Text(
                            "Try another product or category.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = DC.Muted,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            items(products, key = { it.id }) { product ->
                ProductTile(
                    product = product,
                    onOpen = { nav.openProduct(product.id) },
                    onAdd = { vm.addToCart(product) }
                )
            }
        }
    }
}

@Composable
private fun CategoryChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val haptic = LocalHapticFeedback.current
    FilterChip(
        selected = selected,
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.SegmentTick)
            onClick()
        },
        label = {
            Text(
                label,
                fontSize = 16.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp)
            )
        },
        shape = CircleShape,
        colors = FilterChipDefaults.filterChipColors(
            containerColor = DC.Surface,
            labelColor = DC.Ivory,
            selectedContainerColor = DC.Gold,
            selectedLabelColor = DC.OnGold
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = DC.Border,
            selectedBorderColor = DC.Gold
        )
    )
}

@Composable
internal fun ProductTile(product: Product, onOpen: () -> Unit, onAdd: () -> Unit, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(20.dp)
    val haptic = LocalHapticFeedback.current
    var added by remember { mutableStateOf(false) }
    val pop by animateFloatAsState(if (added) 1.15f else 1f, label = "addPop")
    LaunchedEffect(added) {
        if (added) {
            delay(900)
            added = false
        }
    }
    Column(
        modifier
            .clip(shape)
            .background(DC.Surface)
            .border(1.dp, DC.Border, shape)
            .clickable(onClick = onOpen)
    ) {
        Box {
            RemoteImage(
                product.imageUrl,
                product.name,
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.05f)
            )
            if (product.isNew) {
                GoldPill("NEW", Modifier.padding(12.dp))
            }
        }
        Row(
            Modifier.padding(start = 16.dp, end = 10.dp, top = 12.dp, bottom = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    product.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = DC.Ivory,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    formatPrice(product.price),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = DC.Gold
                )
            }
            Box(
                Modifier
                    .size(46.dp)
                    .graphicsLayer {
                        scaleX = pop
                        scaleY = pop
                    }
                    .clip(CircleShape)
                    .background(if (added) DC.Gold else DC.Surface)
                    .border(1.5.dp, DC.Gold, CircleShape)
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                        added = true
                        onAdd()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (added) Icons.Filled.Check else Icons.Filled.Add,
                    contentDescription = "Add ${product.name} to bag",
                    tint = if (added) DC.OnGold else DC.Gold
                )
            }
        }
    }
}
