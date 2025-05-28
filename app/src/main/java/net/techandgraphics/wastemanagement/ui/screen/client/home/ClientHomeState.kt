package net.techandgraphics.wastemanagement.ui.screen.client.home

import net.techandgraphics.wastemanagement.ui.activity.main.activity.main.MainActivityState

data class ClientHomeState(
  val searchQuery: String = "",
  val state: MainActivityState = MainActivityState(),
)
