package net.techandgraphics.quantcal.di

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.techandgraphics.quantcal.data.local.database.AppDatabase

@EntryPoint
@InstallIn(SingletonComponent::class)
interface AppEntryPoint {
  fun appDatabase(): AppDatabase
}
