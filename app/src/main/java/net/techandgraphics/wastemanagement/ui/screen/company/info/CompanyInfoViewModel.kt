package net.techandgraphics.wastemanagement.ui.screen.company.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CompanyInfoViewModel @Inject constructor() : ViewModel() {

  private val _state = MutableStateFlow(CompanyInfoState())
  val state = _state
    .onStart {
    }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000L),
      initialValue = CompanyInfoState(),
    )

  fun onEvent(event: CompanyInfoEvent) {
    when (event) {
      else -> TODO("Handle actions")
    }
  }
}
