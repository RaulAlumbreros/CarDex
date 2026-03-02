package com.raj.cardex.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raj.cardex.data.ServicioApi
import com.raj.cardex.data.GestorSesion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class EstadoUiLogin {
    object Idle : EstadoUiLogin()
    object Cargando : EstadoUiLogin()
    object Exito : EstadoUiLogin()
    data class Error(val message: String) : EstadoUiLogin()
}

class LoginViewModel(private val servicioApi: ServicioApi) : ViewModel() {
    private val _estadoUi = MutableStateFlow<EstadoUiLogin>(EstadoUiLogin.Idle)
    val estadoUi: StateFlow<EstadoUiLogin> = _estadoUi.asStateFlow()

    fun iniciarSesion(usuario: String, contrasena: String) {
        viewModelScope.launch {
            _estadoUi.value = EstadoUiLogin.Cargando
            try {
                val respuesta = withContext(Dispatchers.IO) {
                    servicioApi.iniciarSesion(usuario, contrasena)
                }
                if (respuesta.isSuccessful) {
                    GestorSesion.usuarioActual = usuario
                    _estadoUi.value = EstadoUiLogin.Exito
                } else {
                    _estadoUi.value = EstadoUiLogin.Error("Usuario o contraseña incorrectos")
                }
            } catch (e: Exception) {
                _estadoUi.value = EstadoUiLogin.Error("Error de conexión: ${e.message}")
            }
        }
    }

    fun reiniciarEstado() {
        _estadoUi.value = EstadoUiLogin.Idle
    }
}
