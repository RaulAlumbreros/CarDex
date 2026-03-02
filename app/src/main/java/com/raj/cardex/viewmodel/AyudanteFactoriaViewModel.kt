package com.raj.cardex.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.raj.cardex.data.ServicioApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.content.Context
import com.raj.cardex.data.local.BaseDatosCoches
import com.raj.cardex.data.repositorio.RepositorioCoches
object AyudanteFactoriaViewModel {
    val servicioApi: ServicioApi by lazy {
        Retrofit.Builder()
            .baseUrl("http://172.16.52.112:5000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ServicioApi::class.java)
    }
    fun provideLoginViewModelFactory(): ViewModelProvider.Factory = 
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                    return LoginViewModel(servicioApi) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    fun provideRegisterViewModelFactory(): ViewModelProvider.Factory = 
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
                    return RegisterViewModel(servicioApi) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    fun provideCameraViewModelFactory(): ViewModelProvider.Factory = 
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(CameraViewModel::class.java)) {
                    return CameraViewModel(servicioApi) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    fun provideMapViewModelFactory(): ViewModelProvider.Factory = 
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
                    return MapViewModel(servicioApi) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    fun provideCarViewModelFactory(contexto: Context): ViewModelProvider.Factory = 
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(CarViewModel::class.java)) {
                    val bd = BaseDatosCoches.getDatabase(contexto)
                    val repositorio = RepositorioCoches(servicioApi, bd.carDao())
                    return CarViewModel(repositorio) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
}
