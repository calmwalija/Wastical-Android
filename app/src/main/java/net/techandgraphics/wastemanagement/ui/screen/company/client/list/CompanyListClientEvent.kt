package net.techandgraphics.wastemanagement.ui.screen.company.client.list

import net.techandgraphics.wastemanagement.ui.activity.main.activity.main.MainActivityState

sealed interface CompanyListClientEvent {

  data class AppState(val state: MainActivityState) : CompanyListClientEvent

  sealed interface Goto : CompanyListClientEvent {
    data class Profile(val id: Long) : Goto
  }
}
