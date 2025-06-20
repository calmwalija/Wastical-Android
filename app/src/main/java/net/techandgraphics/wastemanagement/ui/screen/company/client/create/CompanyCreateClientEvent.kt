package net.techandgraphics.wastemanagement.ui.screen.company.client.create

sealed interface CompanyCreateClientEvent {

  data object Load : CompanyCreateClientEvent

  sealed interface Input : CompanyCreateClientEvent {
    data class Info(val value: Any, val type: Type) : Input
    enum class Type { FirstName, Lastname, Contact, AltContact, Title, Location, Plan }
  }

  sealed interface Button : CompanyCreateClientEvent {
    data object Submit : Button
  }

  sealed interface Goto : CompanyCreateClientEvent {
    data object BackHandler : Goto
    data class Profile(val id: Long) : Goto
  }
}
