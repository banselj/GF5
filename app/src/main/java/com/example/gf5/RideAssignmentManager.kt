package com.example.gf5


import android.location.Location
import com.example.gf5.models.Driver
import com.example.gf5.repository.DriverRepository
import com.example.gf5.repository.VehicleRepository

class RideAssignmentManager(private val driverRepository: DriverRepository, private val vehicleRepository: VehicleRepository) {

    suspend fun assignRide(pickupLocation: Location, destinationLocation: Location): Driver? {
        val availableDrivers = driverRepository.getAvailableDrivers()

        // Filter drivers based on proximity and direction
        val nearbyDrivers = availableDrivers.filter {
            isDriverNearby(it, pickupLocation) && isDriverHeadingTowards(it, destinationLocation)
        }

        // Sort drivers by rating and proximity
        val sortedDrivers = nearbyDrivers.sortedWith(compareByDescending<Driver> { it.rating }.thenBy { getDistance(it, pickupLocation) })

        return sortedDrivers.firstOrNull()
    }

    private fun isDriverNearby(driver: Driver, pickupLocation: Location): Boolean {
        val driverLocation = Location("").apply {
            latitude = driver.latitude
            longitude = driver.longitude
        }
        return driverLocation.distanceTo(pickupLocation) < MAX_DISTANCE
    }

    private fun isDriverHeadingTowards(driver: Driver, destinationLocation: Location): Boolean {
        // Implement logic to check if driver is heading towards the destination
        return true
    }

    private fun getDistance(driver: Driver, pickupLocation: Location): Float {
        val driverLocation = Location("").apply {
            latitude = driver.latitude
            longitude = driver.longitude
        }
        return driverLocation.distanceTo(pickupLocation)
    }

    companion object {
        private const val MAX_DISTANCE = 5000 // 5 kilometers
    }
}
