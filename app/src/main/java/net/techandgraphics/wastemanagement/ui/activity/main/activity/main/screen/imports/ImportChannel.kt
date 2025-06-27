package net.techandgraphics.wastemanagement.ui.activity.main.activity.main.screen.imports

sealed interface ImportChannel {
  data object Error : ImportChannel
  data object Success : ImportChannel
  data object Idle : ImportChannel
  data object Done : ImportChannel
}
