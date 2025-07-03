package net.techandgraphics.quantcal.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.techandgraphics.quantcal.keycloak.JwtManager
import net.techandgraphics.quantcal.keycloak.KeycloakApi
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object KeycloakModule {

  @Provides
  @Singleton
  fun providesJwtManager(api: KeycloakApi): JwtManager {
    return JwtManager(api)
  }
}
