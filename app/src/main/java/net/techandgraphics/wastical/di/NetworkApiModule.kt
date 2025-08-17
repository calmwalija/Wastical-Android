package net.techandgraphics.wastical.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.techandgraphics.wastical.data.remote.LastUpdatedApi
import net.techandgraphics.wastical.data.remote.account.AccountApi
import net.techandgraphics.wastical.data.remote.account.otp.AccountOtpApi
import net.techandgraphics.wastical.data.remote.notification.NotificationApi
import net.techandgraphics.wastical.data.remote.payment.PaymentApi
import net.techandgraphics.wastical.di.NetworkModule.api
import net.techandgraphics.wastical.keycloak.KeycloakApi
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkApiModule {

  @Provides
  @Singleton
  fun providesAccountApi(okHttpClient: OkHttpClient) = api<AccountApi>(okHttpClient)

  @Provides
  @Singleton
  fun providesPaymentApi(okHttpClient: OkHttpClient) = api<PaymentApi>(okHttpClient)

  @Provides
  @Singleton
  fun providesKeycloakApi(okHttpClient: OkHttpClient) = api<KeycloakApi>(okHttpClient)

  @Provides
  @Singleton
  fun providesAccountOtpApi(okHttpClient: OkHttpClient) = api<AccountOtpApi>(okHttpClient)

  @Provides
  @Singleton
  fun providesLastUpdatedApi(okHttpClient: OkHttpClient) = api<LastUpdatedApi>(okHttpClient)

  @Provides
  @Singleton
  fun providesNotificationApi(okHttpClient: OkHttpClient) = api<NotificationApi>(okHttpClient)
}
