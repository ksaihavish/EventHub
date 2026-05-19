package com.example.eventhub

import java.io.Serializable

data class Event(
    var id: String = java.util.UUID.randomUUID().toString(),
    var title: String = "",
    var description: String = "",
    var date: String = "",
    var locationName: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0
) : Serializable