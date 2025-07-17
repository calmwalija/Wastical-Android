package net.techandgraphics.quantcal.ui.screen.auth.phone.verify

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.techandgraphics.quantcal.account.AuthenticatorHelper
import net.techandgraphics.quantcal.data.local.database.AppDatabase
import net.techandgraphics.quantcal.data.local.database.account.session.AccountSessionRepository
import net.techandgraphics.quantcal.data.local.database.toAccountEntity
import net.techandgraphics.quantcal.data.remote.account.AccountApi
import net.techandgraphics.quantcal.data.remote.mapApiError
import net.techandgraphics.quantcal.domain.toAccountUiModel
import net.techandgraphics.quantcal.ui.screen.auth.phone.verify.VerifyPhoneChannel.Response
import net.techandgraphics.quantcal.ui.screen.auth.phone.verify.VerifyPhoneEvent.Input
import javax.inject.Inject

@HiltViewModel
class VerifyPhoneViewModel @Inject constructor(
  private val accountApi: AccountApi,
  private val authenticatorHelper: AuthenticatorHelper,
  private val database: AppDatabase,
  private val accountSessionRepository: AccountSessionRepository,
) : ViewModel() {

  private val _channel = Channel<VerifyPhoneChannel>()
  private val _state = MutableStateFlow(VerifyPhoneState())
  val channel = _channel.receiveAsFlow()
  val state = _state.asStateFlow()
  private var job: Job? = null

  private fun onVerify() {
    job = viewModelScope.launch {
      job?.cancel()
      val contact = state.value.contact.takeLast(9)
      runCatching { accountApi.verify(contact) }
        .onSuccess { account ->
          val newAccount = account.toAccountEntity().toAccountUiModel()
          authenticatorHelper.addAccountPlain(newAccount)
          try {
            database.withTransaction {
              database.clearAllTables()
              accountSessionRepository.purseData(
                accountSessionRepository.fetch(account.id),
              ) { _, _ -> }
            }
            _channel.send(Response.Success(newAccount))
          } catch (e: Exception) {
            _channel.send(Response.Failure(mapApiError(e)))
          }
        }
        .onFailure { _channel.send(Response.Failure(mapApiError(it))) }
    }
  }

  private fun onInputPhone(event: Input.Phone) {
    _state.update { it.copy(contact = event.value) }
  }

  fun onEvent(event: VerifyPhoneEvent) {
    when (event) {
      is Input.Phone -> onInputPhone(event)
      VerifyPhoneEvent.Button.Verify -> onVerify()
      else -> Unit
    }
  }
}
