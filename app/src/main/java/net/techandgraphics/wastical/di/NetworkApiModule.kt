package net.techandgraphics.wastical.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.techandgraphics.wastical.data.remote.account.AccountApi
import net.techandgraphics.wastical.data.remote.account.otp.AccountOtpApi
import net.techandgraphics.wastical.data.remote.payment.PaymentApi
import net.techandgraphics.wastical.di.NetworkModule.api
import net.techandgraphics.wastical.keycloak.KeycloakApi
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkApiModule {

  @Provides
  @Singleton
  fun providesAccountApi() = api<AccountApi>()

  @Provides
  @Singleton
  fun providesPaymentApi() = api<PaymentApi>()

  @Provides
  @Singleton
  fun providesKeycloakApi() = api<KeycloakApi>()

  @Provides
  @Singleton
  fun providesAccountOtpApi() = api<AccountOtpApi>()
}
