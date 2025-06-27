package net.techandgraphics.wastemanagement.ui.screen.company.home

import net.techandgraphics.wastemanagement.data.local.database.dashboard.payment.MonthYear
import net.techandgraphics.wastemanagement.data.local.database.payment.pay.PaymentEntity
import net.techandgraphics.wastemanagement.data.remote.ApiResult

sealed interface CompanyHomeEvent {
  data object Tap : CompanyHomeEvent

  sealed interface Button : CompanyHomeEvent {
    data object Export : Button
    data class WorkingMonth(val param: MonthYear) : Button
  }

  data object Load : CompanyHomeEvent

  sealed interface Goto : CompanyHomeEvent {
    data object Company : Goto
    data object Clients : Goto
    data object Payments : Goto
    data object Create : Goto
    data object PerLocation : Goto
    data class LocationOverview(val id: Long) : Goto
    data object VerifyPayment : Goto
  }

  sealed interface Payment : CompanyHomeEvent {
    data class Success(val payments: List<PaymentEntity>) : Payment
    data class Failure(val error: ApiResult.Error) : Payment
  }
}
