package com.example.truky.ui.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.example.networkdomain.model.TruckItemEntity
import com.example.truky.R
import com.example.truky.base.BaseActivity
import com.example.truky.databinding.ActivityMainBinding
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import java.text.ParseException
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

@AndroidEntryPoint
class MapActivity : BaseActivity<ActivityMainBinding>(), OnMapReadyCallback {

    private lateinit var mapCurrent: GoogleMap
    private var truckList = ArrayList<TruckItemEntity>()
    private var mapFragment: SupportMapFragment? = null

    companion object {
        const val KEY_LAT_LNG = "KEY_LAT_LNG"
    }


    override fun layoutId(): Int = R.layout.activity_main
    private val _viewModel: MapViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getData()
        initUi()
        addListener()
        addObservers()
    }

    private fun getData() {
        if (intent.getParcelableArrayListExtra<TruckItemEntity>(KEY_LAT_LNG) != null) {
            truckList =
                intent.getParcelableArrayListExtra<TruckItemEntity>(KEY_LAT_LNG) as ArrayList<TruckItemEntity>
        }
    }


    private fun initUi() {
        with(binding) {
            appbar.tvTitle.text = getString(R.string.txt_app_namee)
            appbar.imgSearch.visibility = View.GONE
            appbar.imgMap.visibility = View.GONE
            appbar.imgList.visibility = View.VISIBLE
        }
        if (truckList.isEmpty()) {
            _viewModel.getTruckList()
        }
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    private fun addListener() {
        binding.appbar.imgList.setOnClickListener {
            onBackPressed()
            finish()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        MapsInitializer.initialize(this)
        mapCurrent = googleMap
        mapCurrent.mapType = GoogleMap.MAP_TYPE_NORMAL
        addMarker()
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(
            LatLng(
                truckList[0].lastWaypoint?.lat ?: 0.0,
                truckList[0].lastWaypoint?.lng ?: 0.0
            ), 8f
        )
        mapCurrent.animateCamera(cameraUpdate)
    }


    private fun addMarker() {
        truckList.forEach {
            val latLng = LatLng(it.lastWaypoint?.lat ?: 0.0, it.lastWaypoint?.lng ?: 0.0)
            val markerOptions = MarkerOptions().position(latLng)
            if (it.lastRunningState?.truckRunningState == 1) {
                markerOptions.icon(bitmapDescriptorFromVector(this, R.drawable.truckk_green))
            } else if (it.lastRunningState?.truckRunningState == 0) {
                if (it.lastWaypoint?.ignitionOn == true) {
                    markerOptions.icon(bitmapDescriptorFromVector(this, R.drawable.truckk_yellow))
                } else {
                    markerOptions.icon(bitmapDescriptorFromVector(this, R.drawable.truck_blue))
                }
            }
            val stopTime = it.lastRunningState?.stopStartTime?.let { differenceInTime(it) }
            if (stopTime != null) {
                if (stopTime.second.contains("days") || (stopTime.second.contains("hour") && stopTime.first.toIntOrNull() ?: 0 >= 4)) {
                    markerOptions.icon(bitmapDescriptorFromVector(this, R.drawable.truck_red))
                }
            }
            markerOptions.anchor((0.5f), 0.5f)
            mapCurrent.addMarker(markerOptions)
        }
    }


    private fun differenceInTime(longTime: Long): Pair<String, String> {
        var day = 0
        var hh = 0
        var mm = 0
        try {
            val cDate = Date()
            val timeDiff = cDate.time - longTime
            day = TimeUnit.MILLISECONDS.toDays(timeDiff).toInt()
            hh =
                (TimeUnit.MILLISECONDS.toHours(timeDiff) - TimeUnit.DAYS.toHours(day.toLong())).toInt()
            mm = (TimeUnit.MILLISECONDS.toMinutes(timeDiff) - TimeUnit.HOURS.toMinutes(
                TimeUnit.MILLISECONDS.toHours(timeDiff)
            )).toInt()
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return if (mm <= 60 && hh != 0) {
            if (hh <= 60 && day != 0) {
                Pair("$day", "days")
            } else {
                Pair("$hh", "hour")
            }
        } else {
            Pair("$mm", "min")
        }
    }


    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
        vectorDrawable!!.setBounds(
            0,
            0,
            50,
            50
        )
        val bitmap: Bitmap = Bitmap.createBitmap(
            40,
            40,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }


    override fun addObservers() {
        if (truckList.isEmpty()) {
            observeViewState()
            observeTruckList()
            observeError()
        }
    }

    private fun observeViewState() {
        _viewModel.viewState.observe(this, {
            when (it) {
                com.example.truky.base.ViewState.Loading -> {
                    uiUtil.showProgress()
                }
                com.example.truky.base.ViewState.Success() -> {
                    uiUtil.hideProgress()
                }
                else -> {}
            }
        })
    }

    private fun observeTruckList() {
        _viewModel.setTruckList.observe(this, {
            if (it?.data == null || it.responseCode == null || it.responseCode?.responseCode == null || it.responseCode!!.responseCode!! == 0) {
                uiUtil.showToast("Something went wrong")
                return@observe
            }
            truckList.clear()
            for (item in it.data!!) {
                truckList.add(item!!.toTruckItemEntity())
            }
            if (mapFragment == null) {
                mapFragment =
                    supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
            }
            mapFragment?.getMapAsync(this)
        })
    }

    private fun observeError() {
        _viewModel.setError.observe(this, {
            showToast(it.toString())
        })
    }


}





