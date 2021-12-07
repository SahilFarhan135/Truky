package com.example.networkdomain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TruckItemEntity(
    var truckNumber: String? = null,
    var lastWaypoint: LastWaypointDto? = null,
    var lastRunningState: LastRunningState? = null,
    var deactivated: Boolean? = null,
    var id: Int? = null
):Parcelable
