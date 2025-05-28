package net.techandgraphics.wastemanagement.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.gson.gson
import net.techandgraphics.wastemanagement.AppUrl
import net.techandgraphics.wastemanagement.data.remote.AppApi
import net.techandgraphics.wastemanagement.data.remote.account.AccountApi
import net.techandgraphics.wastemanagement.data.remote.company.CompanyApi
import net.techandgraphics.wastemanagement.keycloak.KeycloakApi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

  private const val AUTHORIZATION = "Authorization"
  private const val COOKIE = "Cookie"

  inline fun <reified T> api(baseUrl: String = AppUrl.API_URL): T =
    Retrofit.Builder().baseUrl(baseUrl)
      .client(authOkHttpClient())
      .addConverterFactory(GsonConverterFactory.create())
      .build()
      .create(T::class.java)

  @Singleton
  @Provides
  fun authOkHttpClient() = OkHttpClient.Builder()
    .addInterceptor(
      Interceptor { chain: Interceptor.Chain ->
        val request = chain.request().newBuilder()
        request.addHeader(AUTHORIZATION, "Bearer")
        chain.proceed(request.build())
      },
    )
    .addInterceptor(
      HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
        redactHeader(AUTHORIZATION)
        redactHeader(COOKIE)
      },
    )
    .retryOnConnectionFailure(false)
    .build()

  @Provides
  @Singleton
  fun providesApi(): AppApi {
    return AppApi(
      accountApi = api<AccountApi>(),
      companyApi = api<CompanyApi>(),
      keycloakApi = api<KeycloakApi>(),
      paymentApi = api(),
    )
  }

  @Provides
  @Singleton
  fun provideKtorClient(): HttpClient = HttpClient {
    install(ContentNegotiation) { gson() }
    install(Logging) { level = LogLevel.INFO }
    defaultRequest { url { host = AppUrl.API_URL } }
  }
}
