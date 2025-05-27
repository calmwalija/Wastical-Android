package net.techandgraphics.wastemanagement.ui.screen.company.payment.pay
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CompanyMakePaymentViewModel @Inject constructor() : ViewModel() {

  private val _state = MutableStateFlow(CompanyMakePaymentState())
  val state = _state
    .onStart {
    }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000L),
      initialValue = CompanyMakePaymentState(),
    )

  fun onEvent(event: CompanyMakePaymentEvent) {
    when (event) {
      else -> TODO("Handle actions")
    }
  }
}
