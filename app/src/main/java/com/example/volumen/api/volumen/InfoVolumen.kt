package com.example.volumen.api.volumen

import com.google.gson.annotations.SerializedName

data class InfoVolumen(
    @SerializedName("alto")
    val alto: Double,
    @SerializedName("ancho")
    val ancho: Double,
    @SerializedName("largo")
    val largo: Double,
    @SerializedName("volumen")
    val volumen: Double
)