package net.techandgraphics.wastemanagement.ui.screen.client.home

import net.techandgraphics.wastemanagement.ui.activity.main.activity.main.MainActivityState

data class HomeState(
  val searchQuery: String = "",
  val state: MainActivityState = MainActivityState(),
)
