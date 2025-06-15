package net.techandgraphics.wastemanagement.ui.screen.company.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import net.techandgraphics.wastemanagement.data.local.database.AppDatabase
import net.techandgraphics.wastemanagement.domain.toCompanyContactUiModel
import net.techandgraphics.wastemanagement.domain.toCompanyUiModel
import javax.inject.Inject

@HiltViewModel
class CompanyInfoViewModel @Inject constructor(
  private val database: AppDatabase,
) : ViewModel() {

  private val _state = MutableStateFlow<CompanyInfoState>(CompanyInfoState.Loading)
  val state = _state
    .onStart {
      viewModelScope.launch { launch { onLoad() } }
    }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000L),
      initialValue = CompanyInfoState.Loading,
    )

  private suspend fun onLoad() {
    val company = database.companyDao.query().first().toCompanyUiModel()
    val contacts = database.companyContactDao.query().map { it.toCompanyContactUiModel() }
    _state.value = CompanyInfoState.Success(company = company, contacts = contacts)
  }

  fun onEvent(event: CompanyInfoEvent) {
    when (event) {
      else -> TODO("Handle actions")
    }
  }
}
