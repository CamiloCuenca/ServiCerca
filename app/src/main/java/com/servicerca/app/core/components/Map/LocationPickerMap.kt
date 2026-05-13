package com.servicerca.app.core.components.Map

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.viewport.data.DefaultViewportTransitionOptions

@Composable
fun LocationPickerMap(
    modifier: Modifier = Modifier,
    initialLat: Double = 4.4687891,
    initialLng: Double = -75.6491181,
    onLocationConfirmed: (Double, Double) -> Unit,
    onDismiss: () -> Unit
) {
    val permissionState = rememberLocationPermissionState()
    var followUser by remember { mutableStateOf(false) }

    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            zoom(14.0)
            center(Point.fromLngLat(initialLng, initialLat))
        }
    }

    Box(modifier = modifier) {
        MapboxMap(
            modifier = Modifier.matchParentSize(),
            mapViewportState = mapViewportState
        ) {
            if (permissionState.hasPermission && followUser) {
                MapEffect(key1 = "picker_follow") { mapView ->
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
                    followUser = false
                }
            }
        }

        // Pin fijo en el centro del mapa — el usuario arrastra el mapa para posicionarlo
        Icon(
            imageVector = Icons.Default.Place,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.Center)
                .size(48.dp)
                .offset(y = (-20).dp)
        )

        // Label de instrucción
        Surface(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.92f)
        ) {
            Text(
                text = "Mueve el mapa para seleccionar la ubicación",
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                style = MaterialTheme.typography.bodySmall
            )
        }

        // FAB "Mi ubicación"
        FloatingActionButton(
            onClick = {
                if (permissionState.hasPermission) {
                    followUser = true
                } else {
                    permissionState.requestPermission()
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 96.dp),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) {
            Icon(Icons.Default.MyLocation, contentDescription = "Mi ubicación")
        }

        // Botones Cancelar / Confirmar
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Cancelar")
            }
            Button(
                onClick = {
                    val center = mapViewportState.cameraState?.center
                        ?: Point.fromLngLat(initialLng, initialLat)
                    onLocationConfirmed(center.latitude(), center.longitude())
                },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Confirmar")
            }
        }
    }
}
