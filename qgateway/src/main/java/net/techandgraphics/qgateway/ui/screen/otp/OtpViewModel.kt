package net.techandgraphics.qgateway.ui.screen.otp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import net.techandgraphics.qgateway.data.local.database.QgatewayDatabase
import javax.inject.Inject

@HiltViewModel
class OtpViewModel @Inject constructor(
  private val database: QgatewayDatabase,
) : ViewModel() {

  private val _state = MutableStateFlow<OtpState>(OtpState.Loading)
  val state = _state.asStateFlow()

  init {
    onEvent(OtpEvent.Load)
  }

  private fun onLoad() = viewModelScope.launch {
    database.optDao.flowOf()
      .map { p0 -> p0.map { it.toAccountWithOtpUiModel() } }
      .collectLatest { accountWithOtps ->
        _state.value = OtpState.Success(accountWithOtps = accountWithOtps)
      }
  }

  fun onEvent(event: OtpEvent) {
    when (event) {
      OtpEvent.Load -> onLoad()
    }
  }
}
