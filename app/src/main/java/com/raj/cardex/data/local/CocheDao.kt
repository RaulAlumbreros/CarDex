package com.raj.cardex.data.local
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.raj.cardex.data.CocheAvistado
@Dao
interface CocheDao {
    @Query("SELECT * FROM coches")
    suspend fun obtenerTodosLosCoches(): List<CocheAvistado>
    @Insert(onConflict = OnConflictStrategy.REPLACE) 
    suspend fun insertarCoches(coches: List<CocheAvistado>)
}
