package net.techandgraphics.quantcal.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.techandgraphics.quantcal.data.local.database.account.session.AccountSessionRepository
import net.techandgraphics.quantcal.data.local.database.account.session.AccountSessionRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

  @Binds
  abstract fun providesSessionRepository(p0: AccountSessionRepositoryImpl): AccountSessionRepository
}
