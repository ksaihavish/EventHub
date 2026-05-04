package com.example.eventhub

data class Event(
    val title: String,
    val description: String,
    val date: String,
    val locationName: String,
    val latitude: Double,  // Must be Double
    val longitude: Double  // Must be Double
)