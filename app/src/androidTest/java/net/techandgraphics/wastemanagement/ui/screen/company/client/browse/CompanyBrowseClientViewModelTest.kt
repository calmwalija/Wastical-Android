package net.techandgraphics.wastemanagement.ui.screen.company.client.browse

import net.techandgraphics.wastemanagement.BaseTest
import org.junit.Before

class CompanyBrowseClientViewModelTest : BaseTest() {

  private lateinit var viewModel: CompanyBrowseClientViewModel

  override fun populateStaticTestData() {
  }

  @Before
  fun setupViewModel() {
    viewModel = CompanyBrowseClientViewModel(database)
  }
}
