package com.example.eventhub.ui

import com.example.eventhub.models.*
import com.example.eventhub.adapters.*
import com.example.eventhub.data.*
import com.example.eventhub.ui.*
import com.example.eventhub.R
import com.example.eventhub.BuildConfig

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventhub.databinding.ActivityViewEventsBinding

class ViewEventsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewEventsBinding
    private lateinit var adapter: EventAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewEventsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize adapter with an empty list and a click listener
        adapter = EventAdapter(mutableListOf(), onItemClick = { selectedEvent ->
            // Show a message when an event is clicked in the list
            Toast.makeText(this, "Event: ${selectedEvent.title}", Toast.LENGTH_SHORT).show()
        })

        binding.rvEvents.layoutManager = LinearLayoutManager(this)
        binding.rvEvents.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        refreshList()
    }

    private fun refreshList() {
        // Fetch data from SharedPrefManager
        val updatedEvents = SharedPrefManager.getInstance(this).getEvents()

        // This now works because submitList is inside EventAdapter.kt
        adapter.submitList(updatedEvents)
    }
}
