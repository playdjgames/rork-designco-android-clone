package com.DesignCo.djgames.ui.screens

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.DesignCo.djgames.data.Catalog
import com.DesignCo.djgames.data.ServiceKind
import com.DesignCo.djgames.ui.AppViewModel
import com.DesignCo.djgames.ui.components.DcCard
import com.DesignCo.djgames.ui.components.GoldButton
import com.DesignCo.djgames.ui.components.RemoteImage
import com.DesignCo.djgames.ui.navigation.AppNavigator
import com.DesignCo.djgames.ui.navigation.Tab
import com.DesignCo.djgames.ui.navigation.tabBarClearance
import com.DesignCo.djgames.ui.theme.DC
import com.DesignCo.djgames.ui.theme.SerifFamily
import com.DesignCo.djgames.util.formatDuration
import com.DesignCo.djgames.util.formatPrice

/** Favorites tab: saved services and inspiration looks to share with your stylist. */
@Composable
fun FavoritesScreen(vm: AppViewModel, nav: AppNavigator) {
    val data by vm.data.collectAsStateWithLifecycle()
    val haptic = LocalHapticFeedback.current
    val favoriteServices = remember(data.favoriteServiceIds) {
        Catalog.services.filter { it.id in data.favoriteServiceIds }
    }
    val savedInspiration = remember(data.inspirationIds) {
        Catalog.inspiration.filter { it.id in data.inspirationIds }
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
                Column(Modifier.padding(top = 14.dp)) {
                    Text("Favorites", fontFamily = SerifFamily, fontSize = 40.sp, color = DC.Ivory)
                    Text(
                        "Your looks and your go-to services.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = DC.Muted,
                        modifier = Modifier.padding(top = 2.dp, bottom = 6.dp)
                    )
                }
            }

            item(span = { GridItemSpan(2) }) {
                Text(
                    "SAVED SERVICES",
                    fontSize = 13.sp,
                    letterSpacing = 3.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DC.Muted,
                    modifier = Modifier.padding(top = 10.dp, bottom = 2.dp)
                )
            }
            if (favoriteServices.isEmpty()) {
                item(span = { GridItemSpan(2) }) {
                    Text(
                        "Tap the heart on any service in Book to keep it here for one-tap rebooking.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = DC.Muted,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            } else {
                items(favoriteServices, key = { "svc-${it.id}" }) { service ->
                    FavoriteServiceCard(
                        name = service.name,
                        detail = "${formatDuration(service.durationMinutes)} · ${formatPrice(service.price)}",
                        onBook = {
                            vm.setBookingKind(service.kind)
                            nav.openTab(Tab.Book)
                        },
                        onRemove = {
                            haptic.performHapticFeedback(HapticFeedbackType.SegmentTick)
                            vm.toggleServiceFavorite(service.id)
                        }
                    )
                }
            }

            item(span = { GridItemSpan(2) }) {
                Text(
                    "INSPIRATION",
                    fontSize = 13.sp,
                    letterSpacing = 3.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DC.Muted,
                    modifier = Modifier.padding(top = 14.dp, bottom = 2.dp)
                )
            }
            if (savedInspiration.isEmpty()) {
                item(span = { GridItemSpan(2) }) {
                    Column(Modifier.padding(vertical = 8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.PhotoLibrary, null, tint = DC.Gold, modifier = Modifier.size(28.dp))
                            Spacer(Modifier.width(12.dp))
                            Text("No saved looks yet", style = MaterialTheme.typography.titleMedium, color = DC.Ivory)
                        }
                        Text(
                            "Save the looks you love and share them privately with your stylist before your visit.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = DC.Muted,
                            modifier = Modifier.padding(top = 6.dp, bottom = 14.dp)
                        )
                        GoldButton(text = "Browse inspiration", onClick = { nav.openTab(Tab.Book) })
                    }
                }
            } else {
                items(savedInspiration, key = { it.id }) { item ->
                    InspirationCard(
                        title = item.title,
                        imageUrl = item.imageUrl,
                        shared = item.id in data.sharedInspirationIds,
                        onShare = { vm.shareInspiration(item.id) },
                        onRemove = {
                            haptic.performHapticFeedback(HapticFeedbackType.SegmentTick)
                            vm.toggleInspiration(item.id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun FavoriteServiceCard(name: String, detail: String, onBook: () -> Unit, onRemove: () -> Unit) {
    val shape = RoundedCornerShape(18.dp)
    Column(
        Modifier
            .clip(shape)
            .background(DC.Surface)
            .border(1.dp, DC.Border, shape)
            .clickable(onClick = onBook)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Favorite, null, tint = DC.Gold, modifier = Modifier.size(18.dp))
            Spacer(Modifier.weight(1f))
            Icon(
                Icons.Outlined.FavoriteBorder,
                contentDescription = "Remove from favorites",
                tint = DC.Muted,
                modifier = Modifier
                    .size(20.dp)
                    .clickable(onClick = onRemove)
            )
        }
        Spacer(Modifier.height(10.dp))
        Text(
            name,
            style = MaterialTheme.typography.titleMedium,
            color = DC.Ivory,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(detail, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = DC.Gold)
        Spacer(Modifier.height(12.dp))
        Box(
            Modifier
                .clip(CircleShape)
                .background(DC.SurfaceHigh)
                .border(1.dp, DC.Border, CircleShape)
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Text("Book again", style = MaterialTheme.typography.labelMedium, color = DC.Ivory)
        }
    }
}

@Composable
private fun InspirationCard(
    title: String,
    imageUrl: String,
    shared: Boolean,
    onShare: () -> Unit,
    onRemove: () -> Unit
) {
    val shape = RoundedCornerShape(18.dp)
    Column(
        Modifier
            .clip(shape)
            .background(DC.Surface)
            .border(1.dp, DC.Border, shape)
    ) {
        Box {
            RemoteImage(
                url = imageUrl,
                contentDescription = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.1f)
            )
            Box(
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
                    .clip(CircleShape)
                    .background(DC.Canvas.copy(alpha = 0.75f))
                    .clickable(onClick = onRemove)
                    .padding(8.dp)
            ) {
                Icon(
                    Icons.Filled.Favorite,
                    contentDescription = "Remove look",
                    tint = DC.Gold,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Column(Modifier.padding(14.dp)) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                color = DC.Ivory,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(8.dp))
            Box(
                Modifier
                    .clip(CircleShape)
                    .background(if (shared) SolidColor(DC.SurfaceHigh) else DC.GoldGradient)
                    .clickable(enabled = !shared, onClick = onShare)
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (shared) Icons.Filled.Check else Icons.Outlined.Share,
                        null,
                        tint = if (shared) DC.Muted else DC.OnGold,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        if (shared) "Shared" else "Share with stylist",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (shared) DC.Muted else DC.OnGold
                    )
                }
            }
        }
    }
}
