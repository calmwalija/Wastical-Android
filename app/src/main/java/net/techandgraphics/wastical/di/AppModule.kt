package net.techandgraphics.wastical.di

import android.accounts.AccountManager
import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.WorkManager
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import net.techandgraphics.wastical.account.AuthenticatorHelper
import net.techandgraphics.wastical.data.local.Preferences
import net.techandgraphics.wastical.data.local.database.AppDatabase
import net.techandgraphics.wastical.ui.screen.AccountLogout
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
      .addCallback(object : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
          super.onCreate(db)
          // Pre-fill notification templates on first install
          val insertSql = "INSERT INTO notification_template (title, body, scope, created_at, updated_at) VALUES (?, ?, ?, ?, ?)"
          val now = System.currentTimeMillis() / 1000
          val templates = listOf(
            arrayOf("Service Interruption", "Dear customer, there will be a temporary service interruption today. We apologize for any inconvenience.", "COMPANY", now, now),
            arrayOf("Payment Reminder", "Kind reminder to complete your monthly payment to avoid service disruption. Thank you.", "COMPANY", now, now),
            arrayOf("Collection Notice", "Our team will collect waste in your area tomorrow. Please place your bins outside by 7:00 AM.", "LOCATION", now, now),
            arrayOf("Welcome", "Welcome to our service! We're glad to have you onboard.", "ACCOUNT", now, now),
            arrayOf("Outstanding Balance", "You have an outstanding balance. Please make a payment at your earliest convenience.", "ACCOUNT", now, now),
          )
          templates.forEach { values ->
            db.execSQL(insertSql, values)
          }
        }
      })
      .setQueryCallback(
        queryCallback = { query, args ->
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
  fun providesAccountLogout(
    database: AppDatabase,
    accountManager: AccountManager,
    authenticatorHelper: AuthenticatorHelper,
    application: Application,
    preferences: Preferences,
    gson: Gson,
  ): AccountLogout = AccountLogout(
    database = database,
    authenticatorHelper = authenticatorHelper,
    accountManager = accountManager,
    application = application,
    preferences = preferences,
  )

  @Provides
  @Singleton
  fun providesWorkManager(application: Application): WorkManager {
    return WorkManager.getInstance(application)
  }

  @Provides
  @Singleton
  fun providesAccountManager(app: Application): AccountManager = AccountManager.get(app)

  @Provides
  @Singleton
  fun providesAuthenticatorHelper(accountManager: AccountManager): AuthenticatorHelper =
    AuthenticatorHelper(accountManager)
}
