package net.techandgraphics.quantcal.ui.screen.company.location.overview

import net.techandgraphics.quantcal.data.local.database.dashboard.payment.AccountSortOrder

sealed interface CompanyPaymentLocationOverviewEvent {

  data class Load(val id: Long = 58) : CompanyPaymentLocationOverviewEvent

  sealed interface Button : CompanyPaymentLocationOverviewEvent {
    data object BackHandler : Button
    data class SortBy(val sort: AccountSortOrder) : Button
  }

  sealed interface Goto : CompanyPaymentLocationOverviewEvent {
    data class Profile(val id: Long) : Goto
  }
}
