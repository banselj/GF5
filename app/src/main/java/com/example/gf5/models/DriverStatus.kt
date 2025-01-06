package com.example.gf5.models

import com.google.gson.annotations.SerializedName

enum class DriverStatus(val displayName: String) {
    @SerializedName("AVAILABLE")
    AVAILABLE("Available"),

    @SerializedName("BUSY")
    BUSY("Busy"),

    @SerializedName("OFFLINE")
    OFFLINE("Offline"),

    @SerializedName("IDLE")
    IDLE("Idle")
}