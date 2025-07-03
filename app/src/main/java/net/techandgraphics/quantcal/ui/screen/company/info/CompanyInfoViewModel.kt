package net.techandgraphics.quantcal.ui.screen.company.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.techandgraphics.quantcal.data.local.database.AppDatabase
import net.techandgraphics.quantcal.domain.toCompanyContactUiModel
import net.techandgraphics.quantcal.domain.toCompanyUiModel
import javax.inject.Inject

@HiltViewModel
class CompanyInfoViewModel @Inject constructor(
  private val database: AppDatabase,
) : ViewModel() {

  private val _state = MutableStateFlow<CompanyInfoState>(CompanyInfoState.Loading)
  val state = _state.asStateFlow()

  init {
    onLoad()
  }

  fun onLoad() = viewModelScope.launch {
    val company = database.companyDao.query().first().toCompanyUiModel()
    val contacts = database.companyContactDao.query().map { it.toCompanyContactUiModel() }
    _state.value = CompanyInfoState.Success(company = company, contacts = contacts)
  }

  fun onEvent(event: CompanyInfoEvent) {
    when (event) {
      CompanyInfoEvent.Load -> onLoad()
      else -> TODO("Handle actions")
    }
  }
}
