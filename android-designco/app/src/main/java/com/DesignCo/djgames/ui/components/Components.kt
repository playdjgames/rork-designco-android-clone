package com.DesignCo.djgames.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.DesignCo.djgames.ui.theme.DC
import com.DesignCo.djgames.ui.theme.TaglineStyle
import com.DesignCo.djgames.ui.theme.WordmarkStyle

/** Custom crown glyph used for the Beauty Club. */
val CrownIcon: ImageVector by lazy {
    ImageVector.Builder(
        name = "Crown",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(3f, 7.5f)
            lineTo(7.6f, 11.6f)
            lineTo(12f, 4.5f)
            lineTo(16.4f, 11.6f)
            lineTo(21f, 7.5f)
            lineTo(19.4f, 17f)
            lineTo(4.6f, 17f)
            close()
            moveTo(4.6f, 18.4f)
            lineTo(19.4f, 18.4f)
            lineTo(19.4f, 20f)
            lineTo(4.6f, 20f)
            close()
        }
    }.build()
}

/** Serif wordmark with tracked tagline used at the top of every tab. */
@Composable
fun BrandHeader(
    modifier: Modifier = Modifier,
    horizontalPadding: Dp = 20.dp,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = horizontalPadding, end = horizontalPadding, top = 14.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text("DesignCo", style = WordmarkStyle)
            Spacer(Modifier.height(2.dp))
            Text("BEAUTY LOOKS BETTER HERE", style = TaglineStyle, maxLines = 1)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), content = actions)
    }
}

/** Outlined circular icon button with optional count badge or dot. */
@Composable
fun CircleIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badgeCount: Int = 0,
    showDot: Boolean = false,
    size: Dp = 52.dp
) {
    BadgedBox(
        modifier = modifier,
        badge = {
            when {
                badgeCount > 0 -> Badge(containerColor = DC.Gold, contentColor = DC.OnGold) {
                    Text("$badgeCount", fontWeight = FontWeight.Bold)
                }
                showDot -> Badge(containerColor = DC.Gold, modifier = Modifier.padding(top = 10.dp, end = 10.dp))
            }
        }
    ) {
        OutlinedIconButton(
            onClick = onClick,
            modifier = Modifier.size(size),
            border = BorderStroke(1.dp, DC.Border),
            colors = IconButtonDefaults.outlinedIconButtonColors(
                containerColor = DC.Surface.copy(alpha = 0.7f),
                contentColor = DC.Ivory
            )
        ) {
            Icon(icon, contentDescription = contentDescription, modifier = Modifier.size(22.dp))
        }
    }
}

/** Primary call-to-action: brushed-gold gradient pill with press scale and haptic. */
@Composable
fun GoldButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    height: Dp = 58.dp
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val scale by animateFloatAsState(if (pressed) 0.97f else 1f, label = "goldPress")
    val haptic = LocalHapticFeedback.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .shadow(
                elevation = if (enabled) 14.dp else 0.dp,
                shape = CircleShape,
                ambientColor = DC.Gold,
                spotColor = DC.Gold
            )
            .clip(CircleShape)
            .background(if (enabled) DC.GoldGradient else SolidColor(DC.SurfaceHigh))
            .clickable(
                interactionSource = interaction,
                indication = ripple(color = DC.OnGold),
                enabled = enabled,
                role = Role.Button
            ) {
                haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(fontSize = 17.sp),
            color = if (enabled) DC.OnGold else DC.Muted
        )
    }
}

/** Dark rounded surface with a hairline gold-tinted border. */
@Composable
fun DcCard(
    modifier: Modifier = Modifier,
    borderColor: Color = DC.Border,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(22.dp)
    Column(
        modifier = modifier
            .clip(shape)
            .background(DC.Surface)
            .border(1.dp, borderColor, shape)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        content = content
    )
}

/** Section title with an optional trailing link or hint. */
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            title,
            style = MaterialTheme.typography.titleLarge,
            color = DC.Ivory,
            modifier = Modifier.weight(1f)
        )
        if (actionLabel != null) {
            if (onAction != null) {
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable(onClick = onAction)
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(actionLabel, style = MaterialTheme.typography.bodyLarge, color = DC.Muted)
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = DC.Muted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            } else {
                Text(actionLabel, style = MaterialTheme.typography.bodyLarge, color = DC.Muted)
            }
        }
    }
}

@Composable
fun Chevron(modifier: Modifier = Modifier, tint: Color = DC.Muted) {
    Icon(
        Icons.AutoMirrored.Filled.KeyboardArrowRight,
        contentDescription = null,
        tint = tint,
        modifier = modifier.size(22.dp)
    )
}

/** A list row living inside a [DcCard]. */
@Composable
fun CardRow(
    icon: ImageVector,
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    iconTint: Color = DC.Gold,
    showDivider: Boolean = false,
    onClick: (() -> Unit)? = null,
    trailing: @Composable RowScope.() -> Unit = { Chevron() }
) {
    Column(modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
                .padding(horizontal = 18.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(18.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    color = DC.Ivory,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (subtitle != null) {
                    Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = DC.Muted)
                }
            }
            trailing()
        }
        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(start = 60.dp),
                thickness = 1.dp,
                color = DC.Divider
            )
        }
    }
}

/** Remote photo with a dark placeholder background. */
@Composable
fun RemoteImage(
    url: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    alignment: Alignment = Alignment.Center
) {
    AsyncImage(
        model = url,
        contentDescription = contentDescription,
        contentScale = contentScale,
        alignment = alignment,
        modifier = modifier.background(DC.SurfaceHigh)
    )
}

/** Small gold pill label, e.g. NEW or New Arrival. */
@Composable
fun GoldPill(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(DC.GoldGradient)
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        Text(
            text,
            style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 0.6.sp),
            color = DC.OnGold
        )
    }
}

/** Minus / count / plus control. */
@Composable
fun QuantityStepper(
    quantity: Int,
    onChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    min: Int = 1
) {
    val haptic = LocalHapticFeedback.current
    Row(
        modifier = modifier
            .clip(CircleShape)
            .border(1.dp, DC.Border, CircleShape)
            .background(DC.Surface),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.SegmentTick)
                onChange(quantity - 1)
            },
            enabled = quantity > min
        ) {
            Icon(Icons.Filled.Remove, contentDescription = "Decrease", tint = if (quantity > min) DC.Ivory else DC.Border)
        }
        Text(
            "$quantity",
            style = MaterialTheme.typography.titleMedium,
            color = DC.Ivory,
            modifier = Modifier.width(24.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        IconButton(onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.SegmentTick)
            onChange(quantity + 1)
        }) {
            Icon(Icons.Filled.Add, contentDescription = "Increase", tint = DC.Gold)
        }
    }
}

/** Shared text field colors for forms. */
@Composable
fun dcFieldColors(): TextFieldColors = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = DC.Gold,
    unfocusedBorderColor = DC.Border,
    focusedLabelColor = DC.Gold,
    unfocusedLabelColor = DC.Muted,
    cursorColor = DC.Gold,
    focusedContainerColor = DC.Canvas,
    unfocusedContainerColor = DC.Canvas,
    focusedTextColor = DC.Ivory,
    unfocusedTextColor = DC.Ivory,
    focusedPlaceholderColor = DC.Muted,
    unfocusedPlaceholderColor = DC.Muted,
    focusedLeadingIconColor = DC.Gold,
    unfocusedLeadingIconColor = DC.Muted
)
