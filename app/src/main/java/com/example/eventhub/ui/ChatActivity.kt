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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventhub.databinding.ActivityChatBinding
import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.launch

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var chatAdapter: ChatAdapter
    
    // IMPORTANT: Replace this with your actual Gemini API Key from Google AI Studio
    // Use BuildConfig to safely load the API Key from local.properties
    private val GEMINI_API_KEY = BuildConfig.GEMINI_API_KEY
    
    private var aiChat: Chat? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup RecyclerView
        chatAdapter = ChatAdapter(mutableListOf())
        binding.rvChat.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true // Start from bottom like a real chat app
        }
        binding.rvChat.adapter = chatAdapter

        // Initial welcome message
        addBotMessage("Hi! I am the EventHub AI Assistant. Ask me to recommend events, plan an itinerary, or write an event description for you!")

        // Initialize AI model if key is provided
        if (GEMINI_API_KEY != "YOUR_API_KEY_HERE" && GEMINI_API_KEY.isNotBlank()) {
            initGemini()
        } else {
            addBotMessage("⚠️ Please put your Gemini API Key in ChatActivity.kt to activate my AI brain!")
        }

        binding.btnSend.setOnClickListener {
            val userText = binding.etChatInput.text.toString().trim()
            if (userText.isNotEmpty()) {
                // Prevent button mashing
                binding.btnSend.isEnabled = false
                
                // Add user message to UI
                addUserMessage(userText)
                binding.etChatInput.text.clear()

                // Process AI response
                if (aiChat != null) {
                    processAIResponse(userText)
                } else {
                    addBotMessage("I'm currently running in offline mode. Please add an API key for full AI functionality.")
                    binding.btnSend.isEnabled = true
                }
            }
        }
    }

    private fun initGemini() {
        try {
            // Get user's events to give the AI context about what the user is doing
            val events = SharedPrefManager.getInstance(this).getEvents()
            val eventContext = if (events.isEmpty()) {
                "The user currently has no events planned."
            } else {
                "The user's planned events: " + events.joinToString("; ") { "${it.title} at ${it.locationName} on ${it.date}" }
            }

            // Using flash-lite which is highly efficient and has a massive free-tier quota
            val generativeModel = GenerativeModel(
                modelName = "gemini-3.1-flash-lite",
                apiKey = GEMINI_API_KEY
            )

            // Inject the system instruction as the very first hidden chat turn
            val chatHistory = listOf(
                content("user") { 
                    text("You are the EventHub AI Assistant, an expert in event planning. Be concise, friendly, and helpful. Here is the context about the user's current events: $eventContext. Do you understand?") 
                },
                content("model") { 
                    text("Understood! I am ready to help the user with their events.") 
                }
            )

            aiChat = generativeModel.startChat(history = chatHistory)
            
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to initialize AI: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun processAIResponse(userText: String) {
        lifecycleScope.launch {
            try {
                // Show a typing indicator (simple version)
                val typingMsg = ChatMessage("typing...", false)
                chatAdapter.addMessage(typingMsg)
                binding.rvChat.scrollToPosition(chatAdapter.itemCount - 1)

                // Send to Gemini
                val response = aiChat?.sendMessage(userText)
                
                // Remove typing indicator
                chatAdapter.removeLastMessage()
                
                // Add real response
                response?.text?.let {
                    addBotMessage(it)
                }
            } catch (e: Exception) {
                chatAdapter.removeLastMessage()
                val errorMsg = if (e is java.net.UnknownHostException || e.message?.contains("network", ignoreCase = true) == true || e.message?.contains("resolve host", ignoreCase = true) == true) {
                    "I seem to be offline right now. Please connect to the internet so my AI brain can work!"
                } else {
                    "Sorry, I had trouble thinking of a response. Please try again.\n\n(Error Details: ${e.message})"
                }
                addBotMessage(errorMsg)
            } finally {
                // Re-enable the send button
                binding.btnSend.isEnabled = true
            }
        }
    }

    private fun addUserMessage(text: String) {
        chatAdapter.addMessage(ChatMessage(text, true))
        binding.rvChat.smoothScrollToPosition(chatAdapter.itemCount - 1)
    }

    private fun addBotMessage(text: String) {
        chatAdapter.addMessage(ChatMessage(text, false))
        binding.rvChat.smoothScrollToPosition(chatAdapter.itemCount - 1)
    }
}
