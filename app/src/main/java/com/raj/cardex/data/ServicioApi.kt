package com.raj.cardex.data
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
data class RegistroResponse(
    val status: String,
    val message: String
)
interface ServicioApi {
    @POST("login")
    suspend fun iniciarSesion(
        @Query("user") usuario: String,
        @Query("pass") contrasena: String
    ): Response<RegistroResponse>
    @POST("register")
    suspend fun registrarUsuario(
        @Query("user") nombre: String,
        @Query("email") correo: String,
        @Query("pass") contrasena: String
    ): Response<RegistroResponse>
    @POST("nuevo-coche")
    suspend fun enviarCoche(@Body coche: CocheAvistado): Response<Unit>
    @GET("coches")
    suspend fun obtenerCoches(): List<CocheAvistado>
}
