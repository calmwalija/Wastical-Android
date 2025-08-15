package net.techandgraphics.wastical.ui.screen.company.notification

import androidx.paging.PagingSource
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import net.techandgraphics.wastical.BaseUnitTest
import net.techandgraphics.wastical.data.local.database.company.CompanyDao
import net.techandgraphics.wastical.data.local.database.notification.NotificationDao
import net.techandgraphics.wastical.data.local.database.notification.NotificationEntity
import net.techandgraphics.wastical.data.local.database.toCompanyEntity
import net.techandgraphics.wastical.ui.screen.company4Preview
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CompanyNotificationViewModelTest : BaseUnitTest() {

  @Before
  fun setUp() {
    every { mockDatabase.companyDao } returns mockk<CompanyDao>()
    every { mockDatabase.notificationDao } returns mockk<NotificationDao>()
    coEvery { mockDatabase.companyDao.query() } returns listOf(company4Preview.toCompanyEntity())
  }

  private val fakePagingSource = object : PagingSource<Int, NotificationEntity>() {
    override fun getRefreshKey(state: androidx.paging.PagingState<Int, NotificationEntity>): Int? =
      null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NotificationEntity> =
      LoadResult.Page(emptyList(), null, null)
  }

  @Test
  fun `initial load should fetch company and build paging flow`() = runTest {
    coEvery { mockDatabase.companyDao.query() } returns listOf(company4Preview.toCompanyEntity())

    every {
      mockDatabase.notificationDao.flowOfPaging(
        query = any(),
        sort = any(),
      )
    } returns fakePagingSource

    val viewModel = CompanyNotificationViewModel(mockDatabase)

    viewModel.onEvent(CompanyNotificationEvent.Load)
    advanceUntilIdle()

    verify(atLeast = 1) { mockDatabase.notificationDao.flowOfPaging(query = "", sort = true) }
  }

  @Test
  fun `query input should debounce and rebuild paging with trimmed term`() = runTest {
    coEvery { mockDatabase.companyDao.query() } returns listOf(company4Preview.toCompanyEntity())

    every {
      mockDatabase.notificationDao.flowOfPaging(
        query = any(),
        sort = any(),
      )
    } returns fakePagingSource

    val viewModel = CompanyNotificationViewModel(mockDatabase)
    viewModel.onEvent(CompanyNotificationEvent.Load)
    advanceUntilIdle()
    viewModel.onEvent(CompanyNotificationEvent.Input.Query("  bar  "))
    advanceTimeBy(600)
    advanceUntilIdle()

    verify { mockDatabase.notificationDao.flowOfPaging(query = "bar", sort = true) }
  }

  @Test
  fun `rapid successive queries should cancel previous and only invoke last`() = runTest {
    coEvery { mockDatabase.companyDao.query() } returns listOf(company4Preview.toCompanyEntity())

    val queries = mutableListOf<String>()
    every {
      mockDatabase.notificationDao.flowOfPaging(
        query = capture(queries),
        sort = any(),
      )
    } returns fakePagingSource

    val viewModel = CompanyNotificationViewModel(mockDatabase)
    viewModel.onEvent(CompanyNotificationEvent.Load)
    advanceUntilIdle()

    viewModel.onEvent(CompanyNotificationEvent.Input.Query("alpha"))
    advanceTimeBy(200)
    viewModel.onEvent(CompanyNotificationEvent.Input.Query("beta"))

    advanceTimeBy(600)
    advanceUntilIdle()

    assertEquals("beta", queries.last())
  }
}
