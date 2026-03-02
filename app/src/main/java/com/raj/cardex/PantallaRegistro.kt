package com.raj.cardex.viewmodel

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun PantallaRegistro(
    alRegistroExito: () -> Unit,
    alVolverALogin: () -> Unit,
    viewModel: RegisterViewModel = viewModel(
        factory = AyudanteFactoriaViewModel.provideRegisterViewModelFactory()
    )
) {
    var usuario by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    val naranjaCarDex = Color(0xFFFF4500)
    val estadoUi by viewModel.estadoUi.collectAsState()
    val estaCargando = estadoUi is EstadoUiRegistro.Cargando
    val mensajeError = if (estadoUi is EstadoUiRegistro.Error) (estadoUi as EstadoUiRegistro.Error).message else null

    LaunchedEffect(estadoUi) {
        if (estadoUi is EstadoUiRegistro.Exito) {
            alRegistroExito()
            viewModel.reiniciarEstado()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Crear Cuenta", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = naranjaCarDex)
        Spacer(modifier = Modifier.height(32.dp))
        mensajeError?.let {
            Text(text = it, color = Color.Red, modifier = Modifier.padding(bottom = 8.dp))
        }
        OutlinedTextField(
            value = usuario,
            onValueChange = { usuario = it },
            label = { Text("Usuario") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !estaCargando 
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !estaCargando
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = contrasena,
            onValueChange = { contrasena = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            enabled = !estaCargando
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = naranjaCarDex),
            enabled = !estaCargando && usuario.isNotBlank() && correo.isNotBlank(), 
            onClick = {
                viewModel.registrarUsuario(usuario, correo, contrasena)
            }
        ) {
            if (estaCargando) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("Registrarme", color = Color.White)
            }
        }
        TextButton(onClick = alVolverALogin, enabled = !estaCargando) {
            Text("Ya tengo cuenta, volver", color = naranjaCarDex)
        }
    }
}
