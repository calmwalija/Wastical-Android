package net.techandgraphics.wastemanagement.ui.activity.main.activity.main.screen.imports

sealed interface ImportEvent {
  data class Import(val jsonString: String) : ImportEvent
}
