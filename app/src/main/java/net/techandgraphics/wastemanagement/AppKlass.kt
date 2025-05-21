package net.techandgraphics.wastemanagement

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import net.techandgraphics.wastemanagement.notification.NotificationBuilder
import net.techandgraphics.wastemanagement.worker.AppWorkerFactory
import net.techandgraphics.wastemanagement.worker.schedulePaymentRetryWorker
import javax.inject.Inject

@HiltAndroidApp class AppKlass : Application(), Configuration.Provider {

  @Inject
  lateinit var workerFactory: AppWorkerFactory

  override val workManagerConfiguration: Configuration
    get() = Configuration.Builder()
      .setWorkerFactory(workerFactory)
      .setMinimumLoggingLevel(Log.ERROR)
      .build()

  override fun onCreate() {
    super.onCreate()
    schedulePaymentRetryWorker()
    NotificationBuilder(this).registerChannels()
  }
}
