package net.techandgraphics.wastemanagement.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.techandgraphics.wastemanagement.data.local.database.session.SessionRepository
import net.techandgraphics.wastemanagement.data.local.database.session.SessionRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

  @Binds
  abstract fun providesSessionRepository(p0: SessionRepositoryImpl): SessionRepository
}
