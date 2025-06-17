package net.techandgraphics.wastemanagement.ui.screen.company.payment.location

sealed interface CompanyPaymentPerLocationEvent {

  data object Load : CompanyPaymentPerLocationEvent

  sealed interface Button : CompanyPaymentPerLocationEvent {
    data object BackHandler : CompanyPaymentPerLocationEvent
  }

  sealed interface Goto : CompanyPaymentPerLocationEvent {
    data class LocationOverview(val id: Long) : Goto
  }
}
