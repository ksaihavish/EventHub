package com.example.eventhub.adapters

import com.example.eventhub.models.*
import com.example.eventhub.adapters.*
import com.example.eventhub.data.*
import com.example.eventhub.ui.*
import com.example.eventhub.R
import com.example.eventhub.BuildConfig

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.eventhub.databinding.ItemChatBotBinding
import com.example.eventhub.databinding.ItemChatUserBinding

class ChatAdapter(private val messages: MutableList<ChatMessage>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_USER = 1
        private const val VIEW_TYPE_BOT = 2
    }

    fun addMessage(message: ChatMessage) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }

    fun removeLastMessage() {
        if (messages.isNotEmpty()) {
            val position = messages.size - 1
            messages.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isUser) VIEW_TYPE_USER else VIEW_TYPE_BOT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_USER) {
            val binding = ItemChatUserBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            UserViewHolder(binding)
        } else {
            val binding = ItemChatBotBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            BotViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val msg = messages[position]
        if (holder is UserViewHolder) {
            holder.binding.tvMessage.text = msg.text
        } else if (holder is BotViewHolder) {
            holder.binding.tvMessage.text = msg.text
        }
    }

    override fun getItemCount() = messages.size

    class UserViewHolder(val binding: ItemChatUserBinding) : RecyclerView.ViewHolder(binding.root)
    class BotViewHolder(val binding: ItemChatBotBinding) : RecyclerView.ViewHolder(binding.root)
}
