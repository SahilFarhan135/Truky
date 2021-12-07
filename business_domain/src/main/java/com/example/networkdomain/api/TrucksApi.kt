package com.example.networkdomain.api

import com.example.networkdomain.model.TruckDto
import retrofit2.http.GET

interface TrucksApi {

    @GET(EndPoint.GET_TRUCK_LIST)
    suspend fun getTruckList(): TruckDto
}