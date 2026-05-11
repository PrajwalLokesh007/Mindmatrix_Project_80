package com.example.myapplication.data.model

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val ecoPoints: Int = 0,
    val badges: List<String> = emptyList()
)
