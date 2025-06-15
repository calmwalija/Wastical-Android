package net.techandgraphics.wastemanagement

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import net.techandgraphics.wastemanagement.data.local.database.AppDatabase
import org.junit.After
import org.junit.Before

abstract class BaseTest {

  protected lateinit var database: AppDatabase
  protected lateinit var context: Context

  @Before
  fun baseSetup() {
    context = ApplicationProvider.getApplicationContext()
    database = Room.inMemoryDatabaseBuilder(
      context,
      AppDatabase::class.java,
    ).allowMainThreadQueries().build()

    populateStaticTestData()
  }

  @After
  fun baseTeardown() {
    database.close()
  }

  protected open fun populateStaticTestData() = Unit
}
