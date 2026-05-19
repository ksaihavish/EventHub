package com.example.eventhub.ui

import com.example.eventhub.models.*
import com.example.eventhub.adapters.*
import com.example.eventhub.data.*
import com.example.eventhub.ui.*
import com.example.eventhub.R
import com.example.eventhub.BuildConfig

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.eventhub.databinding.ActivityProfileBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private val prefManager by lazy { SharedPrefManager.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadProfileData()

        // SAVE BUTTON FUNCTION
        binding.btnSaveProfile.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val phone = binding.etPhone.text.toString().trim()
            val bio = binding.etBio.text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
            } else {
                prefManager.saveProfile(name, phone, bio)
                // Update avatar initial after save
                binding.tvUserInitial.text = name.first().uppercase()
                Toast.makeText(this, "Profile saved!", Toast.LENGTH_SHORT).show()
            }
        }

        // LOGOUT BUTTON FUNCTION
        binding.btnLogout.setOnClickListener {
            // Sign out from Firebase
            FirebaseAuth.getInstance().signOut()

            // Also sign out from Google (so account picker shows next time)
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
            GoogleSignIn.getClient(this, gso).signOut()

            startActivity(Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh event count whenever user returns
        binding.tvEventCount.text = prefManager.getEventCount().toString()
    }

    private fun loadProfileData() {
        val currentUser = FirebaseAuth.getInstance().currentUser

        // Avatar initial
        val displayName = currentUser?.displayName
        val savedName = prefManager.getProfileName()
        val name = when {
            savedName.isNotBlank() -> savedName
            !displayName.isNullOrBlank() -> displayName
            else -> currentUser?.email?.substringBefore("@") ?: "U"
        }

        binding.tvUserInitial.text = name.first().uppercase()

        // Email (read-only)
        val email = currentUser?.email ?: ""
        binding.tvUserEmail.text = email
        binding.etEmail.setText(email)

        // Editable fields
        binding.etName.setText(name)
        binding.etPhone.setText(prefManager.getProfilePhone())
        binding.etBio.setText(prefManager.getProfileBio())

        // Stats
        binding.tvEventCount.text = prefManager.getEventCount().toString()

        // Member since — use Firebase account creation date
        val creationTimestamp = currentUser?.metadata?.creationTimestamp
        if (creationTimestamp != null && creationTimestamp > 0) {
            val dateFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())
            binding.tvMemberSince.text = dateFormat.format(Date(creationTimestamp))
        } else {
            binding.tvMemberSince.text = "—"
        }
    }
}
