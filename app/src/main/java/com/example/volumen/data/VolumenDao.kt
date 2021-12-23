package com.example.volumen.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.volumen.api.volumen.Volumen
import kotlinx.coroutines.flow.Flow

@Dao
interface VolumenDao {

    @Query("SELECT * FROM volumen")
    fun getAllVolumen(): Flow<Volumen>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVolumen(volumen: Volumen)

    @Query("DELETE FROM volumen")
    suspend fun deleteAllVolumen()
}