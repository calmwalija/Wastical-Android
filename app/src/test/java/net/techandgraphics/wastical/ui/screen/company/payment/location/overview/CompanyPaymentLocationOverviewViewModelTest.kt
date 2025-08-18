package net.techandgraphics.wastical.ui.screen.company.payment.location.overview

import app.cash.turbine.test
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import net.techandgraphics.wastical.BaseUnitTest
import net.techandgraphics.wastical.data.local.database.dashboard.payment.AccountSortOrder
import net.techandgraphics.wastical.ui.screen.company.location.overview.CompanyPaymentLocationOverviewEvent
import net.techandgraphics.wastical.ui.screen.company.location.overview.CompanyPaymentLocationOverviewState
import net.techandgraphics.wastical.ui.screen.company.location.overview.CompanyPaymentLocationOverviewViewModel
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CompanyPaymentLocationOverviewViewModelTest : BaseUnitTest() {

  @Before
  fun setUp() {
    every { mockDatabase.companyLocationDao } returns mockk()
    every { mockDatabase.demographicStreetDao } returns mockk()
    every { mockDatabase.paymentIndicatorDao } returns mockk()
    every { mockDatabase.demographicAreaDao } returns mockk()
    every { mockDatabase.companyDao } returns mockk()
  }

  @Test
  fun `initial state should be Loading`() = runTest {
    val viewModel = CompanyPaymentLocationOverviewViewModel(
      mockDatabase,
      preferences,
      authenticatorHelper = authenticatorHelper,
      accountManager = accountManager,
      application = mockApplication,
    )
    viewModel.state.test {
      val initialState = awaitItem()
      assertTrue(initialState is CompanyPaymentLocationOverviewState.Loading)
      cancelAndIgnoreRemainingEvents()
    }
  }

  @Test
  fun `onSortBy when not in Success state should do nothing`() = runTest {
    val sortOrder = AccountSortOrder.Lastname
    val viewModel = CompanyPaymentLocationOverviewViewModel(
      mockDatabase,
      preferences,
      authenticatorHelper = authenticatorHelper,
      accountManager = accountManager,
      application = mockApplication,
    )
    viewModel.onEvent(CompanyPaymentLocationOverviewEvent.Button.SortBy(sortOrder))
    viewModel.state.test {
      val state = awaitItem()
      assertTrue(state is CompanyPaymentLocationOverviewState.Loading)
      expectNoEvents()
      cancelAndIgnoreRemainingEvents()
    }
  }
}
