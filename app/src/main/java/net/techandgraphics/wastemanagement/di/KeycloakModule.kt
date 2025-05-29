package net.techandgraphics.wastemanagement.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.techandgraphics.wastemanagement.keycloak.JwtManager
import net.techandgraphics.wastemanagement.keycloak.KeycloakApi
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
