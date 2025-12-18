package org.wdsl.witness.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import kotlinx.serialization.json.JsonObject
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.expressions.dsl.const
import org.maplibre.compose.expressions.dsl.image
import org.maplibre.compose.layers.LineLayer
import org.maplibre.compose.layers.SymbolLayer
import org.maplibre.compose.map.MapOptions
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.OrnamentOptions
import org.maplibre.compose.material3.DisappearingCompassButton
import org.maplibre.compose.material3.DisappearingScaleBar
import org.maplibre.compose.material3.ExpandingAttributionButton
import org.maplibre.compose.sources.GeoJsonData
import org.maplibre.compose.sources.rememberGeoJsonSource
import org.maplibre.compose.style.BaseStyle
import org.maplibre.compose.style.rememberStyleState
import org.maplibre.compose.util.ClickResult
import org.maplibre.spatialk.geojson.BoundingBox
import org.maplibre.spatialk.geojson.Feature
import org.maplibre.spatialk.geojson.FeatureCollection
import org.maplibre.spatialk.geojson.LineString
import org.maplibre.spatialk.geojson.Point
import org.maplibre.spatialk.geojson.Position
import org.maplibre.spatialk.geojson.toJson
import org.wdsl.witness.storage.room.Recording
import org.wdsl.witness.util.Log

/**
 * A composable that displays a map with GPS positions from a recording.
 *
 * @param modifier The modifier to be applied to the map container.
 * @param recording The recording containing GPS positions to be displayed on the map.
 */
@Composable
fun ColumnScope.MapComposable(
    modifier: Modifier = Modifier,
    recording: Recording,
) {
    if (recording.gpsPositions.isEmpty()) {
        Box(
            modifier = modifier
                .padding(8.dp)
                .weight(0.7f)
                .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "No GPS data available for this recording.")
        }
        return
    }
    val cameraState = rememberCameraState(
        CameraPosition(
            target = Position(
                recording.gpsPositions.firstOrNull()?.longitude ?: 0.0,
                recording.gpsPositions.firstOrNull()?.latitude ?: 0.0,
            ),
            tilt = 45.0,
            zoom = 16.0,
        )
    )
    val styleState = rememberStyleState()

    Box(
        modifier = modifier
            .padding(8.dp)
            .weight(0.7f)
            .fillMaxSize(),
    ) {
        var boundingBox by remember {
            mutableStateOf(
                BoundingBox(
                    south = -90.0,
                    west = -180.0,
                    north = 90.0,
                    east = 180.0,
                )
            )
        }
        MaplibreMap(
            baseStyle = BaseStyle.Uri("https://tiles.openfreemap.org/styles/liberty"),
            cameraState = cameraState,
            styleState = styleState,
            boundingBox = boundingBox,
            options = MapOptions(ornamentOptions = OrnamentOptions.OnlyLogo),
        ) {
            val (firstLastPoints, middlePoints) = remember(recording.id) {
                val features = recording.gpsPositions.map { gpsPosition ->
                    Feature(
                        geometry = Point(
                            gpsPosition.longitude,
                            gpsPosition.latitude,
                            gpsPosition.altitude
                        ),
                        properties = JsonObject(emptyMap()),
                    )
                }
                boundingBox = boundingBoxFromFeatures(features)
                val list =  emptyList<Feature<Point, JsonObject>>()
                if (features.size >= 2) {
                    listOf(features.first().toJson(), features.last().toJson()) to FeatureCollection(
                        features.subList(1, features.size - 1)
                    ).toJson()
                } else if (features.size == 1) {
                    listOf(features.first().toJson(), features.first().toJson()) to FeatureCollection(
                        list
                    ).toJson()
                } else {
                    listOf(
                        "",
                        ""
                    ) to FeatureCollection(
                        list
                    ).toJson()
                }
            }
            val (firstPoint, lastPoint) = firstLastPoints
            val lineData = remember(recording.id) {
                if (recording.gpsPositions.size >= 2) {
                    val linePositions = recording.gpsPositions.map { p ->
                        Position(p.longitude, p.latitude)
                    }
                    val lineFeature = Feature(
                        geometry = LineString(linePositions),
                        properties = JsonObject(emptyMap())
                    )
                    FeatureCollection(listOf(lineFeature)).toJson()
                } else {
                    FeatureCollection<LineString, JsonObject>(emptyList()).toJson()
                }
            }

            if (recording.gpsPositions.isNotEmpty()) {
                val gpsFirstPointSource = rememberGeoJsonSource(GeoJsonData.JsonString(firstPoint))
                val gpsMiddlePointSource = rememberGeoJsonSource(GeoJsonData.JsonString(middlePoints))
                val gpsLastPointSource = rememberGeoJsonSource(GeoJsonData.JsonString(lastPoint))
                val lineSource = rememberGeoJsonSource(GeoJsonData.JsonString(lineData))

                val marker = rememberVectorPainter(Icons.Default.LocationOn)

                LineLayer(
                    id = "gps_line",
                    source = lineSource,
                    color = const(MaterialTheme.colorScheme.primary),
                    width = const(2.dp),
                    dasharray = const(listOf(4.0, 2.0))
                )
                SymbolLayer(
                    id = "gps_symbols_middle",
                    source = gpsMiddlePointSource,
                    onClick = { features ->
                        Log.d("MapComposable", "Clicked features: $features")
                        ClickResult.Consume
                    },
                    iconImage = image(marker),
                )
                SymbolLayer(
                    id = "gps_symbols_first",
                    source = gpsFirstPointSource,
                    onClick = { features ->
                        Log.d("MapComposable", "Clicked features: $features")
                        ClickResult.Consume
                    },
                    iconImage = image(marker, drawAsSdf = true),
                    iconColor = const(Color.Green)
                )
                SymbolLayer(
                    id = "gps_symbols_last",
                    source = gpsLastPointSource,
                    onClick = { features ->
                        Log.d("MapComposable", "Clicked features: $features")
                        ClickResult.Consume
                    },
                    iconImage = image(marker, drawAsSdf = true),
                    iconColor = const(Color.Red)
                )
            }
        }

        Box(modifier = modifier.fillMaxSize().padding(8.dp)) {
            DisappearingScaleBar(
                metersPerDp = cameraState.metersPerDpAtTarget,
                zoom = cameraState.position.zoom,
                modifier = modifier.align(Alignment.TopStart),
            )
            Row(
                modifier = modifier
                    .padding(8.dp)
                    .clip(CircleShape)
                    .align(Alignment.BottomCenter)
                    .background(Color.Gray.copy(alpha = 0.8f)),
            ) {
                Icon(
                    modifier = modifier
                        .padding(start = 8.dp)
                        .padding(vertical = 8.dp)
                        .padding(end = 4.dp),
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color.Green,
                )
                Text(
                    modifier = modifier
                        .padding(vertical = 8.dp)
                        .padding(end = 8.dp),
                    text = "Start",
                    color = Color.White,
                )
                Icon(
                    modifier = modifier
                        .padding(vertical = 8.dp)
                        .padding(end = 4.dp),
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color.Red,
                )
                Text(
                    modifier = modifier
                        .padding(vertical = 8.dp)
                        .padding(end = 8.dp),
                    text = "End",
                    color = Color.White,
                )
            }
            DisappearingCompassButton(cameraState, modifier = modifier.align(Alignment.TopEnd))
            ExpandingAttributionButton(
                cameraState = cameraState,
                styleState = styleState,
                modifier = modifier.align(Alignment.BottomEnd),
                contentAlignment = Alignment.BottomEnd,
            )
        }
    }
}

/**
 * Computes a bounding box that encompasses all the given features, with optional padding.
 *
 * @param features The list of features to compute the bounding box from.
 * @param paddingDegrees The padding to apply to the bounding box in degrees (default is 0.001).
 * @return A [BoundingBox] that contains all the features with the specified padding.
 */
private fun boundingBoxFromFeatures(
    features: List<Feature<Point, JsonObject>>,
    paddingDegrees: Double = 0.001
): BoundingBox {
    if (features.isEmpty()) {
        return BoundingBox(
            south = -90.0,
            west = -180.0,
            north = 90.0,
            east = 180.0
        )
    }

    var minLat = Double.POSITIVE_INFINITY
    var maxLat = Double.NEGATIVE_INFINITY
    var minLon = Double.POSITIVE_INFINITY
    var maxLon = Double.NEGATIVE_INFINITY

    for (f in features) {
        val p = f.geometry
        val lon = p.longitude
        val lat = p.latitude
        if (lat < minLat) minLat = lat
        if (lat > maxLat) maxLat = lat
        if (lon < minLon) minLon = lon
        if (lon > maxLon) maxLon = lon
    }

    minLat = maxOf(minLat - paddingDegrees, -90.0)
    maxLat = minOf(maxLat + paddingDegrees, 90.0)
    minLon = maxOf(minLon - paddingDegrees, -180.0)
    maxLon = minOf(maxLon + paddingDegrees, 180.0)

    return BoundingBox(south = minLat, west = minLon, north = maxLat, east = maxLon)
}
