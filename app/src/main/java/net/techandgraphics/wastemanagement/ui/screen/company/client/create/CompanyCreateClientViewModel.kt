package net.techandgraphics.wastemanagement.ui.screen.company.client.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.techandgraphics.wastemanagement.data.local.database.AppDatabase
import net.techandgraphics.wastemanagement.data.local.database.account.AccountTitle
import net.techandgraphics.wastemanagement.data.local.database.toAccountContactEntity
import net.techandgraphics.wastemanagement.data.local.database.toAccountEntity
import net.techandgraphics.wastemanagement.data.local.database.toAccountPaymentPlanEntity
import net.techandgraphics.wastemanagement.data.remote.account.AccountApi
import net.techandgraphics.wastemanagement.data.remote.account.AccountRequest
import net.techandgraphics.wastemanagement.data.remote.mapApiError
import net.techandgraphics.wastemanagement.domain.model.demographic.DemographicStreetUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentPlanUiModel
import net.techandgraphics.wastemanagement.ui.screen.company.client.create.CompanyCreateClientEvent.AppState
import net.techandgraphics.wastemanagement.ui.screen.company.client.create.CompanyCreateClientEvent.Create
import javax.inject.Inject

@HiltViewModel
class CompanyCreateClientViewModel @Inject constructor(
  private val database: AppDatabase,
  private val api: AccountApi,
) : ViewModel() {

  private val _state = MutableStateFlow(CreateAccountState())

  private val _channel = Channel<CompanyCreateClientChannel>()
  private fun account() = _state.value.account
  val channel = _channel.receiveAsFlow()
  val state = _state.asStateFlow()

  private fun onAppState(event: AppState) {
    _state.update { it.copy(appState = event.state) }
//    setInitialValues(event.state)
  }

  private fun onSubmit() = viewModelScope.launch {
    with(state.value) {
      var contacts: MutableList<String> = mutableListOf()
      contacts.add(account.contact)
      if (account.altContact.isNotEmpty()) contacts.add(account.altContact)

      val tCSId =
        database
          .companyBinCollectionDao
          .query()
          .first()
//          .getByStreetId(account.street!!.id)
          .id

      AccountRequest(
        title = account.title,
        companyId = appState.companies.random().id,
        paymentPlanId = account.paymentPlan!!.id,
        tCSId = tCSId,
        lastname = account.lastname.trim(),
        firstname = account.firstname.trim(),
        contacts = contacts,
      ).also { request ->

        println(Gson().toJson(request))

        runCatching { api.create(request) }
          .onFailure { _channel.send(CompanyCreateClientChannel.Error(mapApiError(it))) }
          .onSuccess { response ->
            database.withTransaction {
              with(database) {
                response.accounts
                  ?.map { it.toAccountEntity() }?.also { accountDao.insert(it) }
                response.accountContacts
                  ?.map { it.toAccountContactEntity() }
                  ?.also { accountContactDao.insert(it) }
                response.accountPaymentPlans
                  ?.map { it.toAccountPaymentPlanEntity() }
                  ?.also { accountPaymentPlanDao.insert(it) }
              }
              _channel.send(CompanyCreateClientChannel.Success)
            }
          }
      }
    }
  }

  fun onEvent(event: CompanyCreateClientEvent) {
    when (event) {
      is Create.Input.Info -> onAccountCreateInfo(event)
      is AppState -> onAppState(event)
      Create.Button.Submit -> onSubmit()
    }
  }

//  private fun setInitialValues(appState: MainActivityState) {
//    appState.companies.firstOrNull()?.let { company ->
//      database.companyBinCollectionDao.flowOfTxn(company.id)
//        .map { it.map { it.streetEntity.toStreetUiModel() } }
//        .onEach { streets ->
//          _state.update { it.copy(account = account().copy(companyStreets = streets)) }
//
//          streets
//            .firstOrNull()
//            ?.let { street ->
//              _state.update { it.copy(account = account().copy(street = street)) }
//            }
//        }
//        .launchIn(viewModelScope)
//    }
//
//    appState.paymentPlans
//      .firstOrNull()
//      ?.let { plan ->
//        _state.update { it.copy(account = account().copy(paymentPlan = plan)) }
//      }
//  }

  private fun onAccountCreateInfo(event: Create.Input.Info) {
    when (event.type) {
      Create.Input.Type.FirstName ->
        _state.update {
          it.copy(account = account().copy(firstname = "${event.value}"))
        }

      Create.Input.Type.Lastname ->
        _state.update {
          it.copy(account = account().copy(lastname = "${event.value}"))
        }

      Create.Input.Type.Contact ->
        _state.update {
          it.copy(account = account().copy(contact = "${event.value}"))
        }

      Create.Input.Type.AltContact ->
        _state.update {
          it.copy(account = account().copy(altContact = "${event.value}"))
        }

      Create.Input.Type.Title ->
        _state.update {
          it.copy(
            account = account().copy(title = AccountTitle.valueOf("${event.value}")),
          )
        }

      Create.Input.Type.Street -> {
        _state.update {
          it.copy(
            account = account().copy(street = event.value as DemographicStreetUiModel),
          )
        }
      }

      Create.Input.Type.Plan ->
        _state.update {
          it.copy(
            account = account().copy(paymentPlan = event.value as PaymentPlanUiModel),
          )
        }
    }
  }
}
