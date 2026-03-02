package com.raj.cardex.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raj.cardex.data.ServicioApi
import com.raj.cardex.data.CocheAvistado
import com.raj.cardex.data.GestorSesion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class EstadoUiMapa {
    object Cargando : EstadoUiMapa()
    data class Exito(val coches: List<CocheAvistado>) : EstadoUiMapa()
    data class Error(val mensaje: String) : EstadoUiMapa()
}

class MapViewModel(private val servicioApi: ServicioApi) : ViewModel() {
    private val _estadoUi = MutableStateFlow<EstadoUiMapa>(EstadoUiMapa.Cargando)
    val estadoUi: StateFlow<EstadoUiMapa> = _estadoUi.asStateFlow()

    fun cargarCoches() {
        viewModelScope.launch {
            _estadoUi.value = EstadoUiMapa.Cargando
            try {
                val lista = withContext(Dispatchers.IO) {
                    servicioApi.obtenerCoches()
                }
                val filtrados = lista.filter { it.usuario == GestorSesion.usuarioActual }
                _estadoUi.value = EstadoUiMapa.Exito(filtrados)
            } catch (e: Exception) {
                _estadoUi.value = EstadoUiMapa.Error("Error: ${e.message}")
            }
        }
    }
}
