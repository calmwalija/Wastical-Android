package net.techandgraphics.wastemanagement.ui.screen.company.client.manage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CompanyManageClientViewModel @Inject constructor() : ViewModel() {

  private val _state = MutableStateFlow(CompanyManageClientState())
  val state = _state
    .onStart {
    }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000L),
      initialValue = CompanyManageClientState(),
    )

  fun onEvent(event: CompanyManageClientEvent) {
    when (event) {
      else -> TODO("Handle actions")
    }
  }
}
