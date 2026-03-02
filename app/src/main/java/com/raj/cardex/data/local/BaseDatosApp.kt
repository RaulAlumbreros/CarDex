package com.raj.cardex.data.local
import androidx.room.Database
import androidx.room.RoomDatabase
import com.raj.cardex.data.CocheAvistado
@Database(entities = [CocheAvistado::class], version = 1)
abstract class BaseDatosApp : RoomDatabase() {
    abstract fun carDao(): CocheDao
}
