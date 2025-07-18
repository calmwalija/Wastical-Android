package net.techandgraphics.qgateway.ui.activity

sealed interface MainActivityEvent {
  data object Load : MainActivityEvent
}
