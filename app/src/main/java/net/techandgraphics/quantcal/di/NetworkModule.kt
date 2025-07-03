package net.techandgraphics.quantcal.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.techandgraphics.quantcal.appUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

  private const val AUTHORIZATION = "Authorization"
  private const val COOKIE = "Cookie"

  inline fun <reified T> api(baseUrl: String = appUrl().apiDomain): T =
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
    .protocols(listOf(Protocol.HTTP_1_1))
    .retryOnConnectionFailure(false)
    .build()
}
