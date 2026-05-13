package com.servicerca.app.core.components.Map

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.viewport.data.DefaultViewportTransitionOptions

@Composable
fun MapBox(
    modifier: Modifier = Modifier,
    showMyLocationButton: Boolean = true,
    markerPoint: Point? = null
) {
    val permissionState = rememberLocationPermissionState()
    var shouldFollowUser by remember { mutableStateOf(false) }

    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            zoom(8.0)
            center(Point.fromLngLat(-75.6491181, 4.4687891))
        }
    }

    var markerManager: PointAnnotationManager? by remember { mutableStateOf(null) }
    val markerBitmap = remember { createMapMarkerBitmap() }

    // Actualizar pin cuando cambia la ubicación seleccionada o cuando el manager está listo
    LaunchedEffect(markerPoint, markerManager) {
        val manager = markerManager ?: return@LaunchedEffect
        manager.deleteAll()

        markerPoint?.let { point ->
            manager.create(
                PointAnnotationOptions()
                    .withPoint(point)
                    .withIconImage(markerBitmap)
                    .withIconSize(1.0)
            )
            mapViewportState.setCameraOptions {
                center(point)
                zoom(14.0)
            }
        }
    }

    Box(modifier = modifier) {
        MapboxMap(
            modifier = Modifier.matchParentSize(),
            mapViewportState = mapViewportState
        ) {
            // Crear annotation manager una sola vez cuando el mapa esté listo
            MapEffect(Unit) { mapView ->
                markerManager = mapView.annotations.createPointAnnotationManager()
            }

            if (permissionState.hasPermission && shouldFollowUser) {
                MapEffect(key1 = "follow_puck") { mapView ->
                    mapView.location.updateSettings {
                        locationPuck = createDefault2DPuck(withBearing = true)
                        enabled = true
                        puckBearing = PuckBearing.COURSE
                        puckBearingEnabled = true
                    }
                    mapViewportState.transitionToFollowPuckState(
                        defaultTransitionOptions = DefaultViewportTransitionOptions.Builder()
                            .maxDurationMs(1500)
                            .build()
                    )
                }
            }
        }

        if (showMyLocationButton) {
            FloatingActionButton(
                onClick = {
                    if (permissionState.hasPermission) {
                        shouldFollowUser = true
                    } else {
                        permissionState.requestPermission()
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = "Mi ubicación"
                )
            }
        }
    }
}

internal fun createMapMarkerBitmap(): Bitmap {
    val w = 32
    val h = 44
    val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    val cx = w / 2f
    val r = w / 2f - 2f
    val cy = r + 2f

    val paintBlue = Paint().apply {
        color = 0xFF1565C0.toInt()
        isAntiAlias = true
        style = Paint.Style.FILL
    }
    val paintWhiteFill = Paint().apply {
        color = android.graphics.Color.WHITE
        isAntiAlias = true
        style = Paint.Style.FILL
    }
    val paintWhiteStroke = Paint().apply {
        color = android.graphics.Color.WHITE
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 2.5f
    }

    canvas.drawCircle(cx, cy, r, paintBlue)
    canvas.drawCircle(cx, cy, r, paintWhiteStroke)

    val tip = android.graphics.Path().apply {
        moveTo(cx - r * 0.55f, cy + r * 0.7f)
        lineTo(cx, h.toFloat() - 1f)
        lineTo(cx + r * 0.55f, cy + r * 0.7f)
        close()
    }
    canvas.drawPath(tip, paintBlue)

    canvas.drawCircle(cx, cy, r * 0.35f, paintWhiteFill)

    return bitmap
}

class LocationPermissionState(
    hasPermission: Boolean = false,
    val requestPermission: () -> Unit = {}
) {
    var hasPermission by mutableStateOf(hasPermission)
        internal set

    var wasJustGranted by mutableStateOf(false)
        internal set
}

@Composable
fun rememberLocationPermissionState(
    permission: String = android.Manifest.permission.ACCESS_FINE_LOCATION
): LocationPermissionState {
    val context = LocalContext.current

    val initialPermission = remember {
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    val state = remember { LocationPermissionState(hasPermission = initialPermission) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        state.wasJustGranted = granted && !state.hasPermission
        state.hasPermission = granted
    }

    return remember(state, launcher) {
        LocationPermissionState(
            hasPermission = state.hasPermission,
            requestPermission = { launcher.launch(permission) }
        ).also {
            it.wasJustGranted = state.wasJustGranted
        }
    }
}
