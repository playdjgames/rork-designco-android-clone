package com.DesignCo.djgames.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EventAvailable
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.ShoppingBag
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.DesignCo.djgames.data.AppData
import com.DesignCo.djgames.data.Appointment
import com.DesignCo.djgames.data.ServiceKind
import com.DesignCo.djgames.ui.AppViewModel
import com.DesignCo.djgames.ui.components.CircleIconButton
import com.DesignCo.djgames.ui.components.CrownIcon
import com.DesignCo.djgames.ui.components.DcBottomSheet
import com.DesignCo.djgames.ui.components.DcCard
import com.DesignCo.djgames.ui.components.GoldButton
import com.DesignCo.djgames.ui.navigation.AppNavigator
import com.DesignCo.djgames.ui.navigation.Tab
import com.DesignCo.djgames.ui.navigation.tabBarClearance
import com.DesignCo.djgames.ui.theme.DC
import com.DesignCo.djgames.ui.theme.SerifFamily
import java.util.Calendar

/** Home: greeting, next visit, membership card and stylist finder — mirroring iOS. */
@Composable
fun HomeScreen(vm: AppViewModel, nav: AppNavigator) {
    val data by vm.data.collectAsStateWithLifecycle()
    var showNotifications by remember { mutableStateOf(false) }
    val upcoming = remember(data.appointments) { data.upcomingAppointments(System.currentTimeMillis()) }

    Box(
        Modifier
            .fillMaxSize()
            .background(DC.CanvasGradient)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            contentPadding = PaddingValues(bottom = tabBarClearance())
        ) {
            item {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 20.dp, top = 18.dp)
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            greeting(),
                            style = MaterialTheme.typography.bodyLarge,
                            color = DC.Muted
                        )
                        Text(
                            data.user?.name?.substringBefore(' ')?.ifBlank { null } ?: "Beautiful",
                            fontFamily = SerifFamily,
                            fontSize = 38.sp,
                            color = DC.Ivory
                        )
                    }
                    CircleIconButton(
                        icon = Icons.Outlined.NotificationsNone,
                        contentDescription = "Notifications",
                        onClick = { showNotifications = true },
                        showDot = upcoming.isNotEmpty() || !data.isMember
                    )
                }
            }
            item {
                NextVisitCard(
                    next = upcoming.firstOrNull(),
                    onBook = {
                        vm.setBookingKind(ServiceKind.Salon)
                        nav.openTab(Tab.Book)
                    },
                    onOpen = { nav.openTab(Tab.Appointments) }
                )
            }
            item {
                MemberCard(
                    data = data,
                    onSeePerks = nav::openClub,
                    onUpgrade = nav::openClub
                )
            }
            item {
                FindStylistCard(onBrowse = { nav.openTab(Tab.Book) })
            }
            item {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                ) {
                    QuickLink(Icons.Outlined.Face, "Book Barber", Modifier.weight(1f)) {
                        vm.setBookingKind(ServiceKind.Barber)
                        nav.openTab(Tab.Book)
                    }
                    Spacer(Modifier.width(12.dp))
                    QuickLink(Icons.Outlined.ShoppingBag, "Shop", Modifier.weight(1f), onClick = nav::openShop)
                }
            }
        }
    }

    if (showNotifications) {
        NotificationsSheet(
            data = data,
            upcoming = upcoming,
            onDismiss = { showNotifications = false },
            onOpenClub = {
                showNotifications = false
                nav.openClub()
            },
            onOpenAppointments = {
                showNotifications = false
                nav.openTab(Tab.Appointments)
            }
        )
    }
}

private fun greeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 5..11 -> "Good morning,"
        in 12..16 -> "Good afternoon,"
        in 17..20 -> "Good evening,"
        else -> "Up late?" // The salon never judges a late-night rebook.
    }
}

@Composable
private fun NextVisitCard(next: Appointment?, onBook: () -> Unit, onOpen: () -> Unit) {
    DcCard(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .fillMaxWidth(),
        borderColor = if (next != null) DC.Gold.copy(alpha = 0.6f) else DC.Border,
        onClick = if (next != null) onOpen else null
    ) {
        Column(Modifier.padding(20.dp)) {
            Box(
                Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(DC.SurfaceHigh),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.EventAvailable, null, tint = DC.Gold, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.height(12.dp))
            if (next == null) {
                Text("Nothing booked yet.", fontFamily = SerifFamily, fontSize = 26.sp, color = DC.Ivory)
                Text(
                    "Your next visit will live right here.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = DC.Muted,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )
                GoldButton("Book an appointment", onClick = onBook)
            } else {
                Text("Your next visit", style = MaterialTheme.typography.labelMedium, color = DC.Gold)
                Text(
                    "${next.serviceName} · ${next.dayLabel}, ${next.timeLabel}",
                    style = MaterialTheme.typography.titleMedium,
                    color = DC.Ivory,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                )
                Text("with ${next.stylistName}", style = MaterialTheme.typography.bodyMedium, color = DC.Muted)
            }
        }
    }
}

@Composable
private fun MemberCard(data: AppData, onSeePerks: () -> Unit, onUpgrade: () -> Unit) {
    val plan = data.clubPlan
    val user = data.user
    DcCard(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .fillMaxWidth(),
        onClick = onSeePerks
    ) {
        Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    if (plan != null) "DESIGNCO MEMBER" else "DESIGNCO GUEST",
                    fontSize = 12.sp,
                    letterSpacing = 2.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DC.Gold
                )
                Text(
                    user?.name ?: "Emma Carter",
                    style = MaterialTheme.typography.titleLarge,
                    color = DC.Ivory,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    when {
                        plan != null -> "${plan.label} plan · every perk is active"
                        user != null && data.clubSince != null -> "Member since ${memberSinceLabel(data.clubSince)}\nUpgrade to the Beauty Club to book salon visits 14 days earlier."
                        else -> "Upgrade to the Beauty Club to book salon visits 14 days earlier."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = DC.Muted,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            if (plan == null) {
                Text(
                    "See perks",
                    style = MaterialTheme.typography.labelLarge,
                    color = DC.Gold,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable(onClick = onUpgrade)
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                )
            } else {
                Icon(CrownIcon, null, tint = DC.Gold, modifier = Modifier.size(28.dp))
            }
        }
    }
}

private fun memberSinceLabel(millis: Long?): String {
    if (millis == null) return "Oct 2026"
    val cal = Calendar.getInstance().apply { timeInMillis = millis }
    val month = java.text.SimpleDateFormat("MMM yyyy", java.util.Locale.US)
    return month.format(cal.time)
}

@Composable
private fun FindStylistCard(onBrowse: () -> Unit) {
    DcCard(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .fillMaxWidth()
    ) {
        Column(Modifier.padding(20.dp)) {
            Box(
                Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(DC.SurfaceHigh),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Face, null, tint = DC.Gold, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.height(12.dp))
            Text("Find your stylist.", fontFamily = SerifFamily, fontSize = 26.sp, color = DC.Ivory)
            Text(
                "Save a favorite and booking with them becomes one tap.",
                style = MaterialTheme.typography.bodyLarge,
                color = DC.Muted,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )
            GoldButton("Browse stylists", onClick = onBrowse)
        }
    }
}

@Composable
private fun QuickLink(icon: ImageVector, label: String, modifier: Modifier, onClick: () -> Unit) {
    val shape = RoundedCornerShape(18.dp)
    Row(
        modifier = modifier
            .height(60.dp)
            .clip(shape)
            .background(DC.Surface)
            .border(1.dp, DC.Border, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = DC.Gold, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(10.dp))
        Text(label, style = MaterialTheme.typography.titleMedium, color = DC.Ivory, maxLines = 1)
    }
}

@Composable
private fun NotificationsSheet(
    data: AppData,
    upcoming: List<Appointment>,
    onDismiss: () -> Unit,
    onOpenClub: () -> Unit,
    onOpenAppointments: () -> Unit
) {
    DcBottomSheet(onDismiss = onDismiss) {
        Text(
            "Notifications",
            style = MaterialTheme.typography.headlineSmall,
            color = DC.Ivory,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
        )
        data.waitlist.forEach { entry ->
            NotificationRow(
                icon = Icons.Outlined.NotificationsNone,
                title = if (entry.spotOpen) "A spot opened on ${entry.dayLabel}" else "Waitlist: ${entry.dayLabel}",
                body = if (entry.spotOpen) "Claim it from Appointments before it's gone." else "We'll alert you the moment a spot opens.",
                onClick = onOpenAppointments
            )
        }
        upcoming.forEach { appt ->
            NotificationRow(
                icon = Icons.Outlined.EventAvailable,
                title = "Upcoming: ${appt.serviceName}",
                body = "${appt.dayLabel} at ${appt.timeLabel} with ${appt.stylistName}",
                onClick = onOpenAppointments
            )
        }
        if (!data.isMember) {
            NotificationRow(
                icon = CrownIcon,
                title = "Join the Beauty Club",
                body = "Book 14 days earlier, plus a birthday treatment and early access.",
                onClick = onOpenClub
            )
        }
        NotificationRow(
            icon = Icons.Outlined.ShoppingBag,
            title = "New arrival: Gold Hour Repair Serum",
            body = "Our signature serum for stronger, shinier hair is here.",
            onClick = onDismiss
        )
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun NotificationRow(icon: ImageVector, title: String, body: String, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 14.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(DC.SurfaceHigh),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = DC.Gold, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column {
            Text(title, style = MaterialTheme.typography.titleMedium, color = DC.Ivory)
            Text(body, style = MaterialTheme.typography.bodyMedium, color = DC.Muted)
        }
    }
}
