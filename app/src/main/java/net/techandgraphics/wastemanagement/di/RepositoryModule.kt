package net.techandgraphics.wastemanagement.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.techandgraphics.wastemanagement.data.local.database.account.session.AccountSessionRepository
import net.techandgraphics.wastemanagement.data.local.database.account.session.AccountSessionRepositoryImpl
import net.techandgraphics.wastemanagement.data.remote.payment.pay.PaymentRepository
import net.techandgraphics.wastemanagement.data.remote.payment.pay.PaymentRepositoryImpl

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

  @Binds
  abstract fun providesSessionRepository(p0: AccountSessionRepositoryImpl): AccountSessionRepository

  @Binds
  abstract fun providesPaymentRepository(p0: PaymentRepositoryImpl): PaymentRepository
}
