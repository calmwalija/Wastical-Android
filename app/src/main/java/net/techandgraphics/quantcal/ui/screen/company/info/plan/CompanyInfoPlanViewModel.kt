package net.techandgraphics.quantcal.ui.screen.company.info.plan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import net.techandgraphics.quantcal.data.local.database.AppDatabase
import net.techandgraphics.quantcal.domain.toCompanyUiModel
import net.techandgraphics.quantcal.domain.toPaymentPlanUiModel
import javax.inject.Inject

@HiltViewModel
class CompanyInfoPlanViewModel @Inject constructor(
  private val database: AppDatabase,
) : ViewModel() {

  private val _state = MutableStateFlow<CompanyInfoPlanState>(CompanyInfoPlanState.Loading)

  val state = _state
    .onStart {
      viewModelScope.launch { launch { onLoad() } }
    }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000L),
      initialValue = CompanyInfoPlanState.Loading,
    )

  private suspend fun onLoad() {
    val company = database.companyDao.query().first().toCompanyUiModel()
    val plans = database.paymentPlanDao.query().map { it.toPaymentPlanUiModel() }
    _state.value = CompanyInfoPlanState.Success(company = company, plans = plans)
  }

  fun onEvent(event: CompanyInfoPlanEvent) {
    when (event) {
      else -> TODO("Handle actions")
    }
  }
}
