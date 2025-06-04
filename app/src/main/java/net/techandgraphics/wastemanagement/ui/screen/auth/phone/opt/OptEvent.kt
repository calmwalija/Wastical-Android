package net.techandgraphics.wastemanagement.ui.screen.auth.phone.opt

sealed interface OptEvent {
  data class Load(val phone: String) : OptEvent
}
