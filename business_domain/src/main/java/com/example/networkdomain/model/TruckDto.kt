package com.example.networkdomain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class TruckDto(
    val data: List<DataItemDto?>? = null,
    val responseCode: ResponseCodeDto? = null
) : Parcelable

@Parcelize
data class LastRunningState(
    val truckId: Int? = null,
    val stopStartTime: Long? = null,
    val lng: Double? = null,
    val truckRunningState: Int? = null,
    val lat: Double? = null,
    val stopNotficationSent: Int? = null
) : Parcelable

@Parcelize
data class DataItemDto(
    val truckNumber: String? = null,
    val lastWaypoint: LastWaypointDto? = null,
    val breakdown: Boolean? = null,
    val trackerType: Int? = null,
    val truckTypeId: Int? = null,
    val lastRunningState: LastRunningState? = null,
    val fuelSensorInstalled: Boolean? = null,
    val deactivated: Boolean? = null,
    val transporterId: Int? = null,
    val companyId: Int? = null,
    val password: String? = null,
    val imeiNumber: String? = null,
    val truckSizeId: Int? = null,
    val createTime: Long? = null,
    val name: String? = null,
    val id: Int? = null,
    val externalTruck: Boolean? = null,
    val simNumber: String? = null,
    val durationInsideSite: Int? = null
) : Parcelable {
    fun toTruckItemEntity(): TruckItemEntity {
        val obj: TruckItemEntity = TruckItemEntity()
        obj.deactivated = this.deactivated
        obj.id = this.id
        obj.lastRunningState = this.lastRunningState
        obj.lastWaypoint = this.lastWaypoint
        obj.truckNumber = this.truckNumber
        return obj
    }
}

@Parcelize
data class LastWaypointDto(
    val truckId: Int? = null,
    val odometerReading: Double? = null,
    val fuelLevel: Int? = null,
    val lng: Double? = null,
    val bearing: Double? = null,
    val accuracy: Double? = null,
    val ignitionOn: Boolean? = null,
    val updateTime: Long? = null,
    val speed: Double? = null,
    val createTime: Long? = null,
    val id: Int? = null,
    val batteryPower: Boolean? = null,
    val lat: Double? = null,
    val batteryLevel: Int? = null
) : Parcelable

@Parcelize
data class ResponseCodeDto(
    val message: String? = null,
    val responseCode: Int? = null
) : Parcelable