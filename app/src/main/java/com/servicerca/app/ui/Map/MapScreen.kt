package com.servicerca.app.ui.Map

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.OnPointAnnotationClickListener
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.viewport.data.DefaultViewportTransitionOptions
import com.servicerca.app.R
import com.servicerca.app.core.components.Map.rememberLocationPermissionState
import com.servicerca.app.domain.model.Service
import kotlin.collections.forEach

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onBackClick: () -> Unit,
    onServiceDetailClick: (String) -> Unit = {},
    viewModel: MapViewModel = hiltViewModel()
) {
    val services by viewModel.approvedServices.collectAsStateWithLifecycle()
    val selectedService by viewModel.selectedService.collectAsStateWithLifecycle()
    val permissionState = rememberLocationPermissionState()
    val markerBitmap = remember { createServiceMarkerBitmap() }

    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            zoom(12.0)
            center(Point.fromLngLat(-75.6491181, 4.4687891))
        }
    }

    // Manager de anotaciones compartido entre MapEffect y LaunchedEffect
    var pointAnnotationManager: PointAnnotationManager? by remember { mutableStateOf(null) }
    val idToService = remember { mutableMapOf<String, Service>() }

    // Actualizar pins cuando cambian los servicios o cuando el manager se inicializa
    LaunchedEffect(services, pointAnnotationManager) {
        val manager = pointAnnotationManager ?: return@LaunchedEffect
        manager.deleteAll()
        idToService.clear()

        services
            .filter { it.location.latitude != 0.0 || it.location.longitude != 0.0 }
            .forEach { service ->
                val annotation = manager.create(
                    PointAnnotationOptions()
                        .withPoint(
                            Point.fromLngLat(
                                service.location.longitude,
                                service.location.latitude
                            )
                        )
                        .withIconImage(markerBitmap)
                        .withIconSize(1.0)
                )
                idToService[annotation.id] = service
            }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Servicios cercanos") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_back)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            MapboxMap(
                modifier = Modifier.fillMaxSize(),
                mapViewportState = mapViewportState
            ) {
                // Inicializar puck de ubicación y annotation manager una sola vez
                MapEffect(Unit) { mapView ->
                    if (permissionState.hasPermission) {
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

                    val manager = mapView.annotations.createPointAnnotationManager()
                    manager.addClickListener(object : OnPointAnnotationClickListener {
                        override fun onAnnotationClick(annotation: PointAnnotation): Boolean {
                            val service = idToService[annotation.id]
                            viewModel.onServiceSelected(service)
                            return true
                        }
                    })
                    pointAnnotationManager = manager
                }
            }

            // FAB para volver a la ubicación del usuario
            FloatingActionButton(
                onClick = {
                    if (permissionState.hasPermission) {
                        mapViewportState.transitionToFollowPuckState(
                            defaultTransitionOptions = DefaultViewportTransitionOptions.Builder()
                                .maxDurationMs(1000)
                                .build()
                        )
                    } else {
                        permissionState.requestPermission()
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(
                        end = 16.dp,
                        bottom = if (selectedService != null) 200.dp else 16.dp
                    ),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = "Mi ubicación")
            }

            // Card del servicio seleccionado, aparece desde abajo al tocar un pin
            AnimatedVisibility(
                visible = selectedService != null,
                modifier = Modifier.align(Alignment.BottomCenter),
                enter = slideInVertically { it },
                exit = slideOutVertically { it }
            ) {
                selectedService?.let { service ->
                    ServiceMapCard(
                        service = service,
                        onDismiss = { viewModel.onServiceSelected(null) },
                        onDetailClick = { onServiceDetailClick(service.id) }
                    )
                }
            }
        }
    }
}


@Composable
private fun ServiceMapCard(
    service: Service,
    onDismiss: () -> Unit,
    onDetailClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = service.photoUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = service.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = service.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = service.type,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar")
                }
                TextButton(onClick = onDetailClick) {
                    Text("Ver")
                    Spacer(Modifier.width(2.dp))
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

private fun createServiceMarkerBitmap(): Bitmap {
    val w = 32
    val h = 44
    val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888, true)
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
