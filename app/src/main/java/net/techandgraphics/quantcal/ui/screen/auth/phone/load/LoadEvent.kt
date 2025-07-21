package net.techandgraphics.quantcal.ui.screen.auth.phone.load

sealed interface LoadEvent {
  data object Load : LoadEvent
}
