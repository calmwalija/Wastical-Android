package net.techandgraphics.quantcal.ui.screen.company.client.create

sealed interface CompanyCreateClientEvent {

  data class Load(val locationId: Long) : CompanyCreateClientEvent

  sealed interface Input : CompanyCreateClientEvent {
    data class Info(val value: Any, val type: Type) : Input
    enum class Type { FirstName, Lastname, Contact, AltContact, Title, Plan }
  }

  sealed interface Button : CompanyCreateClientEvent {
    data object Submit : Button
  }

  sealed interface Goto : CompanyCreateClientEvent {
    data object BackHandler : Goto
  }
}
