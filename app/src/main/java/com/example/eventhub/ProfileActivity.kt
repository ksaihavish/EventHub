package com.example.eventhub

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.eventhub.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 🔥 SAVE BUTTON FUNCTION
        binding.btnSaveProfile.setOnClickListener {

            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()

            if (name.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Enter all fields", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Saved: $name", Toast.LENGTH_SHORT).show()
            }
        }

        // 🔥 LOGOUT BUTTON FUNCTION
        binding.btnLogout.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}