package net.techandgraphics.qgateway.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.techandgraphics.qgateway.data.local.database.QgatewayDatabase
import java.util.concurrent.Executors
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

  @Singleton
  @Provides
  fun providesAppDatabase(app: Application): QgatewayDatabase {
    return Room.databaseBuilder(
      app,
      QgatewayDatabase::class.java,
      QgatewayDatabase.NAME,
    )
      .fallbackToDestructiveMigration()
      .setQueryCallback(
        queryCallback = { query, args ->
          println("query $query == args $args")
        },
        Executors.newSingleThreadExecutor(),
      )
      .build()
  }
}
