package com.raj.cardex.viewmodel

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.raj.cardex.data.CocheAvistado 
import com.raj.cardex.data.GestorSesion
import java.io.File
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun PantallaCamara(
    alCapturarCoche: (CocheAvistado) -> Unit,
    viewModel: CameraViewModel = viewModel(
        factory = AyudanteFactoriaViewModel.provideCameraViewModelFactory()
    )
) { 
    val contexto = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val estadoUi by viewModel.estadoUi.collectAsState()
    var mostrarDialogo by remember { mutableStateOf(false) }
    var nombreCoche by remember { mutableStateOf("") }
    var marcaCoche by remember { mutableStateOf("") }
    var rutaImagenCapturada by remember { mutableStateOf<String?>(null) }
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(contexto) }
    var latitudUsuario by remember { mutableStateOf(40.4167) }
    var longitudUsuario by remember { mutableStateOf(-3.7033) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val locationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        if (!locationGranted) {
            Toast.makeText(contexto, "Se necesita GPS para ubicar el coche", Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    LaunchedEffect(estadoUi) {
        when (estadoUi) {
            is EstadoUiCamara.Exito -> {
                Toast.makeText(contexto, "¡Guardado!", Toast.LENGTH_SHORT).show()
                val coche = (estadoUi as EstadoUiCamara.Exito).coche
                alCapturarCoche(coche)
                mostrarDialogo = false
                nombreCoche = ""
                marcaCoche = ""
                viewModel.reiniciarEstado()
            }
            is EstadoUiCamara.Error -> {
                Toast.makeText(contexto, (estadoUi as EstadoUiCamara.Error).message, Toast.LENGTH_LONG).show()
                viewModel.reiniciarEstado()
            }
            else -> {}
        }
    }

    val imageCapture = remember { ImageCapture.Builder().build() }
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(contexto) }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageCapture
                        )
                    } catch (e: Exception) { Log.e("CAM", "Error") }
                }, ContextCompat.getMainExecutor(ctx))
                previewView
            },
            modifier = Modifier.fillMaxSize()
        )
        Button(
            onClick = {
                if (ContextCompat.checkSelfPermission(contexto, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                        .addOnSuccessListener { location ->
                            location?.let {
                                latitudUsuario = it.latitude
                                longitudUsuario = it.longitude
                            }
                        }
                }
                val photoFile = File(contexto.cacheDir, "${System.currentTimeMillis()}.jpg")
                val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
                imageCapture.takePicture(
                    outputOptions,
                    ContextCompat.getMainExecutor(contexto),
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                            rutaImagenCapturada = photoFile.absolutePath
                            mostrarDialogo = true
                        }
                        override fun onError(exc: ImageCaptureException) { Log.e("CAM", "Error") }
                    }
                )
            },
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 70.dp).size(80.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.8f))
        ) { Text("📸", color = Color.Black) }
    }

    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text("¡Coche Avistado!") },
            text = {
                Column {
                    OutlinedTextField(value = marcaCoche, onValueChange = { marcaCoche = it }, label = { Text("Marca") })
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = nombreCoche, onValueChange = { nombreCoche = it }, label = { Text("Modelo") })
                }
            },
            confirmButton = {
                Button(
                    enabled = estadoUi !is EstadoUiCamara.Guardando,
                    onClick = {
                    if (nombreCoche.isNotEmpty() && marcaCoche.isNotEmpty()) {
                        val nuevoSpot = CocheAvistado(
                            nombre = nombreCoche,
                            marca = marcaCoche,
                            latitud = latitudUsuario,
                            longitud = longitudUsuario,
                            usuario = GestorSesion.usuarioActual ?: "Unknown",
                            rutaImagen = rutaImagenCapturada
                        )
                        viewModel.guardarCoche(nuevoSpot)
                    }
                }) { 
                    if (estadoUi is EstadoUiCamara.Guardando) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Guardar")
                    }
                }
            }
        )
    }
}
