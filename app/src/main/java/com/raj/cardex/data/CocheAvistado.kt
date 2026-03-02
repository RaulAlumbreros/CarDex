package com.raj.cardex.data
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
@Entity(tableName = "coches") 
data class CocheAvistado(
    @PrimaryKey 
    @SerializedName("name") val nombre: String,
    @SerializedName("brand") val marca: String,
    @SerializedName("lat") val latitud: Double,
    @SerializedName("lng") val longitud: Double,
    @SerializedName("user") val usuario: String,
    @SerializedName("imagePath") val rutaImagen: String? = null
)
