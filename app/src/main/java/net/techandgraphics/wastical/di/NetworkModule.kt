package net.techandgraphics.wastical.di

import com.google.common.net.HttpHeaders
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import net.techandgraphics.wastical.appUrl
import net.techandgraphics.wastical.data.local.Preferences
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
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
    .cache(Cache(File(preferences.context.cacheDir, "http_cache"), 20L * 1024 * 1024))
    .addInterceptor(
      Interceptor { chain: Interceptor.Chain ->
        val request = chain.request().newBuilder()
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer")
        chain.proceed(request.build())
      },
    )
    .addInterceptor { chain ->
      val request = chain.request()
      val rawUrl = request.url
      val requestUrl = rawUrl.toString()
      val canonicalUrl = rawUrl.newBuilder().query(null).build().toString()
      val storedEtag = runBlocking {
        preferences.get<String?>("$requestUrl#etag", null)
          ?: preferences.get<String?>("$canonicalUrl#etag", null)
          ?: preferences.get<String?>(requestUrl, null)
      }
      val storedLastModified = runBlocking {
        preferences.get<String?>("$requestUrl#last_modified", null)
          ?: preferences.get<String?>("$canonicalUrl#last_modified", null)
      }
      val newRequest = request.newBuilder().apply {
        storedEtag?.let { addHeader(HttpHeaders.IF_NONE_MATCH, it) }
        storedLastModified?.let { addHeader(HttpHeaders.IF_MODIFIED_SINCE, it) }
      }.build()
      val response = chain.proceed(newRequest)
      runCatching {
        val etag = response.header(HttpHeaders.ETAG)
        val lastMod = response.header(HttpHeaders.LAST_MODIFIED)
        if (etag != null) {
          runBlocking {
            preferences.put("$requestUrl#etag", etag)
            preferences.put("$canonicalUrl#etag", etag)
            preferences.put(requestUrl, etag)
          }
        }
        if (lastMod != null) {
          runBlocking {
            preferences.put("$requestUrl#last_modified", lastMod)
            preferences.put("$canonicalUrl#last_modified", lastMod)
          }
        }
      }
      if (response.header("Cache-Control") == null) {
        response.newBuilder()
          .header("Cache-Control", "public, max-age=3600")
          .build()
      } else {
        response
      }
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
