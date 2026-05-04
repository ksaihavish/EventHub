package com.example.eventhub

import android.os.Bundle
import android.widget.Toast // 1. Added missing Toast import
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventhub.databinding.ActivityEventListBinding

class EventListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEventListBinding
    private lateinit var eventAdapter: EventAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 2. Setup ViewBinding
        binding = ActivityEventListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 3. Setup RecyclerView
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize with empty list and the click listener required by EventAdapter
        eventAdapter = EventAdapter(mutableListOf()) { selectedEvent ->
            // This now works because of the Toast import added above
            Toast.makeText(this, "Event: ${selectedEvent.title}", Toast.LENGTH_SHORT).show()
        }

        binding.recyclerView.adapter = eventAdapter
    }

    override fun onResume() {
        super.onResume()
        // Refresh data every time the user returns to this screen
        loadData()
    }

    private fun loadData() {
        // 4. Get data from your SharedPrefManager
        val events = SharedPrefManager.getInstance(this).getEvents()

        // 5. Update the adapter with the new list using updateData
        eventAdapter.updateData(events)
    }
}