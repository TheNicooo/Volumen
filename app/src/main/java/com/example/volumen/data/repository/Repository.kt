package com.example.volumen.data.repository

import androidx.room.withTransaction
import com.example.volumen.api.ApiService
import com.example.volumen.api.volumen.CodeRequest
import com.example.volumen.data.AppDatabase
import com.example.volumen.utils.networkBoundResource
import javax.inject.Inject

class Repository @Inject constructor(
    private val apiService: ApiService,
    private val appDatabase: AppDatabase
) {

    private val porticoDao = appDatabase.porticoDao()
    private val volumenDao = appDatabase.volumenDao()

    fun porticoStatus(url: String) = networkBoundResource(
        databaseQuery = {
            porticoDao.getAllPortico()
        },
        networkCall = {
            apiService.getPorticoStatus(url)
        },
        saveCallResult = {
            appDatabase.withTransaction {
                porticoDao.deleteAllPortico()
                porticoDao.insertPortico(it)
            }
        }
    )

    fun getVolumen(url: String, code: CodeRequest) = networkBoundResource(
        databaseQuery = {
            volumenDao.getAllVolumen()
        },
        networkCall = {
            apiService.getVolumen(url, code)
        },
        saveCallResult = {
            appDatabase.withTransaction {
                volumenDao.deleteAllVolumen()
                volumenDao.insertVolumen(it)
            }
        }
    )

}