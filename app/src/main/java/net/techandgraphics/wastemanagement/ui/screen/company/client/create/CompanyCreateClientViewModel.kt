package net.techandgraphics.wastemanagement.ui.screen.company.client.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import net.techandgraphics.wastemanagement.data.local.database.AppDatabase
import net.techandgraphics.wastemanagement.data.local.database.account.AccountTitle
import net.techandgraphics.wastemanagement.data.local.database.account.request.AccountRequestEntity
import net.techandgraphics.wastemanagement.data.local.database.toAccountContactEntity
import net.techandgraphics.wastemanagement.data.local.database.toAccountEntity
import net.techandgraphics.wastemanagement.data.local.database.toAccountPaymentPlanEntity
import net.techandgraphics.wastemanagement.data.remote.account.HttpOperation
import net.techandgraphics.wastemanagement.data.remote.mapApiError
import net.techandgraphics.wastemanagement.domain.toCompanyLocationWithDemographicUiModel
import net.techandgraphics.wastemanagement.domain.toCompanyUiModel
import net.techandgraphics.wastemanagement.domain.toPaymentPlanUiModel
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CompanyCreateClientViewModel @Inject constructor(
  private val database: AppDatabase,
) : ViewModel() {

  private val _state = MutableStateFlow<CompanyCreateClientState>(CompanyCreateClientState.Loading)
  private val _channel = Channel<CompanyCreateClientChannel>()
  val channel = _channel.receiveAsFlow()
  val state = _state.asStateFlow()

  private fun onSubmit() = viewModelScope.launch {
    if (_state.value is CompanyCreateClientState.Success) {
      val theState = (_state.value as CompanyCreateClientState.Success)
      val theId = System.currentTimeMillis()
      val account = AccountRequestEntity(
        id = theId,
        title = theState.title,
        firstname = theState.firstname.trim(),
        lastname = theState.lastname.trim(),
        contact = theState.contact.ifEmpty { UUID.randomUUID().toString() },
        altContact = theState.altContact,
        paymentPlanId = theState.planId,
        companyId = theState.company.id,
        companyLocationId = theState.companyLocationId,
        httpOperation = HttpOperation.Create.name,
        accountId = theId,
      )

      runCatching {
        database.withTransaction {
          val localAccount = account.toAccountEntity()
          database.accountDao.insert(localAccount)
          database.accountRequestDao.insert(account)
          database.accountContactDao.insert(localAccount.toAccountContactEntity())
          database.accountPaymentPlanDao.insert(localAccount.toAccountPaymentPlanEntity(account.paymentPlanId))
        }
      }
        .onFailure { _channel.send(CompanyCreateClientChannel.Error(mapApiError(it))) }
        .onSuccess { _channel.send(CompanyCreateClientChannel.Success(theId)) }
    }
  }

  init {
    onEvent(CompanyCreateClientEvent.Load)
  }

  private fun onLoad() = viewModelScope.launch {
    val company = database.companyDao.query().first().toCompanyUiModel()
    val demographics = database.companyLocationDao.qWithDemographic()
      .sortedBy { it.demographicStreet.name }
      .map { it.toCompanyLocationWithDemographicUiModel() }
    val paymentPlans = database.paymentPlanDao.query().map { it.toPaymentPlanUiModel() }
    _state.value = CompanyCreateClientState.Success(
      company = company,
      demographics = demographics,
      paymentPlans = paymentPlans,
      companyLocationId = demographics.firstOrNull()?.location?.id ?: -1,
    )
  }

  fun onEvent(event: CompanyCreateClientEvent) {
    when (event) {
      CompanyCreateClientEvent.Load -> onLoad()
      CompanyCreateClientEvent.Button.Submit -> onSubmit()
      is CompanyCreateClientEvent.Input.Info -> onInputAccountInfo(event)
      else -> Unit
    }
  }

  private fun onInputAccountInfo(event: CompanyCreateClientEvent.Input.Info) {
    if (_state.value is CompanyCreateClientState.Success) {
      val state = (_state.value as CompanyCreateClientState.Success)
      when (event.type) {
        CompanyCreateClientEvent.Input.Type.FirstName ->
          _state.value = state.copy(firstname = "${event.value}")

        CompanyCreateClientEvent.Input.Type.Lastname ->
          _state.value = state.copy(lastname = "${event.value}")

        CompanyCreateClientEvent.Input.Type.Contact ->
          _state.value = state.copy(contact = "${event.value}")

        CompanyCreateClientEvent.Input.Type.AltContact ->
          _state.value = state.copy(altContact = "${event.value}")

        CompanyCreateClientEvent.Input.Type.Title ->
          _state.value = state.copy(title = AccountTitle.valueOf("${event.value}"))

        CompanyCreateClientEvent.Input.Type.Location ->
          _state.value = state.copy(companyLocationId = event.value.toString().toLong())

        CompanyCreateClientEvent.Input.Type.Plan ->
          _state.value = state.copy(planId = event.value.toString().toLong())
      }
    }
  }
}
