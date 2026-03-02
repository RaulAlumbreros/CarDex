package com.raj.cardex.data
data class Car(
    val id: Int,
    val nombre: String,
    val marca: String,
    val isUnlocked: Boolean,
    val rutaImagen: String? = null 
)
