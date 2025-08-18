package net.techandgraphics.wastical.ui.screen.company.client.profile

sealed interface CompanyClientProfileEvent {
  data class Load(val id: Long) : CompanyClientProfileEvent

  sealed interface Option : CompanyClientProfileEvent {
    data object Payment : Option
    data object Plan : Option
    data object History : Option
    data object Invoice : Option
    data object Pending : Option
    data object Location : Option
    data object Info : Option
    data object Notification : Option
    data object Revoke : Option
    data object WhatsApp : Option
    data object Call : Option
  }

  sealed interface Goto : CompanyClientProfileEvent {
    data object BackHandler : Goto
    data class Location(val id: Long) : Goto
    data class WhatsApp(val contact: String) : Goto
    data class Call(val contact: String) : Goto
  }

  sealed interface Button : CompanyClientProfileEvent {
    data class Phone(val contact: String) : Button
  }

  sealed interface Broadcast : CompanyClientProfileEvent {
    data class Send(val title: String, val body: String) : Broadcast
  }
}
