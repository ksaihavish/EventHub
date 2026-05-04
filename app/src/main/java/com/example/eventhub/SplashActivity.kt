package com.example.eventhub

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.eventhub.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Inflate the layout
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2. Control the duration (3000ms = 3 seconds)
        val splashDuration = 3000L

        Handler(Looper.getMainLooper()).postDelayed({
            // Move to Login
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

            // 3. Smooth transition to avoid black flicker
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)

            // 4. Kill splash so user can't go back
            finish()
        }, splashDuration)
    }
}