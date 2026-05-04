package com.example.eventhub

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPrefManager private constructor(context: Context) {
    private val prefs = context.getSharedPreferences("EventHubPrefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        @Volatile
        private var instance: SharedPrefManager? = null
        fun getInstance(context: Context): SharedPrefManager {
            return instance ?: synchronized(this) {
                instance ?: SharedPrefManager(context).also { instance = it }
            }
        }
    }

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
}