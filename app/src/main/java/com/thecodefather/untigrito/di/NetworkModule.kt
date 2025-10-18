package com.thecodefather.untigrito.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.thecodefather.untigrito.data.datasource.remote.AuthApiService
import com.thecodefather.untigrito.data.datasource.remote.AuthErrorInterceptor
import com.thecodefather.untigrito.data.datasource.remote.ClientApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(authErrorInterceptor: AuthErrorInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authErrorInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl("https://mvp-tigrito-web.vercel.app/api/") // URL base de la API de UnTigrito
            .client(okHttpClient)
            .addConverterFactory(Json.asConverterFactory(contentType))
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideClientApiService(retrofit: Retrofit): ClientApiService {
        return retrofit.create(ClientApiService::class.java)
    }
}
