package net.techandgraphics.wastemanagement.ui.screen.company.client.location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.techandgraphics.wastemanagement.data.local.database.AppDatabase
import net.techandgraphics.wastemanagement.domain.model.relations.CompanyLocationWithDemographicUiModel
import net.techandgraphics.wastemanagement.domain.toAccountUiModel
import net.techandgraphics.wastemanagement.domain.toAreaUiModel
import net.techandgraphics.wastemanagement.domain.toCompanyLocationUiModel
import net.techandgraphics.wastemanagement.domain.toCompanyLocationWithDemographicUiModel
import net.techandgraphics.wastemanagement.domain.toCompanyUiModel
import net.techandgraphics.wastemanagement.domain.toStreetUiModel
import javax.inject.Inject

@HiltViewModel
class CompanyClientLocationViewModel @Inject constructor(
  private val database: AppDatabase,
) : ViewModel() {

  private val _state =
    MutableStateFlow<CompanyClientLocationState>(CompanyClientLocationState.Loading)
  val state = _state.asStateFlow()

  private fun onLoad(event: CompanyClientLocationEvent.Load) =
    viewModelScope.launch {
      val account = database.accountDao.get(event.id).toAccountUiModel()
      val company = database.companyDao.query().first().toCompanyUiModel()
      val demographics = database.companyLocationDao.qWithDemographic()
        .map { it.toCompanyLocationWithDemographicUiModel() }
      val accountLocation = database.companyLocationDao.get(account.companyLocationId)
        .toCompanyLocationUiModel()
      val accountDemographicStreet =
        database.demographicStreetDao.get(accountLocation.demographicStreetId)
          .toStreetUiModel()
      val accountDemographicArea =
        database.demographicAreaDao.get(accountLocation.demographicAreaId)
          .toAreaUiModel()
      val demographic = CompanyLocationWithDemographicUiModel(
        accountLocation,
        accountDemographicArea,
        accountDemographicStreet,
      )

      _state.value = CompanyClientLocationState.Success(
        company = company,
        account = account,
        demographics = demographics,
        demographic = demographic,
        accountDemographicStreet = accountDemographicStreet,
        accountDemographicArea = accountDemographicArea,
      )
    }

  fun onEvent(event: CompanyClientLocationEvent) {
    when (event) {
      is CompanyClientLocationEvent.Load -> onLoad(event)
      else -> Unit
    }
  }
}
