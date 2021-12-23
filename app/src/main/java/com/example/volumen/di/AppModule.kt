package com.example.volumen.di

import android.app.Application
import androidx.room.Room
import com.example.volumen.api.ApiService
import com.example.volumen.data.AppDatabase
import com.example.volumen.utils.URL_BASE
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(URL_BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client().build())
            .build()

    private fun client(): OkHttpClient.Builder {
        return OkHttpClient.Builder().apply {
            readTimeout(30, TimeUnit.SECONDS)
            writeTimeout(30, TimeUnit.SECONDS)
            connectTimeout(30, TimeUnit.SECONDS)
        }
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    fun provideDataBase(app: Application) : AppDatabase =
        Room.databaseBuilder(app, AppDatabase::class.java, "app_data_starken")
            .fallbackToDestructiveMigration()
            .build()

//    @Provides
//    fun provideServerDao(db: AppDatabase) = db.serverDao()
}