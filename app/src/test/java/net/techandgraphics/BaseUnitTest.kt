package net.techandgraphics

import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import net.techandgraphics.wastemanagement.data.local.database.AppDatabase
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TestWatcher
import org.junit.runner.Description

abstract class BaseUnitTest {

  @MockK
  lateinit var mockDatabase: AppDatabase

  @OptIn(ExperimentalCoroutinesApi::class)
  @get:Rule
  var mainCoroutineRule = MainCoroutineRule()

  @Before
  fun baseSetup() {
    populateStaticTestData()
    mockDatabase = mockk()
  }

  protected open fun populateStaticTestData() = Unit

  @ExperimentalCoroutinesApi
  class MainCoroutineRule : TestWatcher() {
    private val testDispatcher = StandardTestDispatcher()

    override fun starting(description: Description) {
      super.starting(description)
      Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
      super.finished(description)
      Dispatchers.resetMain()
    }
  }
}
