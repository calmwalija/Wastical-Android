package net.techandgraphics.wastemanagement.ui.screen.company.client.create

import net.techandgraphics.wastemanagement.ui.activity.main.activity.main.MainActivityState

sealed interface CompanyCreateClientEvent {

  data class AppState(val state: MainActivityState) : CompanyCreateClientEvent

  sealed interface Create : CompanyCreateClientEvent {
    sealed interface Input : Create {
      data class Info(val value: Any, val type: Type) : Input
      enum class Type { FirstName, Lastname, Contact, AltContact, Title, Street, Plan }
    }

    sealed interface Button : Create {
      data object Submit : Button
    }
  }
}
