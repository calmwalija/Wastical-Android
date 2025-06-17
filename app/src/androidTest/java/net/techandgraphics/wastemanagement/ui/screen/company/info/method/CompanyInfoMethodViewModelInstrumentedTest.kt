package net.techandgraphics.wastemanagement.ui.screen.company.info.method

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import net.techandgraphics.wastemanagement.BaseTest
import net.techandgraphics.wastemanagement.data.local.database.toCompanyEntity
import net.techandgraphics.wastemanagement.domain.toCompanyUiModel
import net.techandgraphics.wastemanagement.ui.screen.company4Preview
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CompanyInfoMethodViewModelInstrumentedTest : BaseTest() {

  private lateinit var viewModel: CompanyInfoMethodViewModel

  override fun populateStaticTestData() {
    super.populateStaticTestData()
    viewModel = CompanyInfoMethodViewModel(database)
  }

  @Test
  fun initialStateIsLoading() = runTest {
    val initialState = viewModel.state.first()
    assertTrue(initialState is CompanyInfoMethodState.Loading)
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun loadCompanyAndPaymentMethods() = runTest {
    val company = company4Preview.toCompanyEntity()
    viewModel.state.test {
      val loadingState = awaitItem()
      assertTrue(loadingState is CompanyInfoMethodState.Loading)
      val successState = awaitItem() as CompanyInfoMethodState.Success
      assertEquals(company.toCompanyUiModel(), successState.company)
      cancelAndIgnoreRemainingEvents()
    }
  }

  @Test
  fun loadEmptyCompanyAndPaymentMethods() = runTest {
    viewModel.state.test {
      val loadingState = awaitItem()
      assertTrue(loadingState is CompanyInfoMethodState.Loading)
      cancelAndIgnoreRemainingEvents()
    }
  }
}
