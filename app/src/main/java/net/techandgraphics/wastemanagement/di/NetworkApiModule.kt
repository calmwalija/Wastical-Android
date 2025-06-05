package net.techandgraphics.wastemanagement.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.techandgraphics.wastemanagement.data.remote.account.AccountApi
import net.techandgraphics.wastemanagement.data.remote.payment.PaymentApi
import net.techandgraphics.wastemanagement.di.NetworkModule.api
import net.techandgraphics.wastemanagement.keycloak.KeycloakApi
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
