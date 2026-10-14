package com.DesignCo.djgames.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.ContentCut
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.DesignCo.djgames.data.BeautyService
import com.DesignCo.djgames.data.Catalog
import com.DesignCo.djgames.data.ServiceKind
import com.DesignCo.djgames.ui.AppViewModel
import com.DesignCo.djgames.ui.navigation.AppNavigator
import com.DesignCo.djgames.ui.navigation.tabBarClearance
import com.DesignCo.djgames.ui.theme.DC
import com.DesignCo.djgames.ui.theme.SerifFamily
import com.DesignCo.djgames.util.formatDuration
import com.DesignCo.djgames.util.formatPrice

/** The full service menu. Tapping a service opens the Find-a-time sheet. */
@Composable
fun BookScreen(vm: AppViewModel, nav: AppNavigator) {
    val data by vm.data.collectAsStateWithLifecycle()
    val kind by vm.bookingKind.collectAsStateWithLifecycle()
    val haptic = LocalHapticFeedback.current

    var sheetServiceId by rememberSaveable { mutableStateOf<String?>(null) }
    val services = remember(kind) { Catalog.services(kind) }
    val sheetService = Catalog.services.firstOrNull { it.id == sheetServiceId }

    Column(
        Modifier
            .fillMaxSize()
            .background(DC.CanvasGradient)
            .statusBarsPadding()
    ) {
        Column(Modifier.padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(14.dp))
            Text("Book", fontFamily = SerifFamily, fontSize = 40.sp, color = DC.Ivory)
            Text(
                "Your usual, or something new.",
                style = MaterialTheme.typography.bodyLarge,
                color = DC.Muted,
                modifier = Modifier.padding(top = 2.dp, bottom = 14.dp)
            )
            Text(
                "FULL MENU",
                fontSize = 13.sp,
                letterSpacing = 3.sp,
                fontWeight = FontWeight.SemiBold,
                color = DC.Muted,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            KindToggle(
                selected = kind,
                onSelect = {
                    haptic.performHapticFeedback(HapticFeedbackType.SegmentTick)
                    vm.setBookingKind(it)
                }
            )
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = tabBarClearance())
        ) {
            itemsIndexed(services, key = { _, s -> s.id }) { index, service ->
                ServiceMenuCard(
                    service = service,
                    isFavorite = service.id in data.favoriteServiceIds,
                    showDivider = index < services.lastIndex,
                    onOpen = {
                        haptic.performHapticFeedback(HapticFeedbackType.SegmentTick)
                        sheetServiceId = service.id
                    },
                    onToggleFavorite = {
                        haptic.performHapticFeedback(HapticFeedbackType.SegmentTick)
                        vm.toggleServiceFavorite(service.id)
                    }
                )
            }
        }
    }

    sheetService?.let { service ->
        FindTimeSheet(
            service = service,
            vm = vm,
            isMember = data.isMember,
            waitlistedDays = data.waitlist.map { it.dayStartMillis }.toSet(),
            onDismiss = { sheetServiceId = null },
            onBooked = { sheetServiceId = null }
        )
    }
}

/** Salon / Barber animated segmented toggle. */
@Composable
internal fun KindToggle(selected: ServiceKind, onSelect: (ServiceKind) -> Unit, modifier: Modifier = Modifier) {
    val shape = CircleShape
    BoxWithConstraints(
        modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(shape)
            .background(DC.Surface)
            .border(1.dp, DC.Border, shape)
            .padding(6.dp)
    ) {
        val half = maxWidth / 2
        val offset by animateDpAsState(if (selected == ServiceKind.Salon) 0.dp else half, label = "toggle")
        Box(
            Modifier
                .offset(x = offset)
                .width(half)
                .fillMaxHeight()
                .clip(shape)
                .background(DC.GoldGradient)
        )
        Row(Modifier.fillMaxSize()) {
            ServiceKind.entries.forEach { k ->
                val isSel = k == selected
                val color by animateColorAsState(if (isSel) DC.OnGold else DC.Ivory, label = "toggleText")
                Row(
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(shape)
                        .clickable { onSelect(k) },
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        if (k == ServiceKind.Salon) Icons.Outlined.ContentCut else Icons.Outlined.Face,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(k.label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = color)
                }
            }
        }
    }
}

@Composable
private fun ServiceMenuCard(
    service: BeautyService,
    isFavorite: Boolean,
    showDivider: Boolean,
    onOpen: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    Column(Modifier.background(DC.Surface, RoundedCornerShape(18.dp))) {
        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(DC.Surface)
                .clickable(onClick = onOpen)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(DC.SurfaceHigh),
                contentAlignment = Alignment.Center
            ) {
                Icon(service.icon, null, tint = DC.Gold, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    service.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 17.sp,
                    color = DC.Ivory,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    service.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = DC.Muted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    "${formatDuration(service.durationMinutes)} · ${formatPrice(service.price)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = DC.Gold
                )
            }
            Icon(
                if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = if (isFavorite) "Remove from favorites" else "Save to favorites",
                tint = if (isFavorite) DC.Gold else DC.Muted,
                modifier = Modifier
                    .size(24.dp)
                    .clickable(onClick = onToggleFavorite)
                    .padding(2.dp)
            )
        }
        if (showDivider) {
            HorizontalDivider(Modifier.padding(start = 74.dp), thickness = 1.dp, color = DC.Divider)
        }
    }
}
