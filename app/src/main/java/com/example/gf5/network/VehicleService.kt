package com.example.gf5.network


import com.example.gf5.models.Vehicle
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface VehicleService {
    @GET("vehicles/available")
    suspend fun getAvailableVehicles(): Response<List<Vehicle>>

    @POST("vehicles/{vehicleId}/location")
    suspend fun updateVehicleLocation(@Path("vehicleId") vehicleId: String, @Body location: Map<String, Double>): Response<Boolean>
}
