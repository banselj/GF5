package com.example.gf5.models

data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: String // e.g., "rider", "driver", "admin"
)
