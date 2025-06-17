package net.techandgraphics.wastemanagement.ui.screen.company.info.method

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import net.techandgraphics.wastemanagement.data.local.database.AppDatabase
import net.techandgraphics.wastemanagement.domain.toCompanyUiModel
import net.techandgraphics.wastemanagement.domain.toPaymentMethodWithGatewayUiModel
import javax.inject.Inject

@HiltViewModel
class CompanyInfoMethodViewModel @Inject constructor(
  private val database: AppDatabase,
) : ViewModel() {

  private val _state = MutableStateFlow<CompanyInfoMethodState>(CompanyInfoMethodState.Loading)
  val state = _state
    .onStart {
      viewModelScope.launch { launch { onLoad() } }
    }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000L),
      initialValue = CompanyInfoMethodState.Loading,
    )

  private suspend fun onLoad() {
    val company = database.companyDao.query().first().toCompanyUiModel()
    val methods =
      database.paymentMethodDao.qWithGateway().map { it.toPaymentMethodWithGatewayUiModel() }
    _state.value = CompanyInfoMethodState.Success(company = company, methods = methods)
  }

  fun onEvent(event: CompanyInfoMethodEvent) {
    when (event) {
      else -> Unit
    }
  }
}
