package com.example.volumen.api

import com.example.volumen.api.portico.PorticoStatus
import com.example.volumen.api.volumen.CodeRequest
import com.example.volumen.api.volumen.Volumen
import retrofit2.http.*

interface ApiService {

    @POST()
    suspend fun getPorticoStatus(
        @Url url: String,
        @Body body: Any = Object()
    ): PorticoStatus

    @POST()
    suspend fun getVolumen(
        @Url url: String,
        @Body body: CodeRequest
    ): Volumen

}