package net.techandgraphics.wastemanagement.ui.screen.company.client.location

import app.cash.turbine.test
import io.mockk.coEvery
import kotlinx.coroutines.test.runTest
import net.techandgraphics.BaseUnitTest
import net.techandgraphics.wastemanagement.data.local.database.toAccountEntity
import net.techandgraphics.wastemanagement.data.local.database.toCompanyEntity
import net.techandgraphics.wastemanagement.data.local.database.toCompanyLocationEntity
import net.techandgraphics.wastemanagement.data.local.database.toCompanyLocationWithDemographicEntity
import net.techandgraphics.wastemanagement.data.local.database.toDemographicAreaEntity
import net.techandgraphics.wastemanagement.data.local.database.toDemographicStreetEntity
import net.techandgraphics.wastemanagement.ui.screen.account4Preview
import net.techandgraphics.wastemanagement.ui.screen.company4Preview
import net.techandgraphics.wastemanagement.ui.screen.companyLocation4Preview
import net.techandgraphics.wastemanagement.ui.screen.companyLocationWithDemographic4Preview
import net.techandgraphics.wastemanagement.ui.screen.demographicArea4Preview
import net.techandgraphics.wastemanagement.ui.screen.demographicStreet4Preview
import org.junit.Test
import kotlin.test.assertTrue

class CompanyClientLocationViewModelTest : BaseUnitTest() {

  @Test fun `test if onLoad sets all the fields as expected`() = runTest {
    coEvery { mockDatabase.companyDao.query() } returns listOf(company4Preview.toCompanyEntity())

    coEvery { mockDatabase.accountDao.get(1) } returns account4Preview.toAccountEntity()

    coEvery { mockDatabase.demographicAreaDao.get(1) } returns
      demographicArea4Preview.toDemographicAreaEntity()

    coEvery { mockDatabase.demographicStreetDao.get(1) } returns
      demographicStreet4Preview.toDemographicStreetEntity()

    coEvery { mockDatabase.companyLocationDao.qWithDemographic() } returns
      listOf(companyLocationWithDemographic4Preview.toCompanyLocationWithDemographicEntity())

    coEvery { mockDatabase.companyLocationDao.get(account4Preview.companyLocationId) } returns
      companyLocation4Preview.toCompanyLocationEntity()

    coEvery { mockDatabase.companyLocationDao.getWithDemographic(account4Preview.companyLocationId) } returns
      companyLocationWithDemographic4Preview.toCompanyLocationWithDemographicEntity()

    val viewModel = CompanyClientLocationViewModel(mockDatabase)
    viewModel.state.test {
      assertTrue { awaitItem() is CompanyClientLocationState.Loading }
      viewModel.onEvent(CompanyClientLocationEvent.Load(1))
      val successState = awaitItem()
      assertTrue { successState is CompanyClientLocationState.Success }
      val state = successState as CompanyClientLocationState.Success
      assertTrue { state.company.name == company4Preview.name }
      assertTrue { state.account.companyLocationId == account4Preview.companyLocationId }
      assertTrue { state.accountDemographicArea.id == demographicArea4Preview.id }
      assertTrue { state.accountDemographicStreet.name == demographicStreet4Preview.name }
      assertTrue { state.demographics.isNotEmpty() }
      cancelAndIgnoreRemainingEvents()
    }
  }
}
