package com.example.eventhub

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPrefManager private constructor(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences("EventHubPrefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        @Volatile
        private var instance: SharedPrefManager? = null
        fun getInstance(context: Context): SharedPrefManager {
            return instance ?: synchronized(this) {
                instance ?: SharedPrefManager(context.applicationContext).also { instance = it }
            }
        }
    }

    // ── Events ──

    fun saveEvent(event: Event) {
        val list = getEvents().toMutableList()
        list.add(event)
        prefs.edit().putString("events", gson.toJson(list)).apply()
    }

    fun getEvents(): List<Event> {
        val json = prefs.getString("events", null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<Event>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun deleteEvent(event: Event) {
        val list = getEvents().toMutableList()
        list.removeAll { it.id == event.id } // Use ID for safer deletion
        prefs.edit().putString("events", gson.toJson(list)).apply()
    }

    fun syncEventsFromCloud(cloudEvents: List<Event>) {
        prefs.edit().putString("events", gson.toJson(cloudEvents)).apply()
    }

    fun updateEvent(oldEvent: Event, newEvent: Event) {
        val list = getEvents().toMutableList()
        val index = list.indexOfFirst { it.id == oldEvent.id }
        if (index != -1) {
            list[index] = newEvent
            prefs.edit().putString("events", gson.toJson(list)).apply()
        }
    }

    fun getEventCount(): Int = getEvents().size

    // ── Profile ──

    fun saveProfile(name: String, phone: String, bio: String) {
        prefs.edit()
            .putString("profile_name", name)
            .putString("profile_phone", phone)
            .putString("profile_bio", bio)
            .apply()
    }

    fun getProfileName(): String = prefs.getString("profile_name", "") ?: ""
    fun getProfilePhone(): String = prefs.getString("profile_phone", "") ?: ""
    fun getProfileBio(): String = prefs.getString("profile_bio", "") ?: ""
}