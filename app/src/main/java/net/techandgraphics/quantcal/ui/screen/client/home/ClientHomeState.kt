package net.techandgraphics.quantcal.ui.screen.client.home

import net.techandgraphics.quantcal.ui.activity.MainActivityState

data class ClientHomeState(
  val searchQuery: String = "",
  val state: MainActivityState = MainActivityState(),
)
