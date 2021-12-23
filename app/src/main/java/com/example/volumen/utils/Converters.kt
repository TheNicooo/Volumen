package com.example.volumen.utils

import androidx.room.TypeConverter
import com.example.volumen.api.portico.InfoPortico
import com.example.volumen.api.volumen.InfoVolumen
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    @TypeConverter
    fun restoreInfoPorticoData(objectToRestore: String?): InfoPortico? {
        return Gson().fromJson(objectToRestore, object : TypeToken<InfoPortico?>() {}.type)
    }

    @TypeConverter
    fun saveInfoPorticoData(objectToSave: InfoPortico?): String? {
        return Gson().toJson(objectToSave)
    }

    @TypeConverter
    fun restoreInfoVolumenData(objectToRestore: String?): InfoVolumen? {
        return Gson().fromJson(objectToRestore, object : TypeToken<InfoVolumen?>() {}.type)
    }

    @TypeConverter
    fun saveInfoVolumenData(objectToSave: InfoVolumen?): String? {
        return Gson().toJson(objectToSave)
    }

}