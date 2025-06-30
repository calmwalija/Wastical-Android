package net.techandgraphics.wastemanagement.ui.screen.client.home

import net.techandgraphics.wastemanagement.ui.activity.MainActivityState

data class ClientHomeState(
  val searchQuery: String = "",
  val state: MainActivityState = MainActivityState(),
)
