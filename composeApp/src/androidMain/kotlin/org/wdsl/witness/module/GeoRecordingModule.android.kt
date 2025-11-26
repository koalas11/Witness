package org.wdsl.witness.module

import android.Manifest
import android.content.Context
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import org.wdsl.witness.model.LocationData
import org.wdsl.witness.model.toLocationData
import org.wdsl.witness.util.Result
import kotlin.time.Duration.Companion.seconds

class AndroidGeoRecordingModule(
    private val context: Context
) : GeoRecordingModule {

    private val recordedLocations: MutableList<LocationData> = mutableListOf()
    private var locationClient: FusedLocationProviderClient? = null

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            val lastLocation = result.lastLocation ?: return
            recordedLocations.add(lastLocation.toLocationData())
            Log.d("LocationService", "Posizione registrata: ${lastLocation.latitude}")
        }
    }

    private val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        10.seconds.inWholeMilliseconds
    ).build()

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun startGeoRecording(): Result<Unit> {
        recordedLocations.clear()
        locationClient = LocationServices.getFusedLocationProviderClient(context)
        locationClient!!.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        getCurrentLocation(
            onSuccess = {
                recordedLocations.add(it)
            },
            onError = {
                Log.e("AndroidGeoRecordingModule", "start: Unable to get current location")
            }
        )
        return Result.Success(Unit)
    }

    override fun stopGeoRecording() {
        locationClient?.removeLocationUpdates(locationCallback)
        locationClient = null
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun getCurrentLocation(
        onSuccess: (LocationData) -> Unit,
        onError: () -> Unit
    ) {
        locationClient?.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            null
        )?.addOnCompleteListener {
            onSuccess(it.result.toLocationData())
        }?.addOnFailureListener {
            onError()
        }
    }

    override fun getGeoRecordings(): List<LocationData> {
        this.stopGeoRecording()
        return recordedLocations.toList()
    }
}
