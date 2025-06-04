package net.techandgraphics.wastemanagement.ui.screen.company.client.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import net.techandgraphics.wastemanagement.data.local.database.AppDatabase
import net.techandgraphics.wastemanagement.domain.model.account.AccountUiModel
import net.techandgraphics.wastemanagement.domain.toAccountUiModel
import net.techandgraphics.wastemanagement.domain.toPaymentUiModel
import javax.inject.Inject

@HiltViewModel
class CompanyClientHistoryViewModel @Inject constructor(
  private val database: AppDatabase,
  private val imageLoader: ImageLoader,
) : ViewModel() {

  private val _state =
    MutableStateFlow<CompanyClientHistoryState>(CompanyClientHistoryState.Loading)
  val state = _state.asStateFlow()

  private fun getState() = (_state.value as CompanyClientHistoryState.Success)

  private fun onLoad(event: CompanyClientHistoryEvent.Load) =
    viewModelScope.launch {
      _state.value = CompanyClientHistoryState.Loading
      val account = database.accountDao.get(event.id).toAccountUiModel()
      _state.value = CompanyClientHistoryState.Success(account = account, imageLoader = imageLoader)
      launch { getPayments(account) }
    }

  private fun getPayments(account: AccountUiModel) = viewModelScope.launch {
    database.paymentDao.flowOfByAccountId(account.id)
      .map { flowOf -> flowOf.map { it.toPaymentUiModel() } }
      .collectLatest { payments -> _state.value = getState().copy(payments = payments) }
  }

  fun onEvent(event: CompanyClientHistoryEvent) {
    when (event) {
      is CompanyClientHistoryEvent.Load -> onLoad(event)
      else -> Unit
    }
  }
}
