package org.wdsl.witness.module

import org.wdsl.witness.model.LocationData
import org.wdsl.witness.util.Result

/**
 * Common interface for geo-location recording functionality.
 */
interface GeoRecordingModule {
    /**
     * Starts geo-location recording.
     * @return Result indicating success or failure.
     */
    fun startGeoRecording() : Result<Unit>

    /**
     * Stops geo-location recording.
     */
    fun stopGeoRecording()

    /**
     * Gets the current location.
     * @param onSuccess Callback invoked with LocationData on success.
     * @param onError Callback invoked on failure.
     */
    fun getCurrentLocation(
        onSuccess: (LocationData) -> Unit,
        onError: () -> Unit
    )

    /**
     * Retrieves the list of recorded geo-locations.
     * @return List of LocationData.
     */
    fun getGeoRecordings() :  List<LocationData>
}
