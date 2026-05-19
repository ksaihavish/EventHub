package com.example.eventhub.models

import com.example.eventhub.models.*
import com.example.eventhub.adapters.*
import com.example.eventhub.data.*
import com.example.eventhub.ui.*
import com.example.eventhub.R
import com.example.eventhub.BuildConfig

data class EventModel(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)
