package net.techandgraphics.wastemanagement.ui.screen.auth.phone.opt

sealed interface OptState {
  data object Loading : OptState
  data class Success(val phone: String) : OptState
}
