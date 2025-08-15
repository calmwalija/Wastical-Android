package net.techandgraphics.wastical.ui.screen.company.payment.timeline

sealed interface CompanyPaymentTimelineEvent {

  data object Load : CompanyPaymentTimelineEvent

  sealed interface Input : CompanyPaymentTimelineEvent {
    data class Query(val query: String) : Input
  }

  sealed interface Goto : CompanyPaymentTimelineEvent {
    data class Invoice(val id: Long) : Goto
  }

  sealed interface Button : CompanyPaymentTimelineEvent {
    data class Sort(val value: Boolean) : Button
    data object BackHandler : Button
  }
}
