package net.techandgraphics.wastemanagement.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.techandgraphics.wastemanagement.data.remote.account.AccountApi
import net.techandgraphics.wastemanagement.data.remote.account.AccountApiImpl
import net.techandgraphics.wastemanagement.data.remote.company.CompanyApi
import net.techandgraphics.wastemanagement.data.remote.company.CompanyApiImpl
import net.techandgraphics.wastemanagement.data.remote.payment.PaymentApi
import net.techandgraphics.wastemanagement.data.remote.payment.PaymentApiImpl
import net.techandgraphics.wastemanagement.keycloak.KeycloakApi
import net.techandgraphics.wastemanagement.keycloak.KeycloakApiImpl

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkRepositoryModule {

  @Binds
  abstract fun bindsKeycloakApi(p0: KeycloakApiImpl): KeycloakApi

  @Binds
  abstract fun bindsCompanyApi(p0: CompanyApiImpl): CompanyApi

  @Binds
  abstract fun bindsAccountApi(p0: AccountApiImpl): AccountApi

  @Binds
  abstract fun bindsPaymentApi(p0: PaymentApiImpl): PaymentApi
}
