package net.techandgraphics.qgateway.di

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.techandgraphics.qgateway.data.local.database.QgatewayDatabase

@EntryPoint
@InstallIn(SingletonComponent::class)
interface AppEntryPoint {
  fun aQgatewayDatabase(): QgatewayDatabase
}
