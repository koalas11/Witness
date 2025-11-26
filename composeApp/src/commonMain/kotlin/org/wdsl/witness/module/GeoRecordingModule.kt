package org.wdsl.witness.module

import org.wdsl.witness.model.LocationData
import org.wdsl.witness.util.Result

interface GeoRecordingModule {
    fun startGeoRecording() : Result<Unit>
    fun stopGeoRecording()
    fun getCurrentLocation(
        onSuccess: (LocationData) -> Unit,
        onError: () -> Unit
    )
    fun getGeoRecordings() :  List<LocationData>

}