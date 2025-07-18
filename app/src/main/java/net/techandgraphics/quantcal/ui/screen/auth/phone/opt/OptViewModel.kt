package net.techandgraphics.quantcal.ui.screen.auth.phone.opt

import android.app.Application
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class OptViewModel @Inject constructor(
  private val application: Application,
) : ViewModel() {

  private val _state = MutableStateFlow<OptState>(OptState.Loading)
  val state = _state.asStateFlow()

  private fun onLoad(event: OptEvent.Load) {
    _state.value = OptState.Success(phone = event.phone)
  }

  fun onEvent(event: OptEvent) {
    when (event) {
      is OptEvent.Load -> onLoad(event)
      else -> Unit
    }
  }
}
