package com.DesignCo.djgames.data

import android.content.Context
import android.util.Log
import kotlinx.serialization.json.Json

/** Small JSON-in-SharedPreferences store for the customer's bag, bookings and membership. */
class AppStore(context: Context) {
    private val prefs = context.getSharedPreferences("designco_store", Context.MODE_PRIVATE)
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    fun load(): AppData {
        val raw = prefs.getString(KEY, null) ?: return AppData()
        return runCatching { json.decodeFromString(AppData.serializer(), raw) }
            .onFailure { Log.w("AppStore", "Could not restore saved state, starting fresh") }
            .getOrDefault(AppData())
    }

    fun save(data: AppData) {
        prefs.edit().putString(KEY, json.encodeToString(AppData.serializer(), data)).apply()
    }

    private companion object {
        const val KEY = "state_v1"
    }
}
