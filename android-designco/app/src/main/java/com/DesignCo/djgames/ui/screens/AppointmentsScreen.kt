package com.DesignCo.djgames.ui.screens

import android.content.Intent
import android.provider.CalendarContract
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.EditCalendar
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.EventBusy
import androidx.compose.material.icons.outlined.ContentCut
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.DesignCo.djgames.data.Appointment
import com.DesignCo.djgames.data.Catalog
import com.DesignCo.djgames.data.ServiceKind
import com.DesignCo.djgames.ui.AppViewModel
import com.DesignCo.djgames.ui.components.DcCard
import com.DesignCo.djgames.ui.components.GoldButton
import com.DesignCo.djgames.ui.navigation.AppNavigator
import com.DesignCo.djgames.ui.navigation.Tab
import com.DesignCo.djgames.ui.navigation.tabBarClearance
import com.DesignCo.djgames.ui.theme.DC
import com.DesignCo.djgames.ui.theme.SerifFamily
import com.DesignCo.djgames.util.formatPrice

/** Appointments tab: upcoming and past visits, rescheduling, waitlist and rebooking nudges. */
@Composable
fun AppointmentsScreen(vm: AppViewModel, nav: AppNavigator) {
    val data by vm.data.collectAsStateWithLifecycle()
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
    val now = System.currentTimeMillis()
    val upcoming = remember(data.appointments) { data.upcomingAppointments(now) }
    val past = remember(data.appointments) { data.pastAppointments(now) }
    val rebook = remember(data.appointments) { data.rebookingSuggestion(now) }


    var showPast by rememberSaveable { mutableStateOf(false) }
    var cancelTarget by remember { mutableStateOf<Appointment?>(null) }
    var rescheduleTarget by remember { mutableStateOf<Appointment?>(null) }
    var calendarHint by remember { mutableStateOf(false) }

    fun addToCalendar(appointment: Appointment) {
        val intent = Intent(Intent.ACTION_INSERT)
            .setData(CalendarContract.Events.CONTENT_URI)
            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, appointment.dateMillis)
            .putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false)
            .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, appointment.dateMillis + appointment.durationMinutes * 60_000L)
            .putExtra(CalendarContract.Events.TITLE, "DesignCo — ${appointment.serviceName}")
            .putExtra(CalendarContract.Events.DESCRIPTION, "with ${appointment.stylistName} · DesignCo Salon & Barber")
        runCatching { context.startActivity(intent) }
            .onFailure { calendarHint = true }
    }

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
                Column(Modifier.padding(horizontal = 20.dp)) {
                    Spacer(Modifier.height(14.dp))
                    Text("Appointments", fontFamily = SerifFamily, fontSize = 40.sp, color = DC.Ivory)
                    Spacer(Modifier.height(14.dp))
                    Segmented(
                        options = listOf("Upcoming", "Past"),
                        selectedIndex = if (showPast) 1 else 0,
                        onSelect = {
                            haptic.performHapticFeedback(HapticFeedbackType.SegmentTick)
                            showPast = it == 1
                        }
                    )
                    Spacer(Modifier.height(18.dp))
                }
            }
            if (!showPast) {
                rebook?.let { appt ->
                    item(key = "rebook") {
                        RebookCard(appt, onRebook = { nav.openTab(Tab.Book) })
                    }
                }
                data.waitlist.forEach { entry ->
                    item(key = "wl-${entry.dayStartMillis}") {
                        WaitlistCard(
                            label = entry.dayLabel,
                            spotOpen = entry.spotOpen,
                            onOpen = { nav.openTab(Tab.Book) },
                            onLeave = { vm.leaveWaitlist(entry.dayStartMillis) }
                        )
                    }
                }
                if (upcoming.isEmpty()) {
                    item(key = "empty") {
                        EmptyAppointments(
                            onBook = {
                                vm.setBookingKind(ServiceKind.Salon)
                                nav.openTab(Tab.Book)
                            }
                        )
                    }
                } else {
                    items(upcoming.size, key = { upcoming[it].id }) { index ->
                        val appt = upcoming[index]
                        AppointmentCard(
                            appointment = appt,
                            onCancel = { cancelTarget = appt },
                            onReschedule = { rescheduleTarget = appt },
                            onAddToCalendar = { addToCalendar(appt) }
                        )
                    }
                }
            } else if (past.isEmpty()) {
                item(key = "empty-past") {
                    Text(
                        "No past visits yet. Your history will be kept here.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = DC.Muted,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp)
                    )
                }
            } else {
                items(past.size, key = { past[it].id }) { index ->
                    val appt = past[index]
                    PastAppointmentCard(
                        appointment = appt,
                        onRebook = {
                            vm.setBookingKind(appt.kind)
                            nav.openTab(Tab.Book)
                        }
                    )
                }
            }
        }
    }

    cancelTarget?.let { appt ->
        AlertDialog(
            onDismissRequest = { cancelTarget = null },
            containerColor = DC.Surface,
            titleContentColor = DC.Ivory,
            textContentColor = DC.Muted,
            title = { Text("Cancel appointment?", fontFamily = SerifFamily) },
            text = { Text("${appt.serviceName} on ${appt.dayLabel} at ${appt.timeLabel} will be released.") },
            confirmButton = {
                TextButton(onClick = {
                    vm.cancelAppointment(appt.id)
                    cancelTarget = null
                }) { Text("Cancel it", color = DC.Gold) }
            },
            dismissButton = {
                TextButton(onClick = { cancelTarget = null }) { Text("Keep", color = DC.Ivory) }
            }
        )
    }

    rescheduleTarget?.let { appt ->
        val service = Catalog.services.firstOrNull { it.name == appt.serviceName }
        if (service != null) {
            FindTimeSheet(
                service = service,
                vm = vm,
                isMember = data.isMember,
                waitlistedDays = emptySet(),
                rescheduleId = appt.id,
                onDismiss = { rescheduleTarget = null },
                onBooked = { rescheduleTarget = null }
            )
        } else {
            rescheduleTarget = null
        }
    }

    if (calendarHint) {
        AlertDialog(
            onDismissRequest = { calendarHint = false },
            containerColor = DC.Surface,
            titleContentColor = DC.Ivory,
            textContentColor = DC.Muted,
            title = { Text("No calendar app", fontFamily = SerifFamily) },
            text = { Text("DesignCo couldn't find a calendar app on this phone to add the visit to.") },
            confirmButton = {
                TextButton(onClick = { calendarHint = false }) { Text("OK", color = DC.Gold) }
            }
        )
    }
}

/** Upcoming/Past animated segmented control. */
@Composable
private fun Segmented(options: List<String>, selectedIndex: Int, onSelect: (Int) -> Unit) {
    val shape = CircleShape
    BoxWithConstraints(
        Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(shape)
            .background(DC.Surface)
            .border(1.dp, DC.Border, shape)
            .padding(6.dp)
    ) {
        val segment = maxWidth / options.size
        val offset by animateDpAsState(segment * selectedIndex, label = "segmented")
        Box(
            Modifier
                .offset(x = offset)
                .width(segment)
                .fillMaxHeight()
                .clip(shape)
                .background(DC.GoldGradient)
        )
        Row(Modifier.fillMaxSize()) {
            options.forEachIndexed { index, option ->
                Box(
                    Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(shape)
                        .clickable { onSelect(index) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        option,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (index == selectedIndex) DC.OnGold else DC.Ivory
                    )
                }
            }
        }
    }
}

/** Rebooking nudge: a past visit whose recommended interval has elapsed. */
@Composable
private fun RebookCard(appt: Appointment, onRebook: () -> Unit) {
    val weeks = Catalog.services.firstOrNull { it.name == appt.serviceName }?.rebookWeeks ?: 6
    DcCard(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .fillMaxWidth(),
        borderColor = DC.Gold.copy(alpha = 0.6f)
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.NotificationsNone, null, tint = DC.Gold, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(10.dp))
                Text(
                    "TIME TO REBOOK",
                    fontSize = 12.sp,
                    letterSpacing = 2.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DC.Gold
                )
            }
            Text(
                "Your ${appt.serviceName.lowercase()} rhythm is about $weeks weeks.",
                style = MaterialTheme.typography.bodyLarge,
                color = DC.Ivory,
                modifier = Modifier.padding(top = 8.dp, bottom = 14.dp)
            )
            GoldButton("Rebook ${appt.serviceName}", onClick = onRebook)
        }
    }
}

@Composable
private fun WaitlistCard(label: String, spotOpen: Boolean, onOpen: () -> Unit, onLeave: () -> Unit) {
    DcCard(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .fillMaxWidth(),
        onClick = if (spotOpen) onOpen else null
    ) {
        Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                if (spotOpen) Icons.Filled.CheckCircle else Icons.Outlined.NotificationsNone,
                null,
                tint = DC.Gold,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    if (spotOpen) "A spot opened — $label" else "On the waitlist · $label",
                    style = MaterialTheme.typography.titleMedium,
                    color = DC.Ivory
                )
                Text(
                    if (spotOpen) "Tap to claim it before it's gone." else "We'll alert you the moment a spot opens.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DC.Muted
                )
            }
            if (!spotOpen) {
                Text(
                    "Leave",
                    style = MaterialTheme.typography.labelLarge,
                    color = DC.Muted,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable(onClick = onLeave)
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun EmptyAppointments(onBook: () -> Unit) {
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
                Icon(Icons.Outlined.CalendarMonth, null, tint = DC.Gold, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.height(12.dp))
            Text("Nothing booked yet.", fontFamily = SerifFamily, fontSize = 26.sp, color = DC.Ivory)
            Text(
                "Book online or in the app and it shows up here.",
                style = MaterialTheme.typography.bodyLarge,
                color = DC.Muted,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )
            GoldButton("Book an appointment", onClick = onBook)
        }
    }
}

@Composable
private fun AppointmentCard(
    appointment: Appointment,
    onCancel: () -> Unit,
    onReschedule: () -> Unit,
    onAddToCalendar: () -> Unit
) {
    Column(Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
        DcCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.ContentCut, null, tint = DC.Gold, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            appointment.serviceName,
                            style = MaterialTheme.typography.titleMedium,
                            color = DC.Ivory,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            "${appointment.dayLabel} · ${appointment.timeLabel}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = DC.Ivory.copy(alpha = 0.8f)
                        )
                        Text(
                            "${appointment.kind.label} · with ${appointment.stylistName} · ${formatPrice(appointment.price)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = DC.Muted
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ActionChip(Icons.Outlined.Event, "Calendar", Modifier.weight(1f), onClick = onAddToCalendar)
                    ActionChip(Icons.Outlined.EditCalendar, "Reschedule", Modifier.weight(1f), onClick = onReschedule)
                    ActionChip(Icons.Outlined.EventBusy, "Cancel", Modifier.weight(1f), onClick = onCancel)
                }
            }
        }
    }
}

@Composable
private fun PastAppointmentCard(appointment: Appointment, onRebook: () -> Unit) {
    Column(Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
        DcCard(Modifier.fillMaxWidth()) {
            Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.ContentCut, null, tint = DC.Muted, modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        appointment.serviceName,
                        style = MaterialTheme.typography.titleMedium,
                        color = DC.Ivory.copy(alpha = 0.85f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        "${appointment.dayLabel} · ${appointment.timeLabel} · with ${appointment.stylistName}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = DC.Muted
                    )
                }
                Text(
                    "Rebook",
                    style = MaterialTheme.typography.labelLarge,
                    color = DC.Gold,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable(onClick = onRebook)
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun ActionChip(icon: ImageVector, label: String, modifier: Modifier, onClick: () -> Unit) {
    val shape = RoundedCornerShape(16.dp)
    Row(
        modifier
            .clip(shape)
            .background(DC.SurfaceHigh)
            .border(1.dp, DC.Border, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(icon, null, tint = DC.Gold, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(6.dp))
        Text(label, style = MaterialTheme.typography.labelMedium, color = DC.Ivory, maxLines = 1)
    }
}
