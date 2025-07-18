package net.techandgraphics.quantcal

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import net.techandgraphics.quantcal.notification.NotificationBuilder
import net.techandgraphics.quantcal.worker.WorkerFactory
import net.techandgraphics.quantcal.worker.company.account.scheduleCompanyAccountRequestWorker
import javax.inject.Inject

@HiltAndroidApp class AppKlass : Application(), Configuration.Provider {

  @Inject
  lateinit var workerFactory: WorkerFactory

  override val workManagerConfiguration: Configuration
    get() = Configuration.Builder()
      .setWorkerFactory(workerFactory)
      .setMinimumLoggingLevel(Log.ERROR)
      .build()

  override fun onCreate() {
    super.onCreate()
    scheduleCompanyAccountRequestWorker()
    NotificationBuilder(this).registerChannels()
  }
}
