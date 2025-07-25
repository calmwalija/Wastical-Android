package net.techandgraphics.wastical.ui.screen.company.info.method

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.techandgraphics.wastical.data.local.database.AppDatabase
import net.techandgraphics.wastical.domain.toCompanyUiModel
import net.techandgraphics.wastical.domain.toPaymentMethodWithGatewayAndPlanUiModel
import javax.inject.Inject

@HiltViewModel
class CompanyInfoMethodViewModel @Inject constructor(
  private val database: AppDatabase,
) : ViewModel() {

  private val _state = MutableStateFlow<CompanyInfoMethodState>(CompanyInfoMethodState.Loading)
  val state = _state.asStateFlow()

  init {
    onEvent(CompanyInfoMethodEvent.Load)
  }

  private fun onLoad() = viewModelScope.launch {
    val company = database.companyDao.query().first().toCompanyUiModel()
    val methods =
      database.paymentMethodDao.qWithGatewayAndPlan().map { it.toPaymentMethodWithGatewayAndPlanUiModel() }
    _state.value = CompanyInfoMethodState.Success(company = company, methods = methods)
  }

  fun onEvent(event: CompanyInfoMethodEvent) {
    when (event) {
      CompanyInfoMethodEvent.Load -> onLoad()
      else -> Unit
    }
  }
}
