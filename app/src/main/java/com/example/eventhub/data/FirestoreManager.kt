package com.example.eventhub.data

import com.example.eventhub.models.*
import com.example.eventhub.adapters.*
import com.example.eventhub.data.*
import com.example.eventhub.ui.*
import com.example.eventhub.R
import com.example.eventhub.BuildConfig

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

object FirestoreManager {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun getUserCollection() = auth.currentUser?.uid?.let { uid ->
        db.collection("users").document(uid).collection("events")
    }

    // Save or update an event in Firestore
    fun saveEvent(event: Event) {
        getUserCollection()?.document(event.id)?.set(event)
    }

    // Delete an event
    fun deleteEvent(eventId: String) {
        getUserCollection()?.document(eventId)?.delete()
    }

    // Listen to real-time changes (shows updates instantly across devices)
    fun listenToEvents(onEventsUpdated: (List<Event>) -> Unit): ListenerRegistration? {
        val collection = getUserCollection() ?: return null
        return collection.addSnapshotListener { snapshot, error ->
            if (error != null) return@addSnapshotListener
            if (snapshot != null) {
                val events = snapshot.documents.mapNotNull { it.toObject(Event::class.java) }
                onEventsUpdated(events)
            }
        }
    }
}
