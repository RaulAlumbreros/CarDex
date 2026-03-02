package com.raj.cardex.viewmodel

import android.Manifest
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun PantallaMapa(
    viewModel: MapViewModel = viewModel(
        factory = AyudanteFactoriaViewModel.provideMapViewModelFactory()
    )
) {
    val contexto = LocalContext.current
    var mapViewRef by remember { mutableStateOf<MapView?>(null) }
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(contexto) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val locationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        if (locationGranted) {
            obtenerUbicacionYCentrar(fusedLocationClient, mapViewRef)
        }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
        viewModel.cargarCoches()
    }

    val estadoUi by viewModel.estadoUi.collectAsState()
    val spotsDeMongo = if (estadoUi is EstadoUiMapa.Exito) {
        (estadoUi as EstadoUiMapa.Exito).coches
    } else {
        emptyList()
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, mapViewRef) {
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            mapViewRef?.let { map ->
                when (event) {
                    Lifecycle.Event.ON_RESUME -> map.onResume()
                    Lifecycle.Event.ON_PAUSE -> map.onPause()
                    else -> {}
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
        }
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->
            Configuration.getInstance().userAgentValue = ctx.packageName
            MapView(ctx).apply {
                setMultiTouchControls(true)
                controller.setZoom(16.0)
                controller.setCenter(GeoPoint(40.4167, -3.7032))
                mapViewRef = this
            }
        },
        update = { mapView ->
            mapView.overlays.clear()
            spotsDeMongo.forEach { spot ->
                val marker = Marker(mapView).apply {
                    position = GeoPoint(spot.latitud, spot.longitud)
                    title = "${spot.marca} ${spot.nombre}"
                    snippet = "Cazado por: ${spot.usuario}"
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                }
                mapView.overlays.add(marker)
            }
            mapView.invalidate()
        }
    )
}

private fun obtenerUbicacionYCentrar(
    fusedClient: com.google.android.gms.location.FusedLocationProviderClient,
    mapView: MapView?
) {
    try {
        fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                location?.let {
                    val actualPoint = GeoPoint(it.latitude, it.longitude)
                    mapView?.controller?.animateTo(actualPoint)
                    mapView?.controller?.setZoom(17.5)
                }
            }
    } catch (e: SecurityException) {
        Log.e("GPS_ERROR", "Sin permisos de GPS")
    }
}
