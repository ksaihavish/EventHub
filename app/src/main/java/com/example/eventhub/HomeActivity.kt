package com.example.eventhub

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.eventhub.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth
import java.util.Calendar

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupGreeting()

        // 1. ADD EVENT (primary CTA)
        binding.btnAddEvent.setOnClickListener {
            startActivity(Intent(this, AddEventActivity::class.java))
        }

        // 2. VIEW EVENT LIST
        binding.btnEvents.setOnClickListener {
            startActivity(Intent(this, EventListActivity::class.java))
        }

        // 3. VIEW EVENTS ON MAP
        binding.btnMap.setOnClickListener {
            startActivity(Intent(this, ViewEventsOnMapActivity::class.java))
        }

        // 4. CHAT ASSISTANT
        binding.btnChat.setOnClickListener {
            startActivity(Intent(this, ChatActivity::class.java))
        }

        // 5. PROFILE
        binding.btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        // 6. SETTINGS
        binding.btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh stats every time user returns
        val count = SharedPrefManager.getInstance(this).getEventCount()
        binding.tvStats.text = "$count events created"
    }

    private fun setupGreeting() {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val greeting = when {
            hour < 12 -> "Good morning 👋"
            hour < 17 -> "Good afternoon 👋"
            else -> "Good evening 👋"
        }
        binding.tvGreeting.text = greeting

        // Show user's display name or email prefix
        val user = FirebaseAuth.getInstance().currentUser
        val displayName = user?.displayName
        val name = if (!displayName.isNullOrBlank()) {
            displayName
        } else {
            user?.email?.substringBefore("@") ?: "User"
        }
        binding.tvUserName.text = name
    }
}