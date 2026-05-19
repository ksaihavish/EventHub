package com.example.eventhub

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventhub.databinding.ActivityEventListBinding

class EventListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEventListBinding
    private lateinit var eventAdapter: EventAdapter
    private var allEvents: List<Event> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEventListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupSearch()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize with empty list, click listener, and long-press delete
        eventAdapter = EventAdapter(
            mutableListOf(),
            onItemClick = { selectedEvent ->
                Toast.makeText(this, "Event: ${selectedEvent.title}", Toast.LENGTH_SHORT).show()
            },
            onItemLongClick = { event ->
                showOptionsDialog(event)
            }
        )

        binding.recyclerView.adapter = eventAdapter
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener { text ->
            val query = text.toString().trim().lowercase()
            if (query.isEmpty()) {
                eventAdapter.updateData(allEvents)
            } else {
                val filtered = allEvents.filter {
                    it.title.lowercase().contains(query) ||
                    it.locationName.lowercase().contains(query)
                }
                eventAdapter.updateData(filtered)
            }
            updateEmptyState()
        }
    }

    private fun showOptionsDialog(event: Event) {
        val options = arrayOf("Edit Event", "Delete Event")
        AlertDialog.Builder(this)
            .setTitle(event.title)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        val intent = android.content.Intent(this, AddEventActivity::class.java).apply {
                            putExtra("EDIT_EVENT", event)
                        }
                        startActivity(intent)
                    }
                    1 -> {
                        AlertDialog.Builder(this)
                            .setTitle("Delete Event")
                            .setMessage("Are you sure you want to delete \"${event.title}\"?")
                            .setPositiveButton("Delete") { _, _ ->
                                SharedPrefManager.getInstance(this).deleteEvent(event)
                                FirestoreManager.deleteEvent(event.id)
                                Toast.makeText(this, "Event deleted", Toast.LENGTH_SHORT).show()
                                loadData()
                            }
                            .setNegativeButton("Cancel", null)
                            .show()
                    }
                }
            }
            .show()
    }

    private var listenerRegistration: com.google.firebase.firestore.ListenerRegistration? = null

    override fun onResume() {
        super.onResume()
        loadData()
        
        // Listen to real-time cloud changes
        listenerRegistration = FirestoreManager.listenToEvents { cloudEvents ->
            SharedPrefManager.getInstance(this).syncEventsFromCloud(cloudEvents)
            loadData() // Auto-refresh UI
        }
    }

    override fun onPause() {
        super.onPause()
        listenerRegistration?.remove()
    }

    private fun loadData() {
        allEvents = SharedPrefManager.getInstance(this).getEvents()
        eventAdapter.updateData(allEvents)

        // Update count label
        val count = allEvents.size
        binding.tvEventCount.text = "$count event${if (count != 1) "s" else ""}"

        // Re-apply search filter if there's text in the search bar
        val searchText = binding.etSearch.text.toString().trim()
        if (searchText.isNotEmpty()) {
            val filtered = allEvents.filter {
                it.title.lowercase().contains(searchText.lowercase()) ||
                it.locationName.lowercase().contains(searchText.lowercase())
            }
            eventAdapter.updateData(filtered)
        }

        updateEmptyState()
    }

    private fun updateEmptyState() {
        val hasItems = eventAdapter.itemCount > 0
        binding.tvEmpty.visibility = if (hasItems) View.GONE else View.VISIBLE
        binding.recyclerView.visibility = if (hasItems) View.VISIBLE else View.GONE
    }
}