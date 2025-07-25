package net.techandgraphics.wcompanion

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import net.techandgraphics.wcompanion.worker.WorkerFactory
import javax.inject.Inject

@HiltAndroidApp class AppKlass : Application(), Configuration.Provider {

  @Inject
  lateinit var workerFactory: WorkerFactory

  override val workManagerConfiguration: Configuration
    get() = Configuration.Builder()
      .setWorkerFactory(workerFactory)
      .setMinimumLoggingLevel(Log.ERROR)
      .build()
}
