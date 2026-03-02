package com.raj.cardex.data.repositorio
import com.raj.cardex.data.CocheAvistado
import com.raj.cardex.data.ServicioApi
import com.raj.cardex.data.local.CocheDao 
import com.raj.cardex.data.GestorSesion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
class RepositorioCoches(
    private val servicioApi: ServicioApi,
    private val carDao: CocheDao 
) {
    suspend fun obtenerCoches(): List<CocheAvistado> = withContext(Dispatchers.IO) { 
        try {
            val cochesRemotos = servicioApi.obtenerCoches().filter { it.usuario == GestorSesion.usuarioActual }
            if (cochesRemotos.isNotEmpty()) {
                carDao.insertarCoches(cochesRemotos)
            }
            cochesRemotos
        } catch (e: Exception) {
            carDao.obtenerTodosLosCoches().filter { it.usuario == GestorSesion.usuarioActual }
        }
    }
}
