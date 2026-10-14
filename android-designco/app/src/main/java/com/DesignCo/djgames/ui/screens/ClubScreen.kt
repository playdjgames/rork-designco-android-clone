package com.DesignCo.djgames.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Redeem
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ShoppingBag
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.DesignCo.djgames.data.Catalog
import com.DesignCo.djgames.data.ClubPlan
import com.DesignCo.djgames.ui.AppViewModel
import com.DesignCo.djgames.ui.components.CircleIconButton
import com.DesignCo.djgames.ui.components.CrownIcon
import com.DesignCo.djgames.ui.components.DcBottomSheet
import com.DesignCo.djgames.ui.components.DcCard
import com.DesignCo.djgames.ui.components.GoldButton
import com.DesignCo.djgames.ui.components.RemoteImage
import com.DesignCo.djgames.ui.navigation.AppNavigator
import com.DesignCo.djgames.ui.navigation.tabBarClearance
import com.DesignCo.djgames.ui.theme.DC
import com.DesignCo.djgames.ui.theme.SerifFamily

private data class Perk(val icon: ImageVector, val title: String, val detail: String)

private val perks = listOf(
    Perk(
        Icons.Outlined.CalendarMonth, "Book 14 days earlier",
        "Members see salon openings 14 days before everyone else — automatically, right in the app."
    ),
    Perk(
        Icons.Filled.Redeem, "Birthday treatment",
        "A complimentary gloss treatment during your birthday month."
    ),
    Perk(
        Icons.Outlined.AutoAwesome, "Early access",
        "Be first to try new salon services and product launches."
    ),
    Perk(
        Icons.Outlined.ShoppingBag, "10% off all retail",
        "Your member discount is applied automatically to every order in the shop."
    )
)

/** Beauty Club Gold Pass screen, pushed from Home or Profile. */
@Composable
fun ClubScreen(vm: AppViewModel, nav: AppNavigator) {
    val data by vm.data.collectAsStateWithLifecycle()
    val haptic = LocalHapticFeedback.current
    var plan by rememberSaveable { mutableStateOf(ClubPlan.Monthly) }
    var showJoin by remember { mutableStateOf(false) }

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
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircleIconButton(
                    icon = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Back",
                    onClick = nav::back,
                    size = 46.dp
                )
                Text(
                    "Beauty Club",
                    style = MaterialTheme.typography.titleMedium,
                    color = DC.Ivory,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.width(58.dp))
            }

            ClubHero(isMember = data.isMember)
            Text(
                "As a DesignCo member you already get fast rebooking, reminders and waitlist alerts. The Beauty Club adds salon perks on top.",
                style = MaterialTheme.typography.bodyLarge,
                color = DC.Muted,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
            )

            perks.forEach { perk ->
                PerkRow(perk)
                Spacer(Modifier.height(10.dp))
            }

            Spacer(Modifier.height(14.dp))
            val active = data.clubPlan
            if (active != null) {
                DcCard(
                    Modifier
                        .padding(horizontal = 20.dp)
                        .fillMaxWidth(),
                    borderColor = DC.Gold
                ) {
                    Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.CheckCircle, null, tint = DC.Gold, modifier = Modifier.size(28.dp))
                        Spacer(Modifier.width(14.dp))
                        Column(Modifier.weight(1f)) {
                            Text("Gold Pass active", style = MaterialTheme.typography.titleMedium, color = DC.Ivory)
                            Text(
                                "${active.label} plan · ${active.priceLabel} ${active.note}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = DC.Muted
                            )
                        }
                    }
                }
                Spacer(Modifier.height(18.dp))
                Box(Modifier.padding(horizontal = 20.dp)) {
                    GoldButton("Book 14 days earlier", onClick = nav::back)
                }
                TextButton(
                    onClick = { vm.leaveClub() },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 8.dp)
                ) {
                    Text("Cancel membership", color = DC.Muted)
                }
            } else {
                Column(Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    ClubPlan.entries.forEach { p ->
                        PlanCard(p, selected = p == plan) {
                            haptic.performHapticFeedback(HapticFeedbackType.SegmentTick)
                            plan = p
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
                Box(Modifier.padding(horizontal = 20.dp)) {
                    GoldButton("Join the Club", onClick = { showJoin = true })
                }
                Text(
                    "Cancel anytime. Billed at the studio.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DC.Muted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                )
            }
            Spacer(Modifier.height(24.dp))
        }
    }

    if (showJoin) {
        var joined by remember { mutableStateOf(false) }
        DcBottomSheet(onDismiss = { showJoin = false }) {
            Column(Modifier.padding(horizontal = 24.dp)) {
                if (joined) {
                    Icon(
                        CrownIcon,
                        null,
                        tint = DC.Gold,
                        modifier = Modifier
                            .size(56.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                    Text(
                        "Welcome to the Club",
                        fontFamily = SerifFamily,
                        fontSize = 30.sp,
                        color = DC.Ivory,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 12.dp)
                    )
                    Text(
                        "You now book 14 days earlier, with a birthday treatment and early access on top.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = DC.Muted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 24.dp)
                    )
                    GoldButton("Done", onClick = { showJoin = false })
                } else {
                    Text("Join Beauty Club", fontFamily = SerifFamily, fontSize = 28.sp, color = DC.Ivory)
                    Spacer(Modifier.height(16.dp))
                    DcCard(Modifier.fillMaxWidth()) {
                        SummaryLine("Plan", plan.label)
                        SummaryLine("Price", "${plan.priceLabel} ${plan.note}", emphasize = true)
                        SummaryLine("Member", data.user?.name ?: "Guest")
                    }
                    Text(
                        "Your first charge is collected at your next visit. Cancel anytime from this screen.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = DC.Muted,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                    GoldButton("Confirm membership", onClick = {
                        vm.joinClub(plan)
                        joined = true
                    })
                }
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun PerkRow(perk: Perk) {
    val shape = RoundedCornerShape(20.dp)
    Row(
        Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .clip(shape)
            .background(DC.Surface)
            .border(1.dp, DC.Border, shape)
            .padding(18.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(DC.SurfaceHigh),
            contentAlignment = Alignment.Center
        ) {
            Icon(perk.icon, null, tint = DC.Gold, modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text(perk.title, style = MaterialTheme.typography.titleMedium, fontSize = 18.sp, color = DC.Ivory)
            Text(
                perk.detail,
                style = MaterialTheme.typography.bodyMedium,
                color = DC.Muted,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun ClubHero(isMember: Boolean) {
    val shape = RoundedCornerShape(26.dp)
    Box(
        Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .height(280.dp)
            .clip(shape)
            .border(1.dp, DC.Gold.copy(alpha = 0.7f), shape)
    ) {
        RemoteImage(Catalog.CLUB_IMAGE, "DesignCo Beauty Club gold pass", Modifier.fillMaxSize(), alignment = Alignment.CenterEnd)
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        0f to Color.Black.copy(alpha = 0.85f),
                        0.5f to Color.Black.copy(alpha = 0.35f),
                        0.8f to Color.Transparent
                    )
                )
        )
        Column(
            Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Text("DesignCo", fontFamily = SerifFamily, fontSize = 36.sp, color = DC.Ivory)
            Text(
                "BEAUTY CLUB",
                fontSize = 20.sp,
                letterSpacing = 5.sp,
                fontWeight = FontWeight.Medium,
                color = DC.Gold
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "For DesignCo salon clients",
                style = MaterialTheme.typography.bodyLarge,
                color = DC.Ivory.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun PlanCard(plan: ClubPlan, selected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(20.dp)
    val border by animateColorAsState(if (selected) DC.Gold else DC.Border, label = "planBorder")
    Row(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(DC.Surface)
            .border(if (selected) 1.5.dp else 1.dp, border, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(30.dp)
                .clip(CircleShape)
                .border(if (selected) 8.dp else 1.5.dp, if (selected) DC.Gold else DC.Muted, CircleShape)
        )
        Spacer(Modifier.width(18.dp))
        Column {
            Text(plan.label, style = MaterialTheme.typography.titleLarge, color = DC.Ivory)
            Row {
                Text("${plan.priceLabel} ${plan.note}", style = MaterialTheme.typography.bodyLarge, color = DC.Muted)
                if (plan == ClubPlan.Annual) {
                    Text(" · Save 31%", style = MaterialTheme.typography.bodyLarge, color = DC.Gold)
                }
            }
        }
    }
}
