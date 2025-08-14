package net.techandgraphics.wastical.ui.screen.company.payment.timeline

sealed interface CompanyPaymentTimelineEvent {

  data object Load : CompanyPaymentTimelineEvent
  data class InputQuery(val query: String) : CompanyPaymentTimelineEvent
  data class GotoInvoice(val paymentId: Long) : CompanyPaymentTimelineEvent

  sealed interface Button : CompanyPaymentTimelineEvent {
    data class DateTime(val dateTime: PaymentDateTime) : Button
    data object BackHandler : Button
  }

  data class DateFrom(val ts: Long?) : CompanyPaymentTimelineEvent
  data class DateTo(val ts: Long?) : CompanyPaymentTimelineEvent
  data class SortDesc(val value: Boolean) : CompanyPaymentTimelineEvent
  data class DatePreset(val preset: DateRangePreset) : CompanyPaymentTimelineEvent
}
