package net.techandgraphics.quantcal.ui.screen.company.client.pending

import net.techandgraphics.quantcal.domain.model.payment.PaymentRequestUiModel

sealed interface CompanyClientPendingPaymentEvent {
  data class Load(val id: Long) : CompanyClientPendingPaymentEvent

  sealed interface Button : CompanyClientPendingPaymentEvent {
    data class Delete(val payment: PaymentRequestUiModel) : Button
    data class Phone(val contact: String) : Button
  }

  sealed interface Goto : CompanyClientPendingPaymentEvent {
    data object BackHandler : Goto
    data class Location(val id: Long) : Goto
  }
}
