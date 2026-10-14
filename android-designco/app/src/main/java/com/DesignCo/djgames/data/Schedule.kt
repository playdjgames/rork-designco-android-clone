package com.DesignCo.djgames.data

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/** A day shown in the booking date strip. */
data class DayOption(
    val startMillis: Long,
    val weekday: String,
    val dayOfMonth: Int,
    val monthLabel: String,
    val fullLabel: String,
    val isClosed: Boolean
)

/** Which group of upcoming days the guest wants to see. */
enum class AvailabilityScope(val label: String) {
    NextAvailable("Next available"),
    ThisWeek("This week"),
    ThisWeekend("This weekend")
}

/** Part-of-day filter for the slot grid. */
enum class TimeOfDay(val label: String) {
    Any("Any"),
    Morning("Morning"),
    Afternoon("Afternoon"),
    Evening("Evening");

    fun contains(minutes: Int): Boolean = when (this) {
        Any -> true
        Morning -> minutes < 12 * 60
        Afternoon -> minutes in 12 * 60 until 16 * 60
        Evening -> minutes >= 16 * 60
    }
}

/** Studio opening days and appointment slots. The studio runs Tue–Sat, 9:00 AM – 6:00 PM. */
object Schedule {
    const val OPEN_MIN: Int = 9 * 60
    const val CLOSE_MIN: Int = 18 * 60
    const val SLOT_STEP: Int = 15
    const val MEMBER_HORIZON_DAYS: Int = 21
    const val STANDARD_HORIZON_DAYS: Int = 7

    /** Booking horizon: Beauty Club members see openings 14 days before everyone else. */
    fun horizonDays(isMember: Boolean): Int =
        if (isMember) MEMBER_HORIZON_DAYS else STANDARD_HORIZON_DAYS

    /** All bookable start times on one day, on a 15-minute grid. */
    fun slotsForDay(): List<Int> =
        (OPEN_MIN until CLOSE_MIN step SLOT_STEP).toList()

    fun upcomingDays(count: Int): List<DayOption> {
        val weekday = SimpleDateFormat("EEE", Locale.US)
        val month = SimpleDateFormat("MMMM yyyy", Locale.US)
        val full = SimpleDateFormat("EEE, MMM d", Locale.US)
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return List(count) {
            val dow = cal.get(Calendar.DAY_OF_WEEK)
            val option = DayOption(
                startMillis = cal.timeInMillis,
                weekday = weekday.format(cal.time),
                dayOfMonth = cal.get(Calendar.DAY_OF_MONTH),
                monthLabel = month.format(cal.time),
                fullLabel = full.format(cal.time),
                isClosed = dow == Calendar.SUNDAY || dow == Calendar.MONDAY
            )
            cal.add(Calendar.DAY_OF_MONTH, 1)
            option
        }
    }

    fun formatTime(minutes: Int): String {
        val hour = minutes / 60
        val minute = minutes % 60
        val suffix = if (hour < 12) "AM" else "PM"
        val h12 = if (hour % 12 == 0) 12 else hour % 12
        return String.format(Locale.US, "%d:%02d %s", h12, minute, suffix)
    }

    /** Deterministic pseudo-availability so the calendar feels like a real, busy studio. */
    fun isTaken(day: DayOption, minutes: Int, stylistSeed: Int): Boolean =
        (day.dayOfMonth * 7 + minutes / SLOT_STEP + stylistSeed * 3) % 5 == 0

    fun isPast(day: DayOption, minutes: Int, now: Long): Boolean =
        day.startMillis + minutes * 60_000L <= now

    /** True when every slot of the day is either taken or already past. */
    fun isFullyBooked(day: DayOption, stylistSeed: Int, now: Long): Boolean =
        !day.isClosed && slotsForDay().all { isTaken(day, it, stylistSeed) || isPast(day, it, now) }

    /** Days shown for a scope, honoring closed days and the booking horizon. */
    fun daysForScope(scope: AvailabilityScope, days: List<DayOption>, isMember: Boolean): List<DayOption> {
        val horizon = days.take(horizonDays(isMember))
        return when (scope) {
            AvailabilityScope.NextAvailable -> horizon
            AvailabilityScope.ThisWeek -> horizon.take(7)
            AvailabilityScope.ThisWeekend -> {
                val saturday = horizon.firstOrNull { it.weekday == "Sat" }
                val sunday = horizon.firstOrNull { it.weekday == "Sun" }
                listOfNotNull(saturday, sunday).ifEmpty { horizon.take(2) }
            }
        }
    }
}
