package com.example.networkdomain.repository.truckrepository

import com.example.networkdomain.api.TrucksApi
import com.example.networkdomain.model.TruckDto
import javax.inject.Inject

class TruckRepositoryImpl @Inject constructor(
    private val trucksApi: TrucksApi
) : TruckRepository {
    override suspend fun getAllTrucks():TruckDto = trucksApi.getTruckList()
}