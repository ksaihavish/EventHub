package com.example.eventhub.ui

import com.example.eventhub.models.*
import com.example.eventhub.adapters.*
import com.example.eventhub.data.*
import com.example.eventhub.ui.*
import com.example.eventhub.R
import com.example.eventhub.BuildConfig

import android.app.DatePickerDialog
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.eventhub.databinding.ActivityAddEventBinding
import org.json.JSONArray
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.concurrent.thread

class AddEventActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEventBinding
    private var selectedLatitude: Double = 0.0
    private var selectedLongitude: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // OSMDroid config
        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", MODE_PRIVATE))

        binding = ActivityAddEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mapView.setMultiTouchControls(true)
        binding.mapView.controller.setZoom(15.0)

        // Date Picker
        binding.etDate.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(this, { _, y, m, d ->
                binding.etDate.setText(String.format("%04d-%02d-%02d", y, m + 1, d))
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        // Search Action
        binding.etLocation.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                searchLocation(binding.etLocation.text.toString())
                hideKeyboard()
                true
            } else false
        }

        // Manual Selection (Long Press)
        val mReceive = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean = false
            override fun longPressHelper(p: GeoPoint?): Boolean {
                p?.let { updateMapUI(it.latitude, it.longitude, "Manual Pin") }
                return true
            }
        }
        binding.mapView.overlays.add(MapEventsOverlay(mReceive))

        // Edit Mode Check
        val eventToEdit = intent.getSerializableExtra("EDIT_EVENT") as? Event
        if (eventToEdit != null) {
            binding.etTitle.setText(eventToEdit.title)
            binding.etDesc.setText(eventToEdit.description)
            binding.etDate.setText(eventToEdit.date)
            binding.etLocation.setText(eventToEdit.locationName)
            updateMapUI(eventToEdit.latitude, eventToEdit.longitude, eventToEdit.locationName)
            binding.btnSave.text = "Update Event"
        }

        // Save Button
        binding.btnSave.setOnClickListener {
            val title = binding.etTitle.text.toString().trim()
            val date = binding.etDate.text.toString().trim()
            val locName = binding.etLocation.text.toString().trim()

            if (title.isEmpty() || date.isEmpty() || selectedLatitude == 0.0) {
                Toast.makeText(this, "Please enter title, date, and find a location on map", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val newEvent = Event(
                id = eventToEdit?.id ?: java.util.UUID.randomUUID().toString(), // Keep existing ID if editing
                title = title, 
                description = binding.etDesc.text.toString(), 
                date = date, 
                locationName = locName, 
                latitude = selectedLatitude, 
                longitude = selectedLongitude
            )
            
            if (eventToEdit != null) {
                SharedPrefManager.getInstance(this).updateEvent(eventToEdit, newEvent)
                FirestoreManager.saveEvent(newEvent)
                Toast.makeText(this, "Event Updated! ✅", Toast.LENGTH_SHORT).show()
            } else {
                SharedPrefManager.getInstance(this).saveEvent(newEvent)
                FirestoreManager.saveEvent(newEvent)
                Toast.makeText(this, "Event Saved! ✅", Toast.LENGTH_SHORT).show()
            }
            finish()
        }
    }

    private fun searchLocation(query: String) {
        if (query.isEmpty()) return
        Toast.makeText(this, "Searching for coordinates...", Toast.LENGTH_SHORT).show()

        thread {
            var found = false
            // 1. Try Google/System Geocoder
            try {
                val addresses = Geocoder(this, Locale.getDefault()).getFromLocationName(query, 1)
                if (!addresses.isNullOrEmpty()) {
                    updateMapUI(addresses[0].latitude, addresses[0].longitude, query)
                    found = true
                }
            } catch (e: Exception) {}

            // 2. Fallback to OpenStreetMap API
            if (!found) {
                try {
                    val url = URL("https://nominatim.openstreetmap.org/search?q=${query.replace(" ", "+")}&format=json&limit=1")
                    val conn = url.openConnection() as HttpURLConnection
                    conn.setRequestProperty("User-Agent", "EventHub")
                    val resp = conn.inputStream.bufferedReader().readText()
                    val json = JSONArray(resp)
                    if (json.length() > 0) {
                        val obj = json.getJSONObject(0)
                        updateMapUI(obj.getDouble("lat"), obj.getDouble("lon"), query)
                        found = true
                    }
                } catch (e: Exception) {}
            }

            if (!found) runOnUiThread {
                Toast.makeText(this, "Search failed. Please long-press the map to set location.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun updateMapUI(lat: Double, lon: Double, title: String) {
        runOnUiThread {
            selectedLatitude = lat
            selectedLongitude = lon
            binding.mapView.visibility = View.VISIBLE
            val point = GeoPoint(lat, lon)
            binding.mapView.controller.animateTo(point)
            binding.mapView.controller.setZoom(17.5)

            binding.mapView.overlays.removeIf { it is Marker }
            val marker = Marker(binding.mapView)
            marker.position = point
            marker.title = title
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            binding.mapView.overlays.add(marker)
            binding.mapView.invalidate()
            Toast.makeText(this, "Location set at $lat, $lon", Toast.LENGTH_SHORT).show()
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etLocation.windowToken, 0)
    }

    override fun onResume() { super.onResume(); binding.mapView.onResume() }
    override fun onPause() { super.onPause(); binding.mapView.onPause() }
}
