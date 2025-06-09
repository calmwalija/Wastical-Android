package net.techandgraphics.wastemanagement.ui.screen.company.client.history

import net.techandgraphics.wastemanagement.domain.model.payment.PaymentUiModel
import net.techandgraphics.wastemanagement.ui.activity.main.activity.main.MainActivityState

sealed interface CompanyClientHistoryEvent {

  data class Load(val id: Long, val state: MainActivityState) : CompanyClientHistoryEvent

  sealed interface Button : CompanyClientHistoryEvent {
    sealed interface Invoice : Button {
      data class Event(val payment: PaymentUiModel, val op: Op) : Invoice
      enum class Op { Preview, Share }
    }
  }
}
