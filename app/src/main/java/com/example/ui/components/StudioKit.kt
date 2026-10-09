package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CrimsonRedBright
import com.example.ui.theme.TextPrimary

/**
 * Shared visual primitives for the AniRecap Studio screens (upload, monitor,
 * settings). They exist so the three pages read as one product: one type scale,
 * one spacing rhythm, one corner radius, and buttons that are always rounded
 * rectangles with a single-line label — never a circle that breaks the words.
 */
object StudioStyle {
    // Spacing rhythm
    val screenPadding: Dp = 20.dp
    val sectionGap: Dp = 18.dp
    val cardGap: Dp = 12.dp
    val cardPadding: Dp = 16.dp

    // Shape language
    val cardRadius: Dp = 18.dp
    val innerRadius: Dp = 12.dp
    val chipRadius: Dp = 8.dp
    val controlHeight: Dp = 46.dp

    // Type scale
    val kickerSize: TextUnit = 10.sp
    val kickerSpacing: TextUnit = 1.4.sp
    val pageTitleSize: TextUnit = 24.sp
    val sectionTitleSize: TextUnit = 16.sp
    val bodySize: TextUnit = 14.sp

    /** Khmer glyphs carry subscripts, so body copy needs generous leading. */
    val bodyLineHeight: TextUnit = 24.sp
    val metaSize: TextUnit = 12.sp
    val controlLabelSize: TextUnit = 13.sp
}

/** A card with a consistent radius, border and inner spacing. */
@Composable
fun StudioCard(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant,
    contentPadding: Dp = StudioStyle.cardPadding,
    verticalSpacing: Dp = StudioStyle.cardGap,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(StudioStyle.cardRadius)
    val handler: (() -> Unit)? = onClick

    // On tappable cards the fill is painted here (underneath `clickable`) and the
    // Surface is left transparent, otherwise the ripple would be hidden behind it.
    Surface(
        modifier = if (handler != null) {
            modifier
                .fillMaxWidth()
                .clip(shape)
                .background(containerColor)
                .clickable(onClick = handler)
        } else {
            modifier.fillMaxWidth()
        },
        shape = shape,
        color = if (handler != null) Color.Transparent else containerColor,
        contentColor = MaterialTheme.colorScheme.onSurface,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier.padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(verticalSpacing),
            content = content
        )
    }
}

/**
 * Small all-caps label above a title. Keeps page/section labels visually
 * subordinate to real content titles.
 */
@Composable
fun StudioKicker(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Text(
        text = text.uppercase(),
        modifier = modifier,
        fontSize = StudioStyle.kickerSize,
        letterSpacing = StudioStyle.kickerSpacing,
        fontWeight = FontWeight.Bold,
        color = color,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

/** Section heading: kicker + title + optional subtitle, with a trailing slot. */
@Composable
fun StudioSectionHeader(
    kicker: String,
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    trailing: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            StudioKicker(kicker)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontSize = StudioStyle.sectionTitleSize,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 22.sp
            )
            if (!subtitle.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    fontSize = StudioStyle.metaSize,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            }
        }
        if (trailing != null) {
            Spacer(modifier = Modifier.width(12.dp))
            trailing()
        }
    }
}

/** Label/value pair with an icon tile — used for BGM, format, tips. */
@Composable
fun StudioInfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    accent: Color = MaterialTheme.colorScheme.primary
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(StudioStyle.innerRadius))
                .background(accent.copy(alpha = 0.16f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(20.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label.uppercase(),
                fontSize = StudioStyle.kickerSize,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Bold,
                color = accent,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = StudioStyle.bodySize,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = StudioStyle.bodyLineHeight
            )
        }
    }
}

/** Neutral label + value chip. Only one accent colour is used inside a card. */
@Composable
fun StudioTag(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.heightIn(min = 28.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(StudioStyle.chipRadius),
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = label.uppercase(),
                fontSize = 9.sp,
                letterSpacing = 0.8.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                fontSize = StudioStyle.metaSize,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/** Compact metadata pill (episode, runtime, model...). */
@Composable
fun StudioMetaChip(
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    tint: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(StudioStyle.chipRadius),
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(13.dp)
                )
            }
            Text(
                text = text,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/** Success/failure pill used for sync states. */
@Composable
fun StudioStatusPill(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.Check
) {
    Surface(
        modifier = modifier,
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(StudioStyle.chipRadius),
        contentColor = color,
        border = BorderStroke(1.dp, color.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = text,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Primary action. Rounded rectangle, fixed height, single-line label so the
 * text can never wrap into "Cop y Mar kers".
 */
@Composable
fun StudioPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(StudioStyle.controlHeight),
        shape = RoundedCornerShape(StudioStyle.innerRadius),
        elevation = null,
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = CrimsonRedBright,
            contentColor = TextPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        StudioButtonContent(text = text, icon = icon)
    }
}

@Composable
fun StudioSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {
    FilledTonalButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(StudioStyle.controlHeight),
        shape = RoundedCornerShape(StudioStyle.innerRadius),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        StudioButtonContent(text = text, icon = icon)
    }
}

@Composable
fun StudioOutlineButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(StudioStyle.controlHeight),
        shape = RoundedCornerShape(StudioStyle.innerRadius),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)
    ) {
        StudioButtonContent(text = text, icon = icon)
    }
}

@Composable
fun StudioTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null
) {
    TextButton(
        onClick = onClick,
        modifier = modifier.height(StudioStyle.controlHeight),
        shape = RoundedCornerShape(StudioStyle.innerRadius),
        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onSurfaceVariant)
    ) {
        StudioButtonContent(text = text, icon = icon)
    }
}

@Composable
private fun RowScope.StudioButtonContent(text: String, icon: ImageVector?) {
    if (icon != null) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
    }
    Text(
        text = text,
        fontSize = StudioStyle.controlLabelSize,
        fontWeight = FontWeight.SemiBold,
        maxLines = 1,
        softWrap = false,
        overflow = TextOverflow.Ellipsis
    )
}

/** Divider that respects the card padding. */
@Composable
fun StudioDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier.fillMaxWidth(),
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.outlineVariant
    )
}

/**
 * Placeholder block for "nothing here yet" states — keeps cards content-height
 * instead of leaving a tall empty box on screen.
 */
@Composable
fun StudioEmptyBlock(
    icon: ImageVector,
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    StudioCard(modifier = modifier) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(StudioStyle.innerRadius))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )
        }
        Text(
            text = title,
            fontSize = StudioStyle.bodySize,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = message,
            fontSize = StudioStyle.metaSize,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 18.sp
        )
        if (actionLabel != null && onAction != null) {
            StudioOutlineButton(
                text = actionLabel,
                onClick = onAction,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/** Left accent rail used on quoted blocks (voiceover lines). */
@Composable
fun StudioQuoteBlock(
    label: String,
    text: String,
    modifier: Modifier = Modifier,
    accent: Color = CrimsonRedBright
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clip(RoundedCornerShape(StudioStyle.innerRadius))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(modifier = Modifier.fillMaxHeight().width(3.dp).background(accent.copy(alpha = 0.85f)))
        Column(modifier = Modifier.weight(1f).padding(12.dp)) {
            Text(
                text = label.uppercase(),
                fontSize = StudioStyle.kickerSize,
                letterSpacing = 1.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = text,
                fontSize = StudioStyle.bodySize,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 26.sp
            )
        }
    }
}
