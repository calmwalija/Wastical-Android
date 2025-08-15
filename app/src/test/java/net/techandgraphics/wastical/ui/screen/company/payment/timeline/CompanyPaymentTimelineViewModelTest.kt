package net.techandgraphics.wastical.ui.screen.company.payment.timeline

import androidx.paging.PagingSource
import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import net.techandgraphics.wastical.BaseUnitTest
import net.techandgraphics.wastical.data.local.database.query.PaymentWithAccountAndMethodWithGatewayQuery
import net.techandgraphics.wastical.data.local.database.toCompanyEntity
import net.techandgraphics.wastical.ui.screen.company4Preview
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CompanyPaymentTimelineViewModelTest : BaseUnitTest() {

  @Before
  fun setUp() {
    every { mockDatabase.companyDao } returns mockk()
  }

  private val fakePagingSource =
    object : PagingSource<Int, PaymentWithAccountAndMethodWithGatewayQuery>() {
      override fun getRefreshKey(state: androidx.paging.PagingState<Int, PaymentWithAccountAndMethodWithGatewayQuery>): Int? =
        null

      override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PaymentWithAccountAndMethodWithGatewayQuery> =
        LoadResult.Page(emptyList(), null, null)
    }

  @Test
  fun `initial state should be Loading`() = runTest {
    val viewModel = CompanyPaymentTimelineViewModel(mockDatabase)
    viewModel.state.test {
      val initialState = awaitItem()
      assertTrue(initialState is CompanyPaymentTimelineState.Loading)
      cancelAndIgnoreRemainingEvents()
    }
  }

  @Test
  fun `on Load should request paging from dao`() = runTest {
    coEvery { mockDatabase.companyDao.query() } returns listOf(company4Preview.toCompanyEntity())

    every {
      mockDatabase.paymentDao.flowOfPaging(
        query = any(),
        sort = any(),
        status = any(),
      )
    } returns fakePagingSource

    val viewModel = CompanyPaymentTimelineViewModel(mockDatabase)

    viewModel.onEvent(CompanyPaymentTimelineEvent.Load)
    advanceUntilIdle()

    verify(exactly = 1) {
      mockDatabase.paymentDao.flowOfPaging(
        query = "",
        sort = true,
        status = any(),
      )
    }
  }

  @Test
  fun `query input should debounce then request paging with trimmed term`() = runTest {
    coEvery { mockDatabase.companyDao.query() } returns listOf(company4Preview.toCompanyEntity())

    every {
      mockDatabase.paymentDao.flowOfPaging(
        query = any(),
        sort = any(),
        status = any(),
      )
    } returns fakePagingSource

    val viewModel = CompanyPaymentTimelineViewModel(mockDatabase)
    viewModel.onEvent(CompanyPaymentTimelineEvent.Load)
    advanceUntilIdle()

    viewModel.onEvent(CompanyPaymentTimelineEvent.Input.Query("  foo  "))
    advanceTimeBy(600)
    advanceUntilIdle()

    verify { mockDatabase.paymentDao.flowOfPaging(query = "foo", sort = true, status = any()) }
  }

  @Test
  fun `rapid successive queries should cancel previous and only invoke last`() = runTest {
    coEvery { mockDatabase.companyDao.query() } returns listOf(company4Preview.toCompanyEntity())

    val queries = mutableListOf<String>()
    every {
      mockDatabase.paymentDao.flowOfPaging(
        query = capture(queries),
        sort = any(),
        status = any(),
      )
    } returns fakePagingSource

    val viewModel = CompanyPaymentTimelineViewModel(mockDatabase)
    viewModel.onEvent(CompanyPaymentTimelineEvent.Load)
    advanceUntilIdle()

    viewModel.onEvent(CompanyPaymentTimelineEvent.Input.Query("alpha"))
    advanceTimeBy(200)
    viewModel.onEvent(CompanyPaymentTimelineEvent.Input.Query("beta"))

    advanceTimeBy(600)
    advanceUntilIdle()

    assertEquals("beta", queries.last())
  }
}
