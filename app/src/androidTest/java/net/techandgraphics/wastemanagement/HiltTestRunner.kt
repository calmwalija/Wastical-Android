package net.techandgraphics.wastemanagement

import android.app.Activity
import android.content.Intent
import androidx.test.runner.AndroidJUnitRunner
import dagger.hilt.android.testing.HiltTestApplication

class HiltTestRunner : AndroidJUnitRunner() {

  override fun newActivity(cl: ClassLoader?, className: String?, intent: Intent?): Activity? {
    return super.newActivity(cl, HiltTestApplication::class.java.name, intent)
  }
}
