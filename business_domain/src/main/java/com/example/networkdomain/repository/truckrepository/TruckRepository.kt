package com.example.networkdomain.repository.truckrepository

import com.example.networkdomain.model.TruckDto

interface TruckRepository {
    suspend fun getAllTrucks(): TruckDto
}