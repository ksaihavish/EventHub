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
import com.example.eventhub.databinding.ActivityMapBinding
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class MapActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMapBinding
    private lateinit var map: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // REQUIRED for osmdroid
        Configuration.getInstance().load(
            applicationContext,
            getSharedPreferences("osmdroid", MODE_PRIVATE)
        )
        Configuration.getInstance().userAgentValue = packageName

        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        map = binding.mapView

        // Map setup
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        map.controller.setZoom(18.0) // Zoomed in a bit more for specific events

        // --- NEW LOGIC: RECEIVE DATA FROM ADAPTER ---
        val eventLat = intent.getDoubleExtra("LATITUDE", 0.0)
        val eventLng = intent.getDoubleExtra("LONGITUDE", 0.0)
        val eventName = intent.getStringExtra("EVENT_NAME") ?: "Event Location"

        val targetLocation: GeoPoint

        if (eventLat != 0.0 && eventLng != 0.0) {
            // If we got valid coordinates from the Intent
            targetLocation = GeoPoint(eventLat, eventLng)

            // Add a marker for the specific event
            addMarker(targetLocation, eventName, "Event clicked from list")

        } else {
            // Default: Bengaluru center (if Map is opened directly)
            targetLocation = GeoPoint(12.9716, 77.5946)
            addMarker(targetLocation, "Bengaluru", "Default Center")
        }

        // Move map camera to the target location
        map.controller.setCenter(targetLocation)
    }

    // Helper function to add markers so code stays clean
    private fun addMarker(point: GeoPoint, title: String, snippet: String) {
        val marker = Marker(map)
        marker.position = point
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.title = title
        marker.snippet = snippet

        marker.setOnMarkerClickListener { _, _ ->
            marker.showInfoWindow()
            android.app.AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage("Get directions to this location?")
                .setPositiveButton("Directions") { _, _ ->
                    val uri = android.net.Uri.parse("google.navigation:q=${point.latitude},${point.longitude}")
                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, uri)
                    intent.setPackage("com.google.android.apps.maps")
                    try {
                        startActivity(intent)
                    } catch (e: Exception) {
                        // Fallback if Maps app is not installed
                        val fallbackUri = android.net.Uri.parse("https://www.google.com/maps/dir/?api=1&destination=${point.latitude},${point.longitude}")
                        startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW, fallbackUri))
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
            true
        }

        map.overlays.add(marker)
        map.invalidate() // Refresh the map to show the marker
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }
}
