package com.thecodefather.untigrito.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl("https://api.example.com/") // TODO: Reemplazar con la URL base real de tu API
            .addConverterFactory(Json.asConverterFactory(contentType))
            .build()
    }

    // Aquí puedes añadir más providers para tus servicios de API, por ejemplo:
    // @Provides
    // @Singleton
    // fun provideClientApiService(retrofit: Retrofit): ClientApiService {
    //     return retrofit.create(ClientApiService::class.java)
    // }
}
