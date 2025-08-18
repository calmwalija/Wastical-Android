package net.techandgraphics.wastical.ui.screen.company.location.overview

import net.techandgraphics.wastical.data.local.database.dashboard.payment.AccountSortOrder

sealed interface CompanyPaymentLocationOverviewEvent {

  data class Load(val id: Long = 58) : CompanyPaymentLocationOverviewEvent

  sealed interface Button : CompanyPaymentLocationOverviewEvent {
    data object BackHandler : Button
    data object Broadcast : Button
    data class SortBy(val sort: AccountSortOrder) : Button
    data class ClientCreate(val locationId: Long) : Button
  }

  sealed interface Goto : CompanyPaymentLocationOverviewEvent {
    data class Profile(val id: Long) : Goto
    data class RecordProofOfPayment(val id: Long) : Goto
  }

  sealed interface Broadcast : CompanyPaymentLocationOverviewEvent {
    data class Send(val title: String, val body: String) : Broadcast
  }
}
