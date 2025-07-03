package net.techandgraphics.quantcal.ui.screen.company.payment.timeline

sealed interface PaymentTimelineEvent {
  data object Load : PaymentTimelineEvent

  sealed interface Goto : PaymentTimelineEvent {
    data object BackHandler : Goto
    data class Profile(val id: Long) : Goto
  }
}
