package net.techandgraphics.quantcal.ui.screen.company.client.profile

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.every
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import net.techandgraphics.BaseUnitTest
import net.techandgraphics.quantcal.data.local.database.toAccountEntity
import net.techandgraphics.quantcal.data.local.database.toCompanyEntity
import net.techandgraphics.quantcal.data.local.database.toCompanyLocationWithDemographicEntity
import net.techandgraphics.quantcal.data.local.database.toPaymentEntity
import net.techandgraphics.quantcal.data.local.database.toPaymentRequestEntity
import net.techandgraphics.quantcal.ui.screen.account4Preview
import net.techandgraphics.quantcal.ui.screen.company4Preview
import net.techandgraphics.quantcal.ui.screen.companyLocationWithDemographic4Preview
import net.techandgraphics.quantcal.ui.screen.payment4Preview
import net.techandgraphics.quantcal.ui.screen.paymentRequest4Preview
import org.junit.Test
import kotlin.test.assertTrue

class CompanyClientProfileViewModelTest : BaseUnitTest() {

  @Test
  fun `test if initial state is Loading`() = runTest {
    val viewModel = CompanyClientProfileViewModel(mockDatabase)
    viewModel.state.test {
      assert(awaitItem() is CompanyClientProfileState.Loading)
      cancelAndIgnoreRemainingEvents()
    }
  }

  @Test
  fun `test if onLoad sets all the fields as expected`() = runTest {
    val viewModel = CompanyClientProfileViewModel(mockDatabase)
    coEvery { mockDatabase.companyDao.query() } returns listOf(company4Preview.toCompanyEntity())
    coEvery { mockDatabase.accountDao.get(1) } returns account4Preview.toAccountEntity()
    every { mockDatabase.paymentRequestDao.qByAccountId(1) } returns
      flowOf(listOf(paymentRequest4Preview.toPaymentRequestEntity()))
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
