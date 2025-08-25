package com.iries.alarm.di


import com.iries.alarm.data.remote.AuthRepository
import com.iries.alarm.data.remote.SearchApiRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient {
        return HttpClient(CIO)
        {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }

    @Provides
    @Singleton
    fun provideSearchApiRepository(httpClient: HttpClient): SearchApiRepository {
        return SearchApiRepository(httpClient)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(httpClient: HttpClient): AuthRepository {
        return AuthRepository(httpClient)
    }
}
