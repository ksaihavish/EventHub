package com.example.eventhub.ui

import com.example.eventhub.models.*
import com.example.eventhub.adapters.*
import com.example.eventhub.data.*
import com.example.eventhub.ui.*
import com.example.eventhub.R
import com.example.eventhub.BuildConfig

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.eventhub.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Appearance Logic
        binding.layoutAppearance.setOnClickListener {
            showInfoDialog("Appearance", "Theme is set to System Default. EventHub supports both Light and Dark modes.")
        }

        // 2. Privacy & Security Logic (Restored)
        binding.layoutPrivacy.setOnClickListener {
            showInfoDialog("Privacy & Security", "Your data is stored locally on your device. We do not collect or share your personal event information with external servers.")
        }

        // 3. Feedback Logic (Opens Email)
        binding.layoutFeedback.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf("support@eventhub.com"))
                putExtra(Intent.EXTRA_SUBJECT, "EventHub Feedback")
            }
            try {
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show()
            }
        }

        // 4. Rate App Logic
        binding.layoutRate.setOnClickListener {
            val ratings = arrayOf("⭐", "⭐⭐", "⭐⭐⭐", "⭐⭐⭐⭐", "⭐⭐⭐⭐⭐")
            AlertDialog.Builder(this)
                .setTitle("Rate Our App")
                .setItems(ratings) { _, which ->
                    Toast.makeText(this, "Thank you for the ${which + 1} star rating!", Toast.LENGTH_LONG).show()
                }
                .show()
        }

        // 5. About Logic
        binding.layoutAbout.setOnClickListener {
            showInfoDialog("About", "EventHub v1.0\nDeveloped by Harshitha\nPowered by OSMDroid for Maps.")
        }
    }

    private fun showInfoDialog(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Close", null)
            .show()
    }
}
