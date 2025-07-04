package net.techandgraphics.quantcal.ui.screen.company

sealed interface AccountInfoEvent {
  data class Location(val id: Long) : AccountInfoEvent
  data class Phone(val contact: String) : AccountInfoEvent
}
