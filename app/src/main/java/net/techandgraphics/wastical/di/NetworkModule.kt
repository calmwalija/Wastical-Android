package net.techandgraphics.wastical.di

import com.google.common.net.HttpHeaders
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import net.techandgraphics.wastical.appUrl
import net.techandgraphics.wastical.data.local.Preferences
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

  inline fun <reified T> api(
    okHttpClient: OkHttpClient,
    baseUrl: String = appUrl().apiDomain,
  ): T =
    Retrofit.Builder().baseUrl(baseUrl)
      .client(okHttpClient)
      .addConverterFactory(GsonConverterFactory.create())
      .build()
      .create(T::class.java)

  @Singleton
  @Provides
  fun authOkHttpClient(preferences: Preferences) = OkHttpClient.Builder()
    .addInterceptor(
      Interceptor { chain: Interceptor.Chain ->
        val request = chain.request().newBuilder()
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer")
        chain.proceed(request.build())
      },
    )
    .addInterceptor { chain ->
      val request = chain.request()
      val requestUrl = request.url.toString()
      val urlEtag = runBlocking { preferences.get<String?>(requestUrl, null) }
      val newRequest = request.newBuilder().apply {
        urlEtag?.let { addHeader(HttpHeaders.LAST_MODIFIED, urlEtag) }
      }.build()
      chain.proceed(newRequest)
    }
    .addInterceptor(
      HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
        redactHeader(HttpHeaders.AUTHORIZATION)
        redactHeader(HttpHeaders.COOKIE)
      },
    )
    .protocols(listOf(Protocol.HTTP_1_1))
    .retryOnConnectionFailure(false)
    .connectTimeout(30, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .writeTimeout(30, TimeUnit.SECONDS)
    .build()
}
