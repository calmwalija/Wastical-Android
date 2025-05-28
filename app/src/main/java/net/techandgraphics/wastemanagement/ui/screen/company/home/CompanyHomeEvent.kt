package net.techandgraphics.wastemanagement.ui.screen.company.home

import net.techandgraphics.wastemanagement.data.local.database.payment.pay.PaymentEntity
import net.techandgraphics.wastemanagement.data.remote.LoadingEvent

sealed interface CompanyHomeEvent {
  data object Tap : CompanyHomeEvent

  sealed interface Payment : CompanyHomeEvent {
    data class Success(val payments: List<PaymentEntity>) : Payment
    data class Failure(val error: LoadingEvent.Error) : Payment
  }
}
