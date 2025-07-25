package net.techandgraphics.wcompanion.di

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.techandgraphics.wcompanion.data.local.database.QgatewayDatabase

@EntryPoint
@InstallIn(SingletonComponent::class)
interface AppEntryPoint {
  fun aQgatewayDatabase(): QgatewayDatabase
}
