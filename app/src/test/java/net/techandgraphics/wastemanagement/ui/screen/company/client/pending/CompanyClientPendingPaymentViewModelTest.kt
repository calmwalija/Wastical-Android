package net.techandgraphics.wastemanagement.ui.screen.company.client.pending

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.every
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import net.techandgraphics.BaseUnitTest
import net.techandgraphics.wastemanagement.data.local.database.toAccountEntity
import net.techandgraphics.wastemanagement.data.local.database.toCompanyEntity
import net.techandgraphics.wastemanagement.data.local.database.toCompanyLocationWithDemographicEntity
import net.techandgraphics.wastemanagement.data.local.database.toPaymentRequestWithAccountEntity
import net.techandgraphics.wastemanagement.ui.screen.account4Preview
import net.techandgraphics.wastemanagement.ui.screen.company4Preview
import net.techandgraphics.wastemanagement.ui.screen.companyLocationWithDemographic4Preview
import net.techandgraphics.wastemanagement.ui.screen.paymentRequestWithAccount4Preview
import org.junit.Test
import kotlin.test.assertTrue

class CompanyClientPendingPaymentViewModelTest : BaseUnitTest() {

  @Test
  fun `test if initial state is Loading`() = runTest {
    val viewModel = CompanyClientPendingPaymentViewModel(mockDatabase)
    viewModel.state.test {
      val initialState = awaitItem()
      assertTrue { initialState is CompanyClientPendingPaymentState.Loading }
      cancelAndIgnoreRemainingEvents()
    }
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun `test if onLoad sets all the fields as expected`() = runTest {
    every { mockDatabase.paymentRequestDao.getWithAccountByAccountId(1) } returns flowOf(
      listOf(paymentRequestWithAccount4Preview.toPaymentRequestWithAccountEntity()),
    )
    coEvery { mockDatabase.companyDao.query() } returns listOf(company4Preview.toCompanyEntity())
    coEvery { mockDatabase.accountDao.get(1) } returns account4Preview.toAccountEntity()

    coEvery { mockDatabase.companyLocationDao.getWithDemographic(account4Preview.companyLocationId) } returns
      companyLocationWithDemographic4Preview.toCompanyLocationWithDemographicEntity()

    val viewModel = CompanyClientPendingPaymentViewModel(mockDatabase)

    viewModel.state.test {
      assertTrue(awaitItem() is CompanyClientPendingPaymentState.Loading)
      viewModel.onEvent(CompanyClientPendingPaymentEvent.Load(1))
      val success = awaitItem()
      assertTrue(success is CompanyClientPendingPaymentState.Success)
      cancelAndIgnoreRemainingEvents()
    }
  }
}
