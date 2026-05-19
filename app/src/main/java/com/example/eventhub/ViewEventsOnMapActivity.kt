package com.example.eventhub

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventhub.databinding.ActivityViewEventsOnMapBinding
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker

class ViewEventsOnMapActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewEventsOnMapBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", MODE_PRIVATE))
        binding = ActivityViewEventsOnMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val events = SharedPrefManager.getInstance(this).getEvents()

        binding.mapView.setMultiTouchControls(true)
        binding.rvMapEvents.layoutManager = LinearLayoutManager(this)

        binding.rvMapEvents.adapter = EventAdapter(events, onItemClick = { event ->
            focusOnEvent(event)
        })

        events.forEach { event ->
            val marker = Marker(binding.mapView)
            marker.position = GeoPoint(event.latitude, event.longitude)
            marker.title = event.title
            marker.snippet = event.locationName
            
            marker.setOnMarkerClickListener { _, _ ->
                marker.showInfoWindow()
                android.app.AlertDialog.Builder(this@ViewEventsOnMapActivity)
                    .setTitle(event.title)
                    .setMessage("Get directions to this event?")
                    .setPositiveButton("Directions") { _, _ ->
                        val uri = android.net.Uri.parse("google.navigation:q=${event.latitude},${event.longitude}")
                        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, uri)
                        intent.setPackage("com.google.android.apps.maps")
                        try {
                            startActivity(intent)
                        } catch (e: Exception) {
                            val fallbackUri = android.net.Uri.parse("https://www.google.com/maps/dir/?api=1&destination=${event.latitude},${event.longitude}")
                            startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW, fallbackUri))
                        }
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
                true
            }
            binding.mapView.overlays.add(marker)
        }

        if (events.isNotEmpty()) focusOnEvent(events[0])
    }

    private fun focusOnEvent(event: Event) {
        if (event.latitude == 0.0) return
        val point = GeoPoint(event.latitude, event.longitude)
        binding.mapView.controller.animateTo(point)
        binding.mapView.controller.setZoom(18.0)

        binding.mapView.overlays.forEach {
            if (it is Marker && it.position.latitude == point.latitude) {
                it.showInfoWindow()
            }
        }
    }

    override fun onResume() { super.onResume(); binding.mapView.onResume() }
    override fun onPause() { super.onPause(); binding.mapView.onPause() }
}
