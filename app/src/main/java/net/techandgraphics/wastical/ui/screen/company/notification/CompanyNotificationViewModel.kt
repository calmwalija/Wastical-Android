package net.techandgraphics.wastical.ui.screen.company.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import net.techandgraphics.wastical.data.local.database.AppDatabase
import net.techandgraphics.wastical.domain.toCompanyUiModel
import net.techandgraphics.wastical.domain.toNotificationUiModel
import javax.inject.Inject

@HiltViewModel
class CompanyNotificationViewModel @Inject constructor(
  private val database: AppDatabase,
) : ViewModel() {

  private val _state = MutableStateFlow<CompanyNotificationState>(CompanyNotificationState.Loading)
  val state = _state.asStateFlow()

  init {
    onEvent(CompanyNotificationEvent.Load)
  }

  private fun onLoad() = viewModelScope.launch {
    database.notificationDao.flowOf()
      .map { p0 -> p0.map { it.toNotificationUiModel() } }
      .collectLatest { notifications ->
        val company = database.companyDao.query().first().toCompanyUiModel()
        _state.value = CompanyNotificationState.Success(
          company = company,
          notifications = notifications,
        )
      }
  }

  fun onEvent(event: CompanyNotificationEvent) {
    when (event) {
      CompanyNotificationEvent.Load -> onLoad()
      else -> Unit
    }
  }
}
