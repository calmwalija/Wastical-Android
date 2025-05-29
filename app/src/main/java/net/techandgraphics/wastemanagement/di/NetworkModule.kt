package net.techandgraphics.wastemanagement.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.http.Url
import io.ktor.serialization.gson.gson
import net.techandgraphics.wastemanagement.AppUrl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

  @Provides
  @Singleton
  fun provideKtorClient(): HttpClient = HttpClient {
    val baseUrl = Url(AppUrl.API_URL)
    install(ContentNegotiation) { gson() }
    install(Logging) {
      logger = Logger.SIMPLE
      level = LogLevel.ALL
    }
    defaultRequest {
      url {
        protocol = baseUrl.protocol
        host = baseUrl.host
        port = baseUrl.port
      }
    }
  }
}
