package com.iscte.ads.schedulegen

data class Room(
        val building: String,
        val name: String,
        val normalCapacity: Int,
        val examCapacity: Int,
        val features: Array<String> = arrayOf()
)
