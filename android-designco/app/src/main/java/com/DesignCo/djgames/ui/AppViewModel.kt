package com.DesignCo.djgames.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.DesignCo.djgames.data.AppData
import com.DesignCo.djgames.data.AppStore
import com.DesignCo.djgames.data.Appointment
import com.DesignCo.djgames.data.Catalog
import com.DesignCo.djgames.data.ClubPlan
import com.DesignCo.djgames.data.DemoAccount
import com.DesignCo.djgames.data.Order
import com.DesignCo.djgames.data.Product
import com.DesignCo.djgames.data.ServiceKind
import com.DesignCo.djgames.data.UserProfile
import com.DesignCo.djgames.data.WaitlistEntry
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** Optional action attached to a snackbar message. */
enum class MessageAction(val label: String) {
    ViewBag("View bag"),
    OpenClub("Join"),
    OpenAppointments("View"),
    OpenBook("Book")
}

data class AppMessage(val text: String, val action: MessageAction? = null)

/** Single source of truth for the bag, bookings, membership, favorites and account. */
class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val store = AppStore(application)

    private val _data = MutableStateFlow(store.load())
    val data: StateFlow<AppData> = _data.asStateFlow()

    private val _bookingKind = MutableStateFlow(ServiceKind.Salon)
    val bookingKind: StateFlow<ServiceKind> = _bookingKind.asStateFlow()

    private val _messages = MutableSharedFlow<AppMessage>(extraBufferCapacity = 4)
    val messages: SharedFlow<AppMessage> = _messages.asSharedFlow()

    private fun mutate(block: (AppData) -> AppData) {
        _data.update(block)
        store.save(_data.value)
    }

    fun post(text: String, action: MessageAction? = null) {
        _messages.tryEmit(AppMessage(text, action))
    }

    fun setBookingKind(kind: ServiceKind) {
        _bookingKind.value = kind
    }

    /** Deliver any waitlist spot-open alerts queued since the last launch. */
    fun deliverWaitlistAlerts() {
        val current = _data.value
        val ready = current.waitlist.filter { !it.spotOpen && it.joinedAt + SPOT_OPEN_DELAY < System.currentTimeMillis() }
        if (ready.isEmpty()) return
        mutate { d ->
            d.copy(waitlist = d.waitlist.map { entry ->
                if (ready.any { it.dayStartMillis == entry.dayStartMillis }) entry.copy(spotOpen = true) else entry
            })
        }
        ready.firstOrNull()?.let {
            post("A spot opened on ${it.dayLabel} — claim it before it's gone", MessageAction.OpenAppointments)
        }
    }

    fun addToCart(product: Product, quantity: Int = 1) {
        mutate { d -> d.copy(cart = d.cart + (product.id to ((d.cart[product.id] ?: 0) + quantity))) }
        post("${product.name} added to your bag", MessageAction.ViewBag)
    }

    fun setQuantity(productId: String, quantity: Int) {
        mutate { d ->
            val next = d.cart.toMutableMap()
            if (quantity <= 0) next.remove(productId) else next[productId] = quantity
            d.copy(cart = next)
        }
    }

    fun checkout(): Order? {
        val current = _data.value
        val lines = current.cart.mapNotNull { (id, qty) -> Catalog.product(id)?.let { it to qty } }
        if (lines.isEmpty()) return null
        val order = Order(
            id = "DC-${(10000..99999).random()}",
            summary = lines.joinToString { "${it.second}× ${it.first.name}" },
            itemCount = lines.sumOf { it.second },
            total = current.total,
            placedAt = System.currentTimeMillis()
        )
        mutate { it.copy(cart = emptyMap(), orders = listOf(order) + it.orders) }
        return order
    }

    fun book(appointment: Appointment) {
        mutate { it.copy(appointments = it.appointments + appointment) }
    }

    fun reschedule(id: String, dayLabel: String, timeLabel: String, dateMillis: Long) {
        mutate { d ->
            d.copy(appointments = d.appointments.map { appt ->
                if (appt.id == id) appt.copy(dayLabel = dayLabel, timeLabel = timeLabel, dateMillis = dateMillis) else appt
            })
        }
        post("Appointment moved to $dayLabel, $timeLabel")
    }

    fun cancelAppointment(id: String) {
        mutate { d -> d.copy(appointments = d.appointments.filterNot { it.id == id }) }
        post("Appointment cancelled")
    }

    fun toggleServiceFavorite(serviceId: String) {
        mutate { d ->
            val saved = serviceId in d.favoriteServiceIds
            d.copy(favoriteServiceIds = if (saved) d.favoriteServiceIds - serviceId else d.favoriteServiceIds + serviceId)
        }
    }

    fun toggleInspiration(inspirationId: String) {
        mutate { d ->
            val saved = inspirationId in d.inspirationIds
            d.copy(
                inspirationIds = if (saved) d.inspirationIds - inspirationId else d.inspirationIds + inspirationId,
                sharedInspirationIds = if (saved) d.sharedInspirationIds - inspirationId else d.sharedInspirationIds
            )
        }
    }

    fun shareInspiration(inspirationId: String) {
        mutate { d ->
            d.copy(sharedInspirationIds = (d.sharedInspirationIds + inspirationId).distinct())
        }
        val title = Catalog.inspirationItem(inspirationId)?.title ?: "Your inspiration"
        post("\"$title\" shared with your stylist")
    }

    fun joinWaitlist(entry: WaitlistEntry) {
        mutate { d ->
            if (d.waitlist.any { it.dayStartMillis == entry.dayStartMillis }) d else d.copy(waitlist = d.waitlist + entry)
        }
        post("You're on the waitlist for ${entry.dayLabel}. We'll alert you the moment a spot opens.")
    }

    fun leaveWaitlist(dayStartMillis: Long) {
        mutate { d -> d.copy(waitlist = d.waitlist.filterNot { it.dayStartMillis == dayStartMillis }) }
        post("Left the waitlist")
    }

    fun claimWaitlistSpot(dayStartMillis: Long) {
        mutate { d -> d.copy(waitlist = d.waitlist.filterNot { it.dayStartMillis == dayStartMillis }) }
    }

    fun joinClub(plan: ClubPlan) {
        mutate { d -> d.copy(clubPlan = plan, clubSince = d.clubSince ?: System.currentTimeMillis()) }
    }

    fun leaveClub() {
        mutate { it.copy(clubPlan = null) }
        post("Membership cancelled")
    }

    fun createAccount(profile: UserProfile) {
        mutate { it.copy(user = profile) }
        post("Welcome, ${profile.name.substringBefore(' ')}")
    }

    /**
     * Signs in locally. The reviewer demo email must use its exact password and loads a
     * populated account; returns false when demo credentials are wrong.
     */
    fun signIn(email: String, password: String): Boolean {
        if (DemoAccount.matchesEmail(email)) {
            if (!DemoAccount.isValid(email, password)) return false
            mutate { DemoAccount.seed(it) }
            post("Signed in as App Review")
            return true
        }
        signIn(email)
        return true
    }

    private fun signIn(email: String) {
        val name = email.substringBefore('@')
            .replace('.', ' ')
            .split(' ')
            .filter { it.isNotBlank() }
            .joinToString(" ") { part -> part.replaceFirstChar { it.uppercase() } }
            .ifBlank { "Guest" }
        mutate { it.copy(user = UserProfile(name = name, email = email.trim())) }
        post("Signed in as $name")
    }

    fun signOut() {
        mutate { it.copy(user = null) }
        post("Signed out")
    }

    /** Permanently erases the profile and every saved booking, order, favorite and membership. */
    fun deleteAccount() {
        mutate { d -> AppData(remindersOn = d.remindersOn) }
        post("Your account and data were deleted")
    }

    fun setReminders(enabled: Boolean) {
        mutate { it.copy(remindersOn = enabled) }
    }

    private companion object {
        /** A waitlisted day "opens up" this long after joining, so the alert feels live. */
        const val SPOT_OPEN_DELAY: Long = 45_000L
    }
}
