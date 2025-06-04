package net.techandgraphics.wastemanagement.ui.screen.company.home

import net.techandgraphics.wastemanagement.data.local.database.payment.pay.PaymentEntity
import net.techandgraphics.wastemanagement.data.remote.ApiResult
import net.techandgraphics.wastemanagement.ui.activity.main.activity.main.MainActivityState

sealed interface CompanyHomeEvent {
  data object Tap : CompanyHomeEvent

  data class AppState(val state: MainActivityState) : CompanyHomeEvent

  sealed interface Goto : CompanyHomeEvent {
    data object Profile : Goto
    data object Clients : Goto
    data object Payments : Goto
    data object Create : Goto
  }

  sealed interface Payment : CompanyHomeEvent {
    data class Success(val payments: List<PaymentEntity>) : Payment
    data class Failure(val error: ApiResult.Error) : Payment
  }
}
