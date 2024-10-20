package com.example.gf5.models

import com.google.firebase.firestore.PropertyName

/**
 * Data class representing a Ride entity.
 *
 * @property id The unique identifier for the ride.
 * @property userId The ID of the user who requested the ride.
 * @property driverId The ID of the driver assigned to the ride.
 * @property pickupLocation The location where the ride starts.
 * @property destinationLocation The destination of the ride.
 * @property status The current status of the ride (e.g., Pending, InProgress, Completed).
 * @property timestamp The time when the ride was requested.
 */
data class Ride(
    @PropertyName("id")
    val id: String = "",

    @PropertyName("userId")
    val userId: String = "",

    @PropertyName("driverId")
    val driverId: String = "",

    @PropertyName("pickupLocation")
    val pickupLocation: String = "",

    @PropertyName("destinationLocation")
    val destinationLocation: String = "",

    @PropertyName("status")
    val status: String = "Pending", // Default status

    @PropertyName("timestamp")
    val timestamp: Long = 0L
)
