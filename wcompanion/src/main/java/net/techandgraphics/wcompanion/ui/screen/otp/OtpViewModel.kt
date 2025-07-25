package net.techandgraphics.wcompanion.ui.screen.otp

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import net.techandgraphics.wcompanion.data.local.database.QgatewayDatabase
import net.techandgraphics.wcompanion.worker.scheduleSmsWorker
import javax.inject.Inject

@HiltViewModel
class OtpViewModel @Inject constructor(
  private val database: QgatewayDatabase,
  private val application: Application,
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

  private fun onResend(event: OtpEvent.Resend) = viewModelScope.launch {
    database.optDao.update(event.otpUiModel.toOtpEntity().copy(sent = false))
    application.scheduleSmsWorker()
  }

  fun onEvent(event: OtpEvent) {
    when (event) {
      OtpEvent.Load -> onLoad()
      is OtpEvent.Resend -> onResend(event)
    }
  }
}
