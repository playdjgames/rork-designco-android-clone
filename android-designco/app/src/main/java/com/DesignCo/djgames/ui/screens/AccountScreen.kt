package com.DesignCo.djgames.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ContentCut
import androidx.compose.material.icons.outlined.EventBusy
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.DesignCo.djgames.data.Appointment
import com.DesignCo.djgames.ui.AppViewModel
import com.DesignCo.djgames.ui.components.CardRow
import com.DesignCo.djgames.ui.components.Chevron
import com.DesignCo.djgames.ui.components.CrownIcon
import com.DesignCo.djgames.ui.components.DcCard
import com.DesignCo.djgames.ui.components.GoldButton
import com.DesignCo.djgames.ui.components.SectionHeader
import com.DesignCo.djgames.ui.navigation.AppNavigator
import com.DesignCo.djgames.ui.navigation.Tab
import com.DesignCo.djgames.ui.navigation.tabBarClearance
import com.DesignCo.djgames.ui.theme.DC
import com.DesignCo.djgames.ui.theme.SerifFamily
import com.DesignCo.djgames.util.formatPrice

@Composable
fun AccountScreen(vm: AppViewModel, nav: AppNavigator) {
    val data by vm.data.collectAsStateWithLifecycle()
    val upcoming = remember(data.appointments) { data.upcomingAppointments(System.currentTimeMillis()) }
    var cancelTarget by remember { mutableStateOf<Appointment?>(null) }
    var confirmDelete by remember { mutableStateOf(false) }
    val user = data.user

    Box(
        Modifier
            .fillMaxSize()
            .background(DC.CanvasGradient)
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(bottom = tabBarClearance())
        ) {
            Column(Modifier.padding(horizontal = 20.dp)) {
                Spacer(Modifier.height(14.dp))
                Text("Profile", fontFamily = SerifFamily, fontSize = 40.sp, color = DC.Ivory)
            }
            Spacer(Modifier.height(14.dp))

            DcCard(
                Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
            ) {
                Column(Modifier.padding(24.dp)) {
                    if (user == null) {
                        Text("Welcome to DesignCo", fontFamily = SerifFamily, fontSize = 28.sp, color = DC.Ivory)
                        Text(
                            "Create an account to save your details, track orders and manage appointments.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = DC.Muted,
                            modifier = Modifier.padding(top = 8.dp, bottom = 22.dp)
                        )
                        GoldButton("Create Account", onClick = { nav.openAuth("create") })
                        Text(
                            "Beauty Club member? Sign in",
                            style = MaterialTheme.typography.labelLarge,
                            color = DC.Gold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp)
                                .clip(CircleShape)
                                .clickable { nav.openAuth("signin") }
                                .padding(vertical = 10.dp)
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier
                                    .size(58.dp)
                                    .clip(CircleShape)
                                    .background(DC.GoldGradient),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    user.name.split(' ').mapNotNull { it.firstOrNull()?.uppercase() }.take(2).joinToString(""),
                                    fontFamily = SerifFamily,
                                    fontSize = 22.sp,
                                    color = DC.OnGold
                                )
                            }
                            Spacer(Modifier.width(16.dp))
                            Column(Modifier.weight(1f)) {
                                Text("Hello, ${user.name.substringBefore(' ')}", fontFamily = SerifFamily, fontSize = 26.sp, color = DC.Ivory)
                                Text(user.email, style = MaterialTheme.typography.bodyMedium, color = DC.Muted)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            DcCard(
                Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth(),
                onClick = nav::openClub
            ) {
                Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(DC.SurfaceHigh),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(CrownIcon, null, tint = DC.Gold, modifier = Modifier.size(24.dp))
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Beauty Club", style = MaterialTheme.typography.titleMedium, fontSize = 18.sp, color = DC.Ivory)
                        val plan = data.clubPlan
                        Text(
                            if (plan != null) "Member · ${plan.label} plan" else "Not a member · from $12/month",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (plan != null) DC.Gold else DC.Muted
                        )
                    }
                    Chevron()
                }
            }

            SectionHeader(
                "Appointments",
                modifier = Modifier.padding(start = 20.dp, end = 12.dp, top = 26.dp, bottom = 12.dp),
                actionLabel = "See all",
                onAction = { nav.openTab(Tab.Appointments) }
            )
            DcCard(
                Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
            ) {
                if (upcoming.isEmpty()) {
                    CardRow(
                        icon = Icons.Outlined.CalendarMonth,
                        title = "No upcoming appointments",
                        subtitle = "Book from the Book tab",
                        onClick = { nav.openTab(Tab.Book) }
                    )
                } else {
                    upcoming.forEachIndexed { i, appt ->
                        Column {
                            Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.ContentCut, null, tint = DC.Gold, modifier = Modifier.size(24.dp))
                                Spacer(Modifier.width(16.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(appt.serviceName, style = MaterialTheme.typography.titleMedium, color = DC.Ivory)
                                    Text(
                                        "${appt.dayLabel} · ${appt.timeLabel}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = DC.Ivory.copy(alpha = 0.8f)
                                    )
                                    Text(
                                        "${appt.kind.label} · with ${appt.stylistName} · ${formatPrice(appt.price)}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = DC.Muted
                                    )
                                }
                                TextButton(onClick = { cancelTarget = appt }) {
                                    Text("Cancel", color = DC.Gold)
                                }
                            }
                            if (i < upcoming.lastIndex) HorizontalDivider(Modifier.padding(start = 58.dp), color = DC.Divider)
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            DcCard(
                Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
            ) {
                CardRow(
                    icon = Icons.Outlined.Inventory2,
                    title = "Order History",
                    onClick = nav::openOrders,
                    trailing = {
                        Text("${data.orders.size}", style = MaterialTheme.typography.titleMedium, color = DC.Muted)
                        Spacer(Modifier.width(8.dp))
                        Chevron()
                    }
                )
            }

            Spacer(Modifier.height(16.dp))
            DcCard(
                Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
            ) {
                CardRow(
                    icon = Icons.Outlined.NotificationsNone,
                    title = "Appointment reminders",
                    subtitle = "Text reminder the day before",
                    onClick = { vm.setReminders(!data.remindersOn) },
                    trailing = {
                        Switch(
                            checked = data.remindersOn,
                            onCheckedChange = vm::setReminders,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = DC.Ivory,
                                checkedTrackColor = DC.Gold,
                                checkedBorderColor = DC.Gold,
                                uncheckedThumbColor = DC.Muted,
                                uncheckedTrackColor = DC.SurfaceHigh,
                                uncheckedBorderColor = DC.Border
                            )
                        )
                    }
                )
            }

            Spacer(Modifier.height(16.dp))
            DcCard(
                Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    "THE STUDIO",
                    fontSize = 13.sp,
                    letterSpacing = 3.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DC.Muted,
                    modifier = Modifier.padding(start = 20.dp, top = 20.dp, bottom = 4.dp)
                )
                StudioLine(Icons.Outlined.AccessTime, "Tue – Sat", "9:00 AM – 6:00 PM")
                StudioLine(Icons.Outlined.EventBusy, "Sun – Mon", "Closed")
                StudioLine(Icons.Outlined.ContentCut, "Services", "Salon · Barber · Retail")
                StudioLine(Icons.Outlined.LocationOn, "Address", "214 Gold St, Suite 2")
                StudioLine(Icons.Outlined.Phone, "Phone", "(555) 014-2026")
                Spacer(Modifier.height(10.dp))
            }

            if (user != null) {
                Spacer(Modifier.height(16.dp))
                DcCard(
                    Modifier
                        .padding(horizontal = 20.dp)
                        .fillMaxWidth()
                ) {
                    CardRow(
                        icon = Icons.AutoMirrored.Outlined.Logout,
                        title = "Sign out",
                        iconTint = DC.Muted,
                        onClick = vm::signOut,
                        trailing = {}
                    )
                    CardRow(
                        icon = Icons.Outlined.DeleteForever,
                        title = "Delete account",
                        iconTint = DC.Muted,
                        onClick = { confirmDelete = true },
                        trailing = {}
                    )
                }
            }
        }
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            containerColor = DC.Surface,
            titleContentColor = DC.Ivory,
            textContentColor = DC.Muted,
            title = { Text("Delete account?", fontFamily = SerifFamily) },
            text = { Text("Your profile, appointments, orders, favorites and membership will be permanently removed from this device.") },
            confirmButton = {
                TextButton(onClick = {
                    vm.deleteAccount()
                    confirmDelete = false
                }) { Text("Delete", color = DC.Gold) }
            },
            dismissButton = {
                TextButton(onClick = { confirmDelete = false }) { Text("Keep", color = DC.Ivory) }
            }
        )
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
}

@Composable
private fun StudioLine(icon: ImageVector, label: String, value: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = DC.Gold, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(14.dp))
        Text(label, style = MaterialTheme.typography.bodyLarge, color = DC.Muted, modifier = Modifier.weight(1f))
        Text(value, style = MaterialTheme.typography.bodyLarge, color = DC.Ivory)
    }
}
