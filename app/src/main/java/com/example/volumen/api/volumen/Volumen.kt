package com.example.volumen.api.volumen

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "volumen")
data class Volumen(
    @SerializedName("codigo")
    @PrimaryKey val codigo: String,
    @SerializedName("imagen")
    val imagen: String,
    @SerializedName("info_volumen")
    val info_volumen: InfoVolumen,
    @SerializedName("timestamp")
    val timestamp: String
)