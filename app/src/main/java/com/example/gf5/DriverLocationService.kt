package com.example.gf5

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.annotation.RequiresApi
import com.google.android.gms.location.*
import com.google.firebase.firestore.FirebaseFirestore

class DriverLocationService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val locationRequest: LocationRequest = LocationRequest.create().apply {
        interval = 10000 // 10 seconds
        fastestInterval = 5000 // 5 seconds
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }
    private lateinit var locationCallback: LocationCallback

    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
            startForeground(1, createNotification())
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    updateLocationInFirestore(location)
                }
            }
        }

        startLocationUpdates()
    }

    private fun startLocationUpdates() {
        try {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        } catch (unlikely: SecurityException) {
            // Log or handle the security exception
        }
    }

    private fun updateLocationInFirestore(location: Location) {
        val driverId = "driver123" // Replace with actual driver ID
        val locationData = hashMapOf(
            "latitude" to location.latitude,
            "longitude" to location.longitude
        )
        db.collection("drivers").document(driverId)
            .update(locationData)
            .addOnSuccessListener { /* Location updated successfully */ }
            .addOnFailureListener { exception ->
                // Log or handle the failure
            }

        db.collection("vehicles").document(driverId) // Assuming vehicle ID is same as driver ID
            .update(locationData)
            .addOnSuccessListener { /* Location updated successfully */ }
            .addOnFailureListener { exception ->
                // Log or handle the failure
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotification(): Notification {
        val channelId = "driver_tracking_channel"
        return Notification.Builder(this, channelId)
            .setContentTitle("Driver Tracking Active")
            .setContentText("Location is being updated in real-time.")
            .setSmallIcon(R.drawable.ic_tracker)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                "driver_tracking_channel",
                "Driver Tracking Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}