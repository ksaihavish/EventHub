package com.example.eventhub

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.eventhub.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. PROFILE
        binding.btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        // 2. ADD EVENT
        binding.btnAddEvent.setOnClickListener {
            startActivity(Intent(this, AddEventActivity::class.java))
        }

        // 3. VIEW EVENT LIST
        binding.btnEvents.setOnClickListener {
            startActivity(Intent(this, EventListActivity::class.java))
        }

        // 4. VIEW EVENTS ON MAP
        binding.btnMap.setOnClickListener {
            startActivity(Intent(this, ViewEventsOnMapActivity::class.java))
        }

        // 5. SETTINGS
        binding.btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}