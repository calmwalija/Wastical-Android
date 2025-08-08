package net.techandgraphics.wastical.ui.screen.company.client.create

import app.cash.turbine.test
import io.mockk.coEvery
import kotlinx.coroutines.test.runTest
import net.techandgraphics.wastical.BaseUnitTest
import net.techandgraphics.wastical.data.local.database.account.AccountTitle
import net.techandgraphics.wastical.data.local.database.toCompanyEntity
import net.techandgraphics.wastical.data.local.database.toCompanyLocationWithDemographicEntity
import net.techandgraphics.wastical.data.local.database.toPaymentPlanEntity
import net.techandgraphics.wastical.ui.screen.accountInfo4Preview
import net.techandgraphics.wastical.ui.screen.company.client.create.CompanyCreateClientEvent.Input
import net.techandgraphics.wastical.ui.screen.company4Preview
import net.techandgraphics.wastical.ui.screen.companyLocationWithDemographic4Preview
import net.techandgraphics.wastical.ui.screen.paymentPlan4Preview
import org.junit.Test
import kotlin.test.assertTrue

class CompanyCreateClientViewModelTest : BaseUnitTest() {

  @Test fun `test if onLoad sets all the fields as expected`() = runTest {
    val viewModel = CompanyCreateClientViewModel(mockDatabase, mockApplication)
    viewModel.onEvent(CompanyCreateClientEvent.Load(1))

    coEvery { mockDatabase.companyDao.query() } returns listOf(company4Preview.toCompanyEntity())
    coEvery { mockDatabase.paymentPlanDao.query() } returns listOf(paymentPlan4Preview.toPaymentPlanEntity())
    coEvery { mockDatabase.companyLocationDao.getById(1) } returns
      (companyLocationWithDemographic4Preview.toCompanyLocationWithDemographicEntity())
    viewModel.state.test {
      assertTrue { awaitItem() is CompanyCreateClientState.Loading }
      val successState = awaitItem()
      assertTrue { successState is CompanyCreateClientState.Success }
      val state = successState as CompanyCreateClientState.Success
      assertTrue { state.company.name == company4Preview.name }
      cancelAndIgnoreRemainingEvents()
    }
  }

  @Test fun `test if onInputAccountInfo sets data as expected`() = runTest {
    val viewModel = CompanyCreateClientViewModel(mockDatabase, mockApplication)
    viewModel.onEvent(CompanyCreateClientEvent.Load(1))

    coEvery { mockDatabase.companyDao.query() } returns listOf(company4Preview.toCompanyEntity())
    coEvery { mockDatabase.accountDao.qByUname("999003244") } returns listOf(accountInfo4Preview)
    coEvery { mockDatabase.paymentPlanDao.query() } returns listOf(paymentPlan4Preview.toPaymentPlanEntity())
    coEvery { mockDatabase.companyLocationDao.getById(1) } returns
      companyLocationWithDemographic4Preview.toCompanyLocationWithDemographicEntity()
    coEvery { mockDatabase.accountContactDao.getByContact("999003244") } returns listOf()

    viewModel.state.test {
      assertTrue { awaitItem() is CompanyCreateClientState.Loading }
      val successState = awaitItem()
      assertTrue { successState is CompanyCreateClientState.Success }

      viewModel.onEvent(Input.Info(AccountTitle.MR.name, Input.Type.Title))
      var state = successState as CompanyCreateClientState.Success
      assertTrue { state.title == AccountTitle.MR }

      viewModel.onEvent(Input.Info("John", Input.Type.FirstName))
      state = awaitItem() as CompanyCreateClientState.Success
      assertTrue { state.firstname == "John" }

      viewModel.onEvent(Input.Info("Doe", Input.Type.Lastname))
      state = awaitItem() as CompanyCreateClientState.Success
      assertTrue { state.lastname == "Doe" }

      viewModel.onEvent(Input.Info("999003244", Input.Type.Contact))
      state = awaitItem() as CompanyCreateClientState.Success
      assertTrue { state.contact == "999003244" }

      viewModel.onEvent(Input.Info("999003244", Input.Type.AltContact))
      state = awaitItem() as CompanyCreateClientState.Success
      assertTrue { state.altContact == "999003244" }

      viewModel.onEvent(Input.Info(3L, Input.Type.Plan))
      state = awaitItem() as CompanyCreateClientState.Success
      assertTrue { state.planId == 3L }
      cancelAndIgnoreRemainingEvents()
    }
  }
}
