package com.raj.cardex.data.local
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.raj.cardex.data.CocheAvistado
@Database(entities = [CocheAvistado::class], version = 2, exportSchema = false)
abstract class BaseDatosCoches : RoomDatabase() {
    abstract fun carDao(): CocheDao
    companion object {
        @Volatile
        private var INSTANCE: BaseDatosCoches? = null
        fun getDatabase(contexto: Context): BaseDatosCoches {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    contexto.applicationContext,
                    BaseDatosCoches::class.java,
                    "car_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
