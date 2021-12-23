package com.example.volumen.api.portico

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "portico_status")
data class PorticoStatus(
    @SerializedName("info_portico")
    val info_portico: InfoPortico,
    @SerializedName("message")
    @PrimaryKey val message: String
)