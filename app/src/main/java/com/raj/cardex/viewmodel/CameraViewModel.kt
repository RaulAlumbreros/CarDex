package com.raj.cardex.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raj.cardex.data.ServicioApi
import com.raj.cardex.data.CocheAvistado
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class EstadoUiCamara {
    object Idle : EstadoUiCamara()
    object Guardando : EstadoUiCamara()
    data class Exito(val coche: CocheAvistado) : EstadoUiCamara()
    data class Error(val message: String) : EstadoUiCamara()
}

class CameraViewModel(private val servicioApi: ServicioApi) : ViewModel() {
    private val _estadoUi = MutableStateFlow<EstadoUiCamara>(EstadoUiCamara.Idle)
    val estadoUi: StateFlow<EstadoUiCamara> = _estadoUi.asStateFlow()

    fun guardarCoche(coche: CocheAvistado) {
        viewModelScope.launch {
            _estadoUi.value = EstadoUiCamara.Guardando
            try {
                val respuesta = withContext(Dispatchers.IO) {
                    servicioApi.enviarCoche(coche)
                }
                if (respuesta.isSuccessful) {
                    _estadoUi.value = EstadoUiCamara.Exito(coche)
                } else {
                    _estadoUi.value = EstadoUiCamara.Error("No se pudo guardar")
                }
            } catch (e: Exception) {
                _estadoUi.value = EstadoUiCamara.Error("Error: ${e.message}")
            }
        }
    }

    fun reiniciarEstado() {
        _estadoUi.value = EstadoUiCamara.Idle
    }
}
