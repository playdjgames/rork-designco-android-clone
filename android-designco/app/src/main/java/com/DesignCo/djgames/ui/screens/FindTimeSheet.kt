package com.DesignCo.djgames.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.DesignCo.djgames.data.Appointment
import com.DesignCo.djgames.data.AvailabilityScope
import com.DesignCo.djgames.data.BeautyService
import com.DesignCo.djgames.data.Catalog
import com.DesignCo.djgames.data.DayOption
import com.DesignCo.djgames.data.Schedule
import com.DesignCo.djgames.data.TimeOfDay
import com.DesignCo.djgames.data.WaitlistEntry
import com.DesignCo.djgames.ui.AppViewModel
import com.DesignCo.djgames.ui.MessageAction
import com.DesignCo.djgames.ui.components.CrownIcon
import com.DesignCo.djgames.ui.components.DcBottomSheet
import com.DesignCo.djgames.ui.components.DcCard
import com.DesignCo.djgames.ui.components.GoldButton
import com.DesignCo.djgames.ui.components.GoldPill
import com.DesignCo.djgames.ui.theme.DC
import com.DesignCo.djgames.ui.theme.SerifFamily
import com.DesignCo.djgames.util.formatDuration
import com.DesignCo.djgames.util.formatPrice

/**
 * The "Find a time" sheet: stylist chips, availability filters and a 15-minute
 * slot grid grouped by day, mirroring the iOS booking flow. Also used to
 * reschedule an existing appointment when [rescheduleId] is set.
 */
@Composable
fun FindTimeSheet(
    service: BeautyService,
    vm: AppViewModel,
    isMember: Boolean,
    waitlistedDays: Set<Long>,
    onDismiss: () -> Unit,
    onBooked: () -> Unit,
    rescheduleId: String? = null
) {
    val haptic = LocalHapticFeedback.current
    val days = remember { Schedule.upcomingDays(Schedule.MEMBER_HORIZON_DAYS) }
    val stylists = remember(service.kind) { Catalog.stylists(service.kind) }

    var stylistId by remember { mutableStateOf<String?>(null) }
    var scope by remember { mutableStateOf(AvailabilityScope.NextAvailable) }
    var timeOfDay by remember { mutableStateOf(TimeOfDay.Any) }
    var slot by remember { mutableStateOf<Int?>(null) }
    var selectedDay by remember { mutableStateOf<DayOption?>(null) }
    var confirm by remember { mutableStateOf(false) }

    val stylist = stylists.firstOrNull { it.id == stylistId }
    val stylistSeed = stylists.indexOfFirst { it.id == stylistId } + 1
    val now = System.currentTimeMillis()
    val visibleDays = Schedule.daysForScope(scope, days, isMember)
    val memberCutoff = days.first().startMillis + Schedule.STANDARD_HORIZON_DAYS * 24 * 60 * 60_000L

    DcBottomSheet(onDismiss = onDismiss) {
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                ClosePill(onDismiss)
                Text(
                    "Find a time",
                    style = MaterialTheme.typography.titleMedium,
                    color = DC.Ivory,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.width(72.dp))
            }
            Spacer(Modifier.height(18.dp))
            Text(service.name, fontFamily = SerifFamily, fontSize = 28.sp, color = DC.Ivory)
            Text(
                "${formatDuration(service.durationMinutes)} · ${formatPrice(service.price)}",
                style = MaterialTheme.typography.bodyLarge,
                color = DC.Gold,
                modifier = Modifier.padding(top = 2.dp, bottom = 16.dp)
            )

            ChipsRow(
                options = listOf("Any stylist") + stylists.map { it.name },
                selectedIndex = stylistId?.let { id -> stylists.indexOfFirst { it.id == id } + 1 } ?: 0,
                onSelect = { index ->
                    haptic.performHapticFeedback(HapticFeedbackType.SegmentTick)
                    stylistId = if (index == 0) null else stylists[index - 1].id
                    slot = null
                    selectedDay = null
                }
            )
            Spacer(Modifier.height(10.dp))
            ChipsRow(
                options = AvailabilityScope.entries.map { it.label },
                selectedIndex = scope.ordinal,
                onSelect = { index ->
                    haptic.performHapticFeedback(HapticFeedbackType.SegmentTick)
                    scope = AvailabilityScope.entries[index]
                    slot = null
                    selectedDay = null
                }
            )
            Spacer(Modifier.height(10.dp))
            ChipsRow(
                options = TimeOfDay.entries.map { it.label },
                selectedIndex = timeOfDay.ordinal,
                onSelect = { index ->
                    haptic.performHapticFeedback(HapticFeedbackType.SegmentTick)
                    timeOfDay = TimeOfDay.entries[index]
                    slot = null
                    selectedDay = null
                }
            )
            Spacer(Modifier.height(8.dp))

            visibleDays.forEachIndexed { index, day ->
                if (day.isClosed || day.startMillis <= now) return@forEachIndexed
                DaySlotsSection(
                    day = day,
                    stylistSeed = stylistSeed,
                    timeOfDay = timeOfDay,
                    now = now,
                    memberOnly = !isMember && day.startMillis >= memberCutoff,
                    selectedSlot = if (selectedDay == day) slot else null,
                    waitlisted = day.startMillis in waitlistedDays,
                    onSelectSlot = { minutes ->
                        haptic.performHapticFeedback(HapticFeedbackType.SegmentTick)
                        selectedDay = day
                        slot = minutes
                    },
                    onJoinWaitlist = {
                        vm.joinWaitlist(
                            WaitlistEntry(
                                dayStartMillis = day.startMillis,
                                dayLabel = day.fullLabel,
                                joinedAt = System.currentTimeMillis()
                            )
                        )
                    },
                    onJoinClub = {
                        onDismiss()
                        vm.post(
                            "Beauty Club members book ${Schedule.MEMBER_HORIZON_DAYS - Schedule.STANDARD_HORIZON_DAYS} days earlier",
                            MessageAction.OpenClub
                        )
                    },
                    isLast = index == visibleDays.lastIndex
                )
            }

            if (!isMember) {
                LockedHorizonCard(onJoinClub = {
                    onDismiss()
                    vm.post(
                        "Beauty Club members book ${Schedule.MEMBER_HORIZON_DAYS - Schedule.STANDARD_HORIZON_DAYS} days earlier",
                        MessageAction.OpenClub
                    )
                })
            }
            Spacer(Modifier.height(28.dp))
        }
    }

    if (confirm && selectedDay != null && slot != null) {
        val day = selectedDay ?: return
        val minutes = slot ?: return
        ConfirmBookingSheet(
            service = service,
            stylistName = stylist?.name ?: "First available",
            day = day,
            minutes = minutes,
            rescheduling = rescheduleId != null,
            onDismiss = { confirm = false },
            onConfirm = {
                if (rescheduleId != null) {
                    vm.reschedule(rescheduleId, day.fullLabel, Schedule.formatTime(minutes), day.startMillis + minutes * 60_000L)
                } else {
                    vm.book(
                        Appointment(
                            id = "A${System.currentTimeMillis()}",
                            kind = service.kind,
                            serviceName = service.name,
                            price = service.price,
                            stylistName = stylist?.name ?: "First available",
                            dayLabel = day.fullLabel,
                            timeLabel = Schedule.formatTime(minutes),
                            dateMillis = day.startMillis + minutes * 60_000L,
                            durationMinutes = service.durationMinutes
                        )
                    )
                    vm.claimWaitlistSpot(day.startMillis)
                }
            },
            onDone = {
                confirm = false
                onBooked()
            }
        )
    }
}

@Composable
private fun ClosePill(onDismiss: () -> Unit) {
    val shape = RoundedCornerShape(20.dp)
    Box(
        Modifier
            .clip(shape)
            .background(DC.SurfaceHigh)
            .border(1.dp, DC.Border, shape)
            .clickable(onClick = onDismiss)
            .padding(horizontal = 18.dp, vertical = 10.dp)
    ) {
        Text("Close", style = MaterialTheme.typography.labelLarge, color = DC.Ivory)
    }
}

@Composable
private fun ChipsRow(options: List<String>, selectedIndex: Int, onSelect: (Int) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        options.forEachIndexed { index, label ->
            FilterPill(label = label, selected = index == selectedIndex, onClick = { onSelect(index) })
        }
    }
}

@Composable
private fun FilterPill(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        Modifier
            .clip(CircleShape)
            .background(if (selected) DC.GoldGradient else SolidColor(DC.Surface))
            .border(1.dp, if (selected) DC.Gold else DC.Border, CircleShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 10.dp)
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) DC.OnGold else DC.Ivory
        )
    }
}

@Composable
private fun DaySlotsSection(
    day: DayOption,
    stylistSeed: Int,
    timeOfDay: TimeOfDay,
    now: Long,
    memberOnly: Boolean,
    selectedSlot: Int?,
    waitlisted: Boolean,
    onSelectSlot: (Int) -> Unit,
    onJoinWaitlist: () -> Unit,
    onJoinClub: () -> Unit,
    isLast: Boolean
) {
    val all = Schedule.slotsForDay().filter { timeOfDay.contains(it) }
    val open = all.filterNot { Schedule.isTaken(day, it, stylistSeed) || Schedule.isPast(day, it, now) }

    Column(Modifier.padding(vertical = 10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(day.weekday, style = MaterialTheme.typography.titleMedium, color = DC.Ivory)
            Spacer(Modifier.width(8.dp))
            Text(day.fullLabel.substringAfter(", "), style = MaterialTheme.typography.bodyMedium, color = DC.Muted)
        }
        when {
            memberOnly && open.isNotEmpty() -> MemberLockedRow(onJoinClub)
            open.isEmpty() -> FullyBookedRow(waitlisted, onJoinWaitlist)
            else -> all.chunked(3).forEach { rowItems ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowItems.forEach { minutes ->
                        Box(Modifier.weight(1f)) {
                            SlotCell(
                                label = Schedule.formatTime(minutes),
                                enabled = !(Schedule.isTaken(day, minutes, stylistSeed) || Schedule.isPast(day, minutes, now)),
                                selected = selectedSlot == minutes,
                                onClick = { onSelectSlot(minutes) }
                            )
                        }
                    }
                    repeat(3 - rowItems.size) { Box(Modifier.weight(1f)) }
                }
            }
        }
        if (!isLast) {
            HorizontalDivider(Modifier.padding(top = 12.dp), color = DC.Divider)
        }
    }
}

@Composable
private fun MemberLockedRow(onJoinClub: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(DC.Surface)
            .border(1.dp, DC.Border, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(CrownIcon, null, tint = DC.Gold, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(12.dp))
        Text(
            "Members-only opening",
            style = MaterialTheme.typography.bodyLarge,
            color = DC.Ivory,
            modifier = Modifier.weight(1f)
        )
        Box(
            Modifier
                .clip(CircleShape)
                .background(DC.GoldGradient)
                .clickable(onClick = onJoinClub)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text("See perks", style = MaterialTheme.typography.labelLarge, color = DC.OnGold)
        }
    }
}

@Composable
private fun FullyBookedRow(waitlisted: Boolean, onJoinWaitlist: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "Fully booked",
            style = MaterialTheme.typography.bodyLarge,
            color = DC.Muted,
            modifier = Modifier.weight(1f)
        )
        Box(
            Modifier
                .clip(CircleShape)
                .background(if (waitlisted) SolidColor(DC.SurfaceHigh) else DC.GoldGradient)
                .clickable(enabled = !waitlisted, onClick = onJoinWaitlist)
                .padding(horizontal = 18.dp, vertical = 10.dp)
        ) {
            Text(
                if (waitlisted) "On the waitlist" else "Join waitlist",
                style = MaterialTheme.typography.labelLarge,
                color = if (waitlisted) DC.Muted else DC.OnGold
            )
        }
    }
}

@Composable
private fun LockedHorizonCard(onJoinClub: () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(DC.Surface)
            .border(1.dp, DC.Gold.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(CrownIcon, null, tint = DC.Gold, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(10.dp))
            Text("Book 14 days earlier", fontFamily = SerifFamily, fontSize = 20.sp, color = DC.Ivory)
        }
        Text(
            "Beauty Club members see salon openings ${Schedule.MEMBER_HORIZON_DAYS} days ahead — before anyone else.",
            style = MaterialTheme.typography.bodyMedium,
            color = DC.Muted,
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
        )
        GoldPill("Join the Beauty Club", Modifier.clickable(onClick = onJoinClub))
        Spacer(Modifier.height(4.dp))
    }
}

@Composable
private fun SlotCell(label: String, enabled: Boolean, selected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(14.dp)
    Box(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(if (selected) DC.GoldGradient else SolidColor(DC.Surface))
            .border(1.dp, if (selected) DC.Gold else DC.Border, shape)
            .clickable(enabled = enabled, onClick = onClick)
            .alpha(if (enabled) 1f else 0.35f)
            .padding(vertical = 13.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) DC.OnGold else DC.Ivory,
            textDecoration = if (enabled) null else TextDecoration.LineThrough
        )
    }
}

@Composable
internal fun SummaryLine(label: String, value: String, emphasize: Boolean = false) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge, color = DC.Muted, modifier = Modifier.weight(1f))
        Text(
            value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = if (emphasize) FontWeight.Bold else FontWeight.Medium,
            color = if (emphasize) DC.Gold else DC.Ivory
        )
    }
}

/** Review-and-confirm sheet shown after a slot is chosen. Shared by booking and rescheduling. */
@Composable
internal fun ConfirmBookingSheet(
    service: BeautyService,
    stylistName: String,
    day: DayOption,
    minutes: Int,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    onDone: () -> Unit,
    rescheduling: Boolean = false
) {
    var done by remember { mutableStateOf(false) }
    DcBottomSheet(onDismiss = if (done) onDone else onDismiss) {
        Column(Modifier.padding(horizontal = 24.dp)) {
            if (done) {
                Box(
                    Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(DC.GoldGradient)
                        .align(Alignment.CenterHorizontally),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.CheckCircle, null, tint = DC.OnGold, modifier = Modifier.size(34.dp))
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    if (rescheduling) "Visit moved" else "You're booked",
                    fontFamily = SerifFamily,
                    fontSize = 30.sp,
                    color = DC.Ivory,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Text(
                    "${service.name} on ${day.fullLabel} at ${Schedule.formatTime(minutes)}.\nWe'll remind you the day before and when it's time to rebook.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = DC.Muted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 8.dp, bottom = 24.dp)
                )
                GoldButton("View my appointments", onClick = onDone)
            } else {
                Text(
                    if (rescheduling) "Review new time" else "Review booking",
                    fontFamily = SerifFamily,
                    fontSize = 28.sp,
                    color = DC.Ivory
                )
                Spacer(Modifier.height(16.dp))
                DcCard(Modifier.fillMaxWidth()) {
                    SummaryLine("Service", service.name)
                    SummaryLine("With", stylistName)
                    SummaryLine("Date", day.fullLabel)
                    SummaryLine("Time", Schedule.formatTime(minutes))
                    SummaryLine("Duration", formatDuration(service.durationMinutes))
                    HorizontalDivider(color = DC.Divider)
                    SummaryLine("Total", formatPrice(service.price), emphasize = true)
                }
                Text(
                    "Pay at the studio. Free cancellation up to 24 hours before.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DC.Muted,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                GoldButton(if (rescheduling) "Confirm new time" else "Confirm Booking", onClick = {
                    onConfirm()
                    done = true
                })
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}
