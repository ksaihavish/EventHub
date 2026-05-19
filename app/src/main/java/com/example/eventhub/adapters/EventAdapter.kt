package com.example.eventhub.adapters

import com.example.eventhub.models.*
import com.example.eventhub.adapters.*
import com.example.eventhub.data.*
import com.example.eventhub.ui.*
import com.example.eventhub.R
import com.example.eventhub.BuildConfig

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.eventhub.databinding.ItemEventBinding

class EventAdapter(
    private var eventList: List<Event>,
    private val onItemClick: (Event) -> Unit,
    private val onItemLongClick: ((Event) -> Unit)? = null
) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    fun submitList(newList: List<Event>) {
        this.eventList = newList
        notifyDataSetChanged()
    }

    fun updateData(newList: List<Event>) {
        this.eventList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = eventList[position]
        holder.bind(event, onItemClick, onItemLongClick)
    }

    override fun getItemCount(): Int = eventList.size

    class EventViewHolder(private val binding: ItemEventBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(event: Event, clickListener: (Event) -> Unit, longClickListener: ((Event) -> Unit)?) {
            // Mapping data to UI
            binding.tvTitle.text = event.title
            binding.tvDesc.text = event.locationName
            binding.tvDate.text = event.date

            // Handle the click to go to Maps
            binding.root.setOnClickListener {
                // 1. First, trigger the original click listener (for logging or other logic)
                clickListener(event)

                // 2. Automatically lead to MapActivity
                val context = binding.root.context
                val intent = Intent(context, MapActivity::class.java)

                // Ensure your Event class has these fields (latitude/longitude)
                // If your Event class uses 'lat' and 'lng', change these names:
                intent.putExtra("LATITUDE", event.latitude)
                intent.putExtra("LONGITUDE", event.longitude)
                intent.putExtra("EVENT_NAME", event.title)

                context.startActivity(intent)
            }

            // Handle long press for delete
            binding.root.setOnLongClickListener {
                longClickListener?.invoke(event)
                longClickListener != null
            }
        }
    }
}
