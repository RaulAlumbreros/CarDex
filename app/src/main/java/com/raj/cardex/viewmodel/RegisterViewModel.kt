package com.raj.cardex.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raj.cardex.data.ServicioApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class EstadoUiRegistro {
    object Idle : EstadoUiRegistro()
    object Cargando : EstadoUiRegistro()
    object Exito : EstadoUiRegistro()
    data class Error(val message: String) : EstadoUiRegistro()
}

class RegisterViewModel(private val servicioApi: ServicioApi) : ViewModel() {
    private val _estadoUi = MutableStateFlow<EstadoUiRegistro>(EstadoUiRegistro.Idle)
    val estadoUi: StateFlow<EstadoUiRegistro> = _estadoUi.asStateFlow()

    fun registrarUsuario(nombre: String, correo: String, contrasena: String) {
        viewModelScope.launch {
            _estadoUi.value = EstadoUiRegistro.Cargando
            try {
                val respuesta = withContext(Dispatchers.IO) {
                    servicioApi.registrarUsuario(nombre, correo, contrasena)
                }
                if (respuesta.isSuccessful) {
                    _estadoUi.value = EstadoUiRegistro.Exito
                } else {
                    _estadoUi.value = EstadoUiRegistro.Error("Error al registrar: ${respuesta.message()}")
                }
            } catch (e: Exception) {
                _estadoUi.value = EstadoUiRegistro.Error("Error: ${e.message}")
            }
        }
    }

    fun reiniciarEstado() {
        _estadoUi.value = EstadoUiRegistro.Idle
    }
}
