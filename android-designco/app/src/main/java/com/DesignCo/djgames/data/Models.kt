package com.DesignCo.djgames.data

import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

/** Which side of the studio a booking belongs to. */
@Serializable
enum class ServiceKind(val label: String) {
    Salon("Salon"),
    Barber("Barber")
}

/** Retail shelf categories shown as filter chips in the shop. */
enum class ProductCategory(val label: String) {
    Hair("Hair"),
    Skin("Skin"),
    Beard("Beard"),
    Tools("Tools")
}

/** A retail product sold in the DesignCo shop. */
data class Product(
    val id: String,
    val name: String,
    val price: Int,
    val category: ProductCategory,
    val imageUrl: String,
    val tagline: String,
    val description: String,
    val size: String,
    val howToUse: String,
    val isNew: Boolean = false
)

/** A bookable salon or barber service. */
data class BeautyService(
    val id: String,
    val name: String,
    val price: Int,
    val durationMinutes: Int,
    val kind: ServiceKind,
    val icon: ImageVector,
    val description: String,
    /** Recommended weeks between visits, used for rebooking reminders. */
    val rebookWeeks: Int
)

/** A team member who can be booked. */
data class Stylist(
    val id: String,
    val name: String,
    val title: String,
    val imageUrl: String,
    val kind: ServiceKind
)

/** Beauty Club membership plans. */
@Serializable
enum class ClubPlan(val label: String, val priceLabel: String, val note: String) {
    Monthly("Monthly", "$12", "/ month"),
    Annual("Annual", "$99", "/ year")
}

/** A saved inspiration look a guest can share with their stylist. */
data class InspirationItem(
    val id: String,
    val title: String,
    val imageUrl: String
)

/** A confirmed appointment saved on device. */
@Serializable
data class Appointment(
    val id: String,
    val kind: ServiceKind,
    val serviceName: String,
    val price: Int,
    val stylistName: String,
    val dayLabel: String,
    val timeLabel: String,
    val dateMillis: Long,
    val durationMinutes: Int = 60
)

/** A day the guest is waitlisted for because it is fully booked. */
@Serializable
data class WaitlistEntry(
    val dayStartMillis: Long,
    val dayLabel: String,
    val joinedAt: Long,
    val spotOpen: Boolean = false
)

/** A placed retail order. */
@Serializable
data class Order(
    val id: String,
    val summary: String,
    val itemCount: Int,
    val total: Double,
    val placedAt: Long
)

/** Locally stored customer profile. */
@Serializable
data class UserProfile(
    val name: String,
    val email: String,
    val phone: String = ""
)

/** Everything DesignCo persists between launches. */
@Serializable
data class AppData(
    val user: UserProfile? = null,
    val clubPlan: ClubPlan? = null,
    val clubSince: Long? = null,
    val cart: Map<String, Int> = emptyMap(),
    val appointments: List<Appointment> = emptyList(),
    val orders: List<Order> = emptyList(),
    val remindersOn: Boolean = true,
    val favoriteServiceIds: List<String> = emptyList(),
    val inspirationIds: List<String> = emptyList(),
    val sharedInspirationIds: List<String> = emptyList(),
    val waitlist: List<WaitlistEntry> = emptyList()
) {
    val isMember: Boolean get() = clubPlan != null
    val cartCount: Int get() = cart.values.sum()

    val subtotal: Double
        get() = cart.entries.sumOf { (id, qty) -> (Catalog.product(id)?.price ?: 0) * qty.toDouble() }

    val discount: Double get() = if (isMember) subtotal * Catalog.MEMBER_DISCOUNT else 0.0
    val total: Double get() = subtotal - discount

    fun upcomingAppointments(now: Long): List<Appointment> =
        appointments.filter { it.dateMillis > now }.sortedBy { it.dateMillis }

    fun pastAppointments(now: Long): List<Appointment> =
        appointments.filter { it.dateMillis <= now }.sortedByDescending { it.dateMillis }

    /** A service the guest should rebook soon: a past visit whose recommended interval has elapsed. */
    fun rebookingSuggestion(now: Long): Appointment? {
        val upcomingNames = upcomingAppointments(now).map { it.serviceName }.toSet()
        return pastAppointments(now)
            .filter { it.dateMillis + it.durationMinutes * 60_000L + 0 > 0 }
            .firstOrNull { appt ->
                appt.serviceName !in upcomingNames && rebookDue(appt, now)
            }
    }

    private fun rebookDue(appt: Appointment, now: Long): Boolean {
        val weeks = Catalog.services.firstOrNull { it.name == appt.serviceName }?.rebookWeeks ?: return false
        return appt.dateMillis + weeks * 7 * 24 * 60 * 60_000L <= now
    }
}
