package net.techandgraphics.wastemanagement.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.techandgraphics.wastemanagement.data.remote.AppApi
import net.techandgraphics.wastemanagement.keycloak.JwtManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object KeycloakModule {

  @Provides
  @Singleton
  fun providesJwtManager(api: AppApi): JwtManager {
    return JwtManager(api.keycloakApi)
  }
}
