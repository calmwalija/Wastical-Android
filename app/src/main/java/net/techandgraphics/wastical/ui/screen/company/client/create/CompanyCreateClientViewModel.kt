package net.techandgraphics.wastical.ui.screen.company.client.create

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import net.techandgraphics.wastical.data.Status
import net.techandgraphics.wastical.data.local.database.AccountRole
import net.techandgraphics.wastical.data.local.database.AppDatabase
import net.techandgraphics.wastical.data.local.database.account.AccountTitle
import net.techandgraphics.wastical.data.local.database.account.request.AccountRequestEntity
import net.techandgraphics.wastical.data.local.database.toAccountContactEntity
import net.techandgraphics.wastical.data.local.database.toAccountEntity
import net.techandgraphics.wastical.data.local.database.toAccountPaymentPlanEntity
import net.techandgraphics.wastical.data.remote.account.HttpOperation
import net.techandgraphics.wastical.data.remote.mapApiError
import net.techandgraphics.wastical.domain.toCompanyLocationWithDemographicUiModel
import net.techandgraphics.wastical.domain.toCompanyUiModel
import net.techandgraphics.wastical.domain.toPaymentPlanUiModel
import net.techandgraphics.wastical.worker.company.account.scheduleCompanyAccountRequestWorker
import java.time.ZonedDateTime
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CompanyCreateClientViewModel @Inject constructor(
  private val database: AppDatabase,
  private val application: Application,
) : ViewModel() {

  private val _state = MutableStateFlow<CompanyCreateClientState>(CompanyCreateClientState.Loading)
  private val _channel = Channel<CompanyCreateClientChannel>()
  val channel = _channel.receiveAsFlow()
  val state = _state.asStateFlow()
  private var contactAvailableJob: Job? = null

  private fun onSubmit() = viewModelScope.launch {
    if (_state.value is CompanyCreateClientState.Success) {
      val theState = (_state.value as CompanyCreateClientState.Success)
      val theId = System.currentTimeMillis()
      val timestamp = ZonedDateTime.now().toEpochSecond()
      val account = AccountRequestEntity(
        id = theId,
        title = theState.title,
        firstname = theState.firstname.trim(),
        lastname = theState.lastname.trim(),
        contact = theState.contact.ifEmpty {
          System.currentTimeMillis().toString().drop(6)
            .plus("-")
            .plus(
              UUID.randomUUID().toString()
                .plus("-")
                .plus(System.currentTimeMillis().toString().take(5)),
            ).replace("-", "")
            .take(32)
        },
        altContact = theState.altContact,
        paymentPlanId = theState.planId,
        companyId = theState.company.id,
        companyLocationId = theState.demographic.location.id,
        httpOperation = HttpOperation.Post.name,
        accountId = theId,
        role = AccountRole.Client.name,
        createdAt = timestamp,
        updatedAt = timestamp,
        status = Status.Active.name,
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
        .onSuccess {
          _channel.send(CompanyCreateClientChannel.Success(theId))
          application.scheduleCompanyAccountRequestWorker()
        }
    }
  }

  private fun onLoad(event: CompanyCreateClientEvent.Load) = viewModelScope.launch {
    val company = database.companyDao.query().first().toCompanyUiModel()
    val demographic = database.companyLocationDao.getById(event.locationId)
      .toCompanyLocationWithDemographicUiModel()
    val paymentPlans = database.paymentPlanDao.query().map { it.toPaymentPlanUiModel() }
    _state.value = CompanyCreateClientState.Success(
      company = company,
      demographic = demographic,
      paymentPlans = paymentPlans,
    )
  }

  fun onEvent(event: CompanyCreateClientEvent) {
    when (event) {
      is CompanyCreateClientEvent.Load -> onLoad(event)
      CompanyCreateClientEvent.Button.Submit -> onSubmit()
      is CompanyCreateClientEvent.Input.Info -> onInputAccountInfo(event)
      else -> Unit
    }
  }

  private fun checkIfContactAvailable(contact: String) {
    contactAvailableJob?.cancel()
    contactAvailableJob = viewModelScope.launch {
      delay(1_000)
      val ifContactAvailable = database.accountContactDao.getByContact(contact)
      if (ifContactAvailable.isNotEmpty()) {
        _channel.send(CompanyCreateClientChannel.Input.Unique.Conflict)
      } else {
        _channel.send(CompanyCreateClientChannel.Input.Unique.Ok)
      }
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

        CompanyCreateClientEvent.Input.Type.Contact -> {
          _state.value = state.copy(contact = "${event.value}")
          checkIfContactAvailable(event.value.toString())
        }

        CompanyCreateClientEvent.Input.Type.AltContact ->
          _state.value = state.copy(altContact = "${event.value}")

        CompanyCreateClientEvent.Input.Type.Title ->
          _state.value = state.copy(title = AccountTitle.valueOf("${event.value}"))

        CompanyCreateClientEvent.Input.Type.Plan ->
          _state.value = state.copy(planId = event.value.toString().toLong())
      }
    }
  }
}
