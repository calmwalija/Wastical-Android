package net.techandgraphics.wastemanagement.ui.screen.company.account.create

import net.techandgraphics.wastemanagement.ui.activity.main.activity.main.MainActivityState

sealed interface CreateAccountEvent {

  data class AppState(val state: MainActivityState) : CreateAccountEvent

  sealed interface Create : CreateAccountEvent {
    sealed interface Input : Create {
      data class Info(val value: Any, val type: Type) : Input
      enum class Type { FirstName, Lastname, Contact, AltContact, Title, Street, Plan }
    }

    sealed interface Button : Create {
      data object Submit : Button
    }
  }
}
