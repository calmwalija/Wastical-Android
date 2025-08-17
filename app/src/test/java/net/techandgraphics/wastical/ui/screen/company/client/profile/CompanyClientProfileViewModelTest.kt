package net.techandgraphics.wastical.ui.screen.company.client.profile

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.every
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import net.techandgraphics.wastical.BaseUnitTest
import net.techandgraphics.wastical.data.local.database.toAccountEntity
import net.techandgraphics.wastical.data.local.database.toCompanyEntity
import net.techandgraphics.wastical.data.local.database.toCompanyLocationWithDemographicEntity
import net.techandgraphics.wastical.data.local.database.toPaymentEntity
import net.techandgraphics.wastical.data.local.database.toPaymentRequestWithAccountEntity
import net.techandgraphics.wastical.ui.screen.account4Preview
import net.techandgraphics.wastical.ui.screen.accountRequest4Preview
import net.techandgraphics.wastical.ui.screen.company4Preview
import net.techandgraphics.wastical.ui.screen.companyLocationWithDemographic4Preview
import net.techandgraphics.wastical.ui.screen.payment4Preview
import net.techandgraphics.wastical.ui.screen.paymentRequestWithAccount4Preview
import org.junit.Test
import kotlin.test.assertTrue

class CompanyClientProfileViewModelTest : BaseUnitTest() {

  @Test
  fun `test if initial state is Loading`() = runTest {
    val viewModel = CompanyClientProfileViewModel(
      mockDatabase,
      mockApplication,
      accountManager,
      authenticatorHelper,
    )
    viewModel.state.test {
      assert(awaitItem() is CompanyClientProfileState.Loading)
      cancelAndIgnoreRemainingEvents()
    }
  }

  @Test
  fun `test if onLoad sets all the fields as expected`() = runTest {
    val viewModel = CompanyClientProfileViewModel(
      mockDatabase,
      mockApplication,
      accountManager,
      authenticatorHelper,
    )
    viewModel.onEvent(CompanyClientProfileEvent.Load(1))
    coEvery { mockDatabase.companyDao.query() } returns listOf(company4Preview.toCompanyEntity())
    coEvery { mockDatabase.accountDao.get(1) } returns account4Preview.toAccountEntity()
    coEvery { mockDatabase.accountRequestDao.query() } returns listOf(accountRequest4Preview)
    every { mockDatabase.paymentRequestDao.qWithAccountByAccountId(1) } returns
      flowOf(listOf(paymentRequestWithAccount4Preview.toPaymentRequestWithAccountEntity()))
    every { mockDatabase.paymentDao.flowOfByAccountId(1) } returns
      flowOf(listOf(payment4Preview.toPaymentEntity()))
    coEvery { mockDatabase.companyLocationDao.getWithDemographic(account4Preview.companyLocationId) } returns
      companyLocationWithDemographic4Preview.toCompanyLocationWithDemographicEntity()
    viewModel.state.test {
      assert(awaitItem() is CompanyClientProfileState.Loading)
      viewModel.onEvent(CompanyClientProfileEvent.Load(1))
      val successState = awaitItem()
      assert(successState is CompanyClientProfileState.Success)
      val state = successState as CompanyClientProfileState.Success
      assertTrue { state.company.name == company4Preview.name }
      assertTrue { state.account.id == account4Preview.id }
      assertTrue { state.pending.isNotEmpty() }
      cancelAndIgnoreRemainingEvents()
    }
  }
}
