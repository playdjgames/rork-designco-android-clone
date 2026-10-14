package com.DesignCo.djgames.util

import java.util.Locale
import kotlin.math.roundToLong

/** Formats dollars, dropping cents when the amount is whole ($48 vs $43.20). */
fun formatPrice(value: Double): String {
    val cents = (value * 100).roundToLong()
    return if (cents % 100 == 0L) "$${cents / 100}" else String.format(Locale.US, "$%.2f", cents / 100.0)
}

fun formatPrice(value: Int): String = "$$value"

/** Human duration used on service cards: "1 hr", "2 hrs", "45 min". */
fun formatDuration(minutes: Int): String = when {
    minutes < 60 -> "${minutes} min"
    minutes == 60 -> "1 hr"
    else -> "${minutes / 60} hrs"
}
