package net.techandgraphics.wastical.ui.screen.company.home

import net.techandgraphics.wastical.data.local.database.dashboard.payment.MonthYear
import net.techandgraphics.wastical.data.local.database.payment.pay.PaymentEntity
import net.techandgraphics.wastical.data.remote.ApiResult

sealed interface CompanyHomeEvent {
  data object Tap : CompanyHomeEvent

  sealed interface Button : CompanyHomeEvent {
    data object Export : Button
    data object Logout : Button
    data class WorkingMonth(val param: MonthYear) : Button
  }

  data object Load : CompanyHomeEvent
  data object Fetch : CompanyHomeEvent

  sealed interface Goto : CompanyHomeEvent {
    data object Company : Goto
    data object Clients : Goto
    data object Payments : Goto
    data object Timeline : Goto
    data object Report : Goto
    data object PerLocation : Goto
    data class LocationOverview(val id: Long) : Goto
    data class Profile(val id: Long) : Goto
    data object VerifyPayment : Goto
    data object Login : Goto
    data object Reload : Goto
  }

  sealed interface Payment : CompanyHomeEvent {
    data class Success(val payments: List<PaymentEntity>) : Payment
    data class Failure(val error: ApiResult.Error) : Payment
  }
}
