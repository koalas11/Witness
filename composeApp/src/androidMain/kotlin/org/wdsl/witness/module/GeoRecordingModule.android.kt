package org.wdsl.witness.module

import android.Manifest
import android.content.Context
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
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

/**
 * Android implementation of GeoRecordingModule using FusedLocationProviderClient.
 *
 * @param context The Android context.
 */
class AndroidGeoRecordingModule(
    private val context: Context
) : GeoRecordingModule {

    private val recordedLocations: MutableList<LocationData> = mutableListOf()
    private var locationClient: FusedLocationProviderClient? = null

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            val lastLocation = result.lastLocation ?: return
            recordedLocations.add(lastLocation.toLocationData())
            Log.d("LocationService", "Registered Position: ${lastLocation.latitude}")
        }
    }

    private val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        10.seconds.inWholeMilliseconds
    ).build()

    override fun startGeoRecording(): Result<Unit> {
        recordedLocations.clear()
        locationClient = LocationServices.getFusedLocationProviderClient(context)
        locationClient!!.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        getCurrentLocation(
            onSuccess = {
                recordedLocations.add(it)
            },
            onError = {
                Log.e(TAG, "start: Unable to get current location")
            }
        )
        return Result.Success(Unit)
    }

    override fun stopGeoRecording() {
        locationClient?.removeLocationUpdates(locationCallback)
        locationClient = null
    }

    override fun getCurrentLocation(
        onSuccess: (LocationData) -> Unit,
        onError: () -> Unit
    ) {
        Log.d(TAG, "getCurrentLocation: Fetching current location")
        val fineLocationPermission = Manifest.permission.ACCESS_FINE_LOCATION
        val coarseLocationPermission = Manifest.permission.ACCESS_COARSE_LOCATION

        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            context,
            fineLocationPermission
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
            context,
            coarseLocationPermission
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        if (!hasFineLocationPermission && !hasCoarseLocationPermission) {
            Log.e(TAG, "getCurrentLocation: Location permissions are not granted")
            onError()
            return
        }

        LocationServices.getFusedLocationProviderClient(context).getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            null
        ).addOnCompleteListener {
            Log.d(TAG, "getCurrentLocation: Current location fetched successfully")
            onSuccess(it.result.toLocationData())
        }.addOnFailureListener {
            Log.e(TAG, "getCurrentLocation: Failed to get current location", it)
            onError()
        }
    }

    override fun getGeoRecordings(): List<LocationData> {
        this.stopGeoRecording()
        return recordedLocations.toList()
    }

    companion object {
        private const val TAG = "AndroidGeoRecordingModule"
    }
}
