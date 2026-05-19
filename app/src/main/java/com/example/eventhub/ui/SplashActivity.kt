package com.example.eventhub.ui

import com.example.eventhub.models.*
import com.example.eventhub.adapters.*
import com.example.eventhub.data.*
import com.example.eventhub.ui.*
import com.example.eventhub.R
import com.example.eventhub.BuildConfig

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.eventhub.databinding.ActivitySplashBinding
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Inflate the layout
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2. Control the duration (2500ms = 2.5 seconds)
        val splashDuration = 2500L

        Handler(Looper.getMainLooper()).postDelayed({
            // 3. Check if user is already logged in
            val currentUser = FirebaseAuth.getInstance().currentUser
            val destination = if (currentUser != null) {
                HomeActivity::class.java
            } else {
                LoginActivity::class.java
            }

            startActivity(Intent(this, destination))

            // 4. Smooth transition to avoid black flicker
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)

            // 5. Kill splash so user can't go back
            finish()
        }, splashDuration)
    }
}
