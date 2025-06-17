package net.techandgraphics.wastemanagement.ui.screen.company.payment.location.overview

sealed interface CompanyPaymentLocationOverviewEvent {

  data class Load(val id: Long = 58) : CompanyPaymentLocationOverviewEvent

  sealed interface Button : CompanyPaymentLocationOverviewEvent {
    data object BackHandler : Button
  }
}
