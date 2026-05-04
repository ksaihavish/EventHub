package com.example.eventhub

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.semantics.text
import com.example.eventhub.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSend.setOnClickListener {
            val userText = binding.etChatInput.text.toString().lowercase()
            val response = processNLP(userText)
            binding.tvChatResponse.text = "Bot: $response"
            binding.etChatInput.text.clear()
        }
    }

    // This is your "Core Idea" logic
    private fun processNLP(input: String): String {
        return when {
            input.contains("hello") || input.contains("hi") ->
                "Hello! I am your EventHub assistant. How can I help?"

            input.contains("music") || input.contains("concert") ->
                "I found 'Music Fest' in your list! Click on it to see the Map."

            input.contains("map") || input.contains("location") ->
                "To see a location, just click any event in your list. I will open the map for you!"

            input.contains("profile") || input.contains("edit") ->
                "You can change your name in the Profile section."

            else -> "I'm still learning. Try asking about 'Music events' or 'Maps'!"
        }
    }
}