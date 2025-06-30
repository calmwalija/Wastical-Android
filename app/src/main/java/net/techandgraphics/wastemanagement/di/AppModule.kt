package net.techandgraphics.wastemanagement.di

import android.accounts.AccountManager
import android.app.Application
import androidx.room.Room
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.techandgraphics.wastemanagement.account.AuthenticatorHelper
import net.techandgraphics.wastemanagement.data.local.database.AppDatabase
import java.util.concurrent.Executors
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

  @Singleton
  @Provides
  fun providesAppDatabase(app: Application): AppDatabase {
    return Room.databaseBuilder(app, AppDatabase::class.java, AppDatabase.NAME)
      .fallbackToDestructiveMigration()
      .setQueryCallback(
        queryCallback = { query, args ->
//          println("query $query == args $args")
        },
        Executors.newSingleThreadExecutor(),
      )
      .build()
  }

  @Provides
  @Singleton
  fun providesGson(): Gson = Gson()

  @Provides
  @Singleton
  fun providesAccountManager(app: Application): AccountManager = AccountManager.get(app)

  @Provides
  @Singleton
  fun providesAuthenticatorHelper(accountManager: AccountManager): AuthenticatorHelper =
    AuthenticatorHelper(accountManager)
}
