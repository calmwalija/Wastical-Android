package net.techandgraphics.wastical.ui.activity

sealed interface MainActivityEvent {
  data object Load : MainActivityEvent
}
