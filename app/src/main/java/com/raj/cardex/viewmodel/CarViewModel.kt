package com.raj.cardex.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raj.cardex.data.CocheAvistado
import com.raj.cardex.data.repositorio.RepositorioCoches
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class EstadoUiCardex {
    object Cargando : EstadoUiCardex()
    data class Exito(val coches: List<CocheAvistado>) : EstadoUiCardex()
    data class Error(val mensaje: String) : EstadoUiCardex()
}

class CarViewModel(private val repositorio: RepositorioCoches) : ViewModel() {
    private val _estadoUi = MutableStateFlow<EstadoUiCardex>(EstadoUiCardex.Cargando)
    val estadoUi: StateFlow<EstadoUiCardex> = _estadoUi.asStateFlow()

    init {
        obtenerCoches()
    }

    fun obtenerCoches() {
        viewModelScope.launch {
            _estadoUi.value = EstadoUiCardex.Cargando
            try {
                val coches = repositorio.obtenerCoches()
                _estadoUi.value = EstadoUiCardex.Exito(coches)
            } catch (e: Exception) {
                _estadoUi.value = EstadoUiCardex.Error("Error: ${e.message}")
            }
        }
    }
}
