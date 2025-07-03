package net.techandgraphics.quantcal.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.techandgraphics.quantcal.data.remote.account.AccountApi
import net.techandgraphics.quantcal.data.remote.payment.PaymentApi
import net.techandgraphics.quantcal.di.NetworkModule.api
import net.techandgraphics.quantcal.keycloak.KeycloakApi
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
}
