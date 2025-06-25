package net.techandgraphics.wastemanagement.ui.screen.company.client.pending

import net.techandgraphics.wastemanagement.domain.model.payment.PaymentRequestUiModel

sealed interface CompanyClientPendingPaymentEvent {
  data class Load(val id: Long) : CompanyClientPendingPaymentEvent

  sealed interface Button : CompanyClientPendingPaymentEvent {
    data class Delete(val payment: PaymentRequestUiModel) : Button
  }

  sealed interface Goto : CompanyClientPendingPaymentEvent {
    data object BackHandler : Goto
  }
}
