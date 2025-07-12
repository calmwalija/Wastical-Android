package net.techandgraphics.quantcal.ui.screen.company.client.info

sealed interface CompanyClientInfoEvent {

  data class Load(val id: Long) : CompanyClientInfoEvent

  sealed interface Button : CompanyClientInfoEvent {
    data object BackHandler : Button
    data object Submit : Button
  }

  sealed interface Input : CompanyClientInfoEvent {
    enum class OfType { FName, LName, Contact, AltContact, Email, Title }
    data class Type(val newValue: String, val ofType: OfType) : Input
  }
}
