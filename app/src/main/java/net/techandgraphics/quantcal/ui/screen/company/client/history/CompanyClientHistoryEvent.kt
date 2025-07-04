package net.techandgraphics.quantcal.ui.screen.company.client.history

import net.techandgraphics.quantcal.domain.model.payment.PaymentUiModel
import net.techandgraphics.quantcal.ui.activity.MainActivityState

sealed interface CompanyClientHistoryEvent {

  data class Load(val id: Long, val state: MainActivityState) : CompanyClientHistoryEvent

  sealed interface Button : CompanyClientHistoryEvent {
    sealed interface Invoice : Button {
      data class Event(val payment: PaymentUiModel, val op: Op) : Invoice
      enum class Op { Preview, Share }
    }

    data class Delete(val id: Long) : Button
    data class Phone(val contact: String) : Button
  }

  sealed interface Goto : CompanyClientHistoryEvent {
    data object BackHandler : Goto
    data class Location(val id: Long) : Goto
  }
}
