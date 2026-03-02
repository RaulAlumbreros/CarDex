package com.raj.cardex.viewmodel

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun PantallaLogin(
    alIniciarSesionExito: () -> Unit,
    alNavegarARegistro: () -> Unit,
    viewModel: LoginViewModel = viewModel(
        factory = AyudanteFactoriaViewModel.provideLoginViewModelFactory()
    )
) {
    var usuario by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    val contexto = LocalContext.current
    val naranjaCarDex = Color(0xFFFF4500)
    val estadoUi by viewModel.estadoUi.collectAsState()

    LaunchedEffect(estadoUi) {
        when (estadoUi) {
            is EstadoUiLogin.Exito -> {
                alIniciarSesionExito()
                viewModel.reiniciarEstado()
            }
            is EstadoUiLogin.Error -> {
                Toast.makeText(contexto, (estadoUi as EstadoUiLogin.Error).message, Toast.LENGTH_LONG).show()
                viewModel.reiniciarEstado()
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("CarDex", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = naranjaCarDex)
        Spacer(modifier = Modifier.height(32.dp))
        OutlinedTextField(value = usuario, onValueChange = { usuario = it }, label = { Text("Usuario") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = contrasena, onValueChange = { contrasena = it }, label = { Text("Contraseña") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = naranjaCarDex),
            enabled = estadoUi !is EstadoUiLogin.Cargando,
            onClick = {
                viewModel.iniciarSesion(usuario, contrasena)
            }
        ) {
            if (estadoUi is EstadoUiLogin.Cargando) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("Entrar", color = Color.White)
            }
        }
        TextButton(onClick = alNavegarARegistro) {
            Text("¿No tienes cuenta? Regístrate", color = naranjaCarDex)
        }
    }
}
