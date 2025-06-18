package net.techandgraphics.wastemanagement.ui.screen.company.client.pending

sealed interface CompanyClientPendingPaymentEvent {
  data class Load(val id: Long) : CompanyClientPendingPaymentEvent

  sealed interface Goto : CompanyClientPendingPaymentEvent {
    data object BackHandler : Goto
  }
}