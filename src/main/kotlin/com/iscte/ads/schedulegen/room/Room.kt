package com.iscte.ads.schedulegen.room

data class Room(
        val building: String,
        val name: String,
        val normalCapacity: Int,
        val examCapacity: Int,
        val features: Map<String, String> = mapOf()
)
