package com.DesignCo.djgames.data

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Reviewer demo account, mirroring the one registered for the iOS App Store review.
 * Signing in with these credentials loads a fully populated account so every feature
 * (Gold Pass, appointments, rebooking, favorites, orders) can be reviewed immediately.
 */
object DemoAccount {
    const val EMAIL: String = "appreview@designco.app"
    const val PASSWORD: String = "DesignCo2026!"

    fun matchesEmail(email: String): Boolean = email.trim().equals(EMAIL, ignoreCase = true)

    fun isValid(email: String, password: String): Boolean = matchesEmail(email) && password == PASSWORD

    /** Seed data for the demo account, dated relative to [now] so it always looks current. */
    fun seed(current: AppData, now: Long = System.currentTimeMillis()): AppData {
        val day = 24L * 60 * 60 * 1000
        return current.copy(
            user = UserProfile(name = "App Review", email = EMAIL, phone = "(555) 014-2026"),
            clubPlan = ClubPlan.Annual,
            clubSince = now - 90 * day,
            appointments = listOf(
                appointment("demo-up-1", ServiceKind.Salon, "Signature Cut & Style", 85, "Ava", 60, now, 5, 10, 30),
                appointment("demo-up-2", ServiceKind.Barber, "Beard Sculpt", 30, "Marcus", 30, now, 12, 14, 0),
                appointment("demo-past-1", ServiceKind.Salon, "Full Color", 140, "Sofia", 120, now, -42, 11, 0),
                appointment("demo-past-2", ServiceKind.Salon, "Blowout", 55, "Nia", 45, now, -20, 15, 15)
            ),
            orders = listOf(
                Order("DC-20418", "Gold Hour Repair Serum + 1 more", 2, 71.10, now - 14 * day)
            ),
            favoriteServiceIds = listOf("cut-style", "full-color", "beard-sculpt"),
            inspirationIds = listOf("insp-1", "insp-3", "insp-5"),
            sharedInspirationIds = listOf("insp-1"),
            waitlist = emptyList()
        )
    }

    private fun appointment(
        id: String, kind: ServiceKind, service: String, price: Int, stylist: String,
        duration: Int, now: Long, dayOffset: Int, hour: Int, minute: Int
    ): Appointment {
        val cal = Calendar.getInstance().apply {
            timeInMillis = now
            add(Calendar.DAY_OF_YEAR, dayOffset)
            // Studio is closed Sunday and Monday; slide forward to Tuesday.
            while (get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return Appointment(
            id = id,
            kind = kind,
            serviceName = service,
            price = price,
            stylistName = stylist,
            dayLabel = SimpleDateFormat("EEE, MMM d", Locale.US).format(cal.time),
            timeLabel = Schedule.formatTime(hour * 60 + minute),
            dateMillis = cal.timeInMillis,
            durationMinutes = duration
        )
    }
}
