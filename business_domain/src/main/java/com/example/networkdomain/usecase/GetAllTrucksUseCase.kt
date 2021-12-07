package com.example.networkdomain.usecase

import android.util.Log
import com.example.networkdomain.model.TruckDto
import com.example.networkdomain.network.Resources
import com.example.networkdomain.repository.truckrepository.TruckRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetAllTrucksUseCase @Inject constructor(
    private val repository: TruckRepository
) {
    operator fun invoke(): Flow<Resources<TruckDto>> = flow {
        try {
            emit(Resources.Loading())
            val truckList = repository.getAllTrucks()
            emit(Resources.Success(truckList))
        } catch (e: HttpException) {
            emit(Resources.Error(e.localizedMessage ?: "Something went wrong"))
            Log.d("OK", e.localizedMessage.plus(e.code()))
        } catch (e: IOException) {
            emit(Resources.Error("Couldn't connect to server"))
        }
    }
}