package net.techandgraphics.quantcal.ui.activity

sealed interface MainActivityEvent {
  data object Load : MainActivityEvent
  data class Nullify(val logout: Boolean = false) : MainActivityEvent
}
