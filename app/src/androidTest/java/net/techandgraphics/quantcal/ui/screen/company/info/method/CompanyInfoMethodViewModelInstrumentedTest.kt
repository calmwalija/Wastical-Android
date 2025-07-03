package net.techandgraphics.quantcal.ui.screen.company.info.method

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import net.techandgraphics.quantcal.BaseTest
import net.techandgraphics.quantcal.data.local.database.toCompanyEntity
import net.techandgraphics.quantcal.domain.toCompanyUiModel
import net.techandgraphics.quantcal.ui.screen.company4Preview
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class CompanyInfoMethodViewModelInstrumentedTest : BaseTest() {

  @Test
  fun initialStateIsLoading() = runTest {
    val viewModel = CompanyInfoMethodViewModel(database)
    val initialState = viewModel.state.first()
    assertTrue(initialState is CompanyInfoMethodState.Loading)
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun loadCompanyAndPaymentMethods() = runBlocking {
    val company = company4Preview.toCompanyEntity()
    val viewModel = CompanyInfoMethodViewModel(database)
    viewModel.state.test {
      val loadingState = awaitItem()
      assertTrue(loadingState is CompanyInfoMethodState.Loading)
      val successState = awaitItem()
      assertTrue(successState is CompanyInfoMethodState.Success)
      assertEquals(
        company.toCompanyUiModel(),
        (successState as CompanyInfoMethodState.Success).company,
      )
      cancelAndIgnoreRemainingEvents()
    }
  }

  @Test
  fun loadEmptyCompanyAndPaymentMethods() = runTest {
    val viewModel = CompanyInfoMethodViewModel(database)

    viewModel.state.test {
      val loadingState = awaitItem()
      assertTrue(loadingState is CompanyInfoMethodState.Loading)
      cancelAndIgnoreRemainingEvents()
    }
  }
}
