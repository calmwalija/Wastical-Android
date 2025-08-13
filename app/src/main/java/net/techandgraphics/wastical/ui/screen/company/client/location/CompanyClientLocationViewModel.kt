package net.techandgraphics.wastical.ui.screen.company.client.location

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.techandgraphics.wastical.data.local.database.AppDatabase
import net.techandgraphics.wastical.data.local.database.toAccountEntity
import net.techandgraphics.wastical.data.remote.account.HttpOperation
import net.techandgraphics.wastical.domain.model.relations.CompanyLocationWithDemographicUiModel
import net.techandgraphics.wastical.domain.toAccountUiModel
import net.techandgraphics.wastical.domain.toCompanyLocationUiModel
import net.techandgraphics.wastical.domain.toCompanyLocationWithDemographicUiModel
import net.techandgraphics.wastical.domain.toCompanyUiModel
import net.techandgraphics.wastical.domain.toDemographicAreaUiModel
import net.techandgraphics.wastical.domain.toDemographicStreetUiModel
import net.techandgraphics.wastical.worker.company.account.scheduleCompanyAccountDemographicRequestWorker
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class CompanyClientLocationViewModel @Inject constructor(
  private val database: AppDatabase,
  private val application: Application,
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
          .toDemographicStreetUiModel()
      val accountDemographicArea =
        database.demographicAreaDao.get(accountLocation.demographicAreaId)
          .toDemographicAreaUiModel()
      val demographic = CompanyLocationWithDemographicUiModel(
        accountLocation,
        accountDemographicArea,
        accountDemographicStreet,
      )
      val companyLocation =
        database.companyLocationDao.get(account.companyLocationId).toCompanyLocationUiModel()

      _state.value = CompanyClientLocationState.Success(
        company = company,
        account = account,
        demographics = demographics,
        demographic = demographic,
        accountDemographicStreet = accountDemographicStreet,
        accountDemographicArea = accountDemographicArea,
        companyLocation = companyLocation,
      )
    }

  private fun onButtonChange(event: CompanyClientLocationEvent.Button.Change) =
    viewModelScope.launch {
      if (_state.value is CompanyClientLocationState.Success) {
        val state = (_state.value as CompanyClientLocationState.Success)
        val accountPlan = database.accountPaymentPlanDao.getByAccountId(state.account.id)
        val newCompanyLocation =
          database.companyLocationDao.getByStreetId(event.demographicStreet.id)
        val accountRequest = state.account
          .copy(updatedAt = ZonedDateTime.now().toEpochSecond())
          .toAccountEntity()
          .toAccountEntity(accountPlan.paymentPlanId)
          .copy(
            companyLocationId = newCompanyLocation.id,
            httpOperation = HttpOperation.Demographic.name,
          )
        runCatching { database.accountRequestDao.insert(accountRequest) }
          .onSuccess { application.scheduleCompanyAccountDemographicRequestWorker() }
          .onFailure { println(it) }
      }
    }

  private fun onInputSearch(event: CompanyClientLocationEvent.Input.Search) =
    viewModelScope.launch {
      if (_state.value is CompanyClientLocationState.Success) {
        val state = (_state.value as CompanyClientLocationState.Success)
        _state.value = state.copy(query = event.query)
      }
    }

  private fun onButtonClear() = viewModelScope.launch {
    if (_state.value is CompanyClientLocationState.Success) {
      val state = (_state.value as CompanyClientLocationState.Success)
      _state.value = state.copy(query = "")
    }
  }

  fun onEvent(event: CompanyClientLocationEvent) {
    when (event) {
      is CompanyClientLocationEvent.Load -> onLoad(event)
      is CompanyClientLocationEvent.Button.Change -> onButtonChange(event)
      is CompanyClientLocationEvent.Input.Search -> onInputSearch(event)
      CompanyClientLocationEvent.Button.Clear -> onButtonClear()
      else -> Unit
    }
  }
}
