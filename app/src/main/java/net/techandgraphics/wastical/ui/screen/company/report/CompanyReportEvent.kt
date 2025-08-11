package net.techandgraphics.wastical.ui.screen.company.report

import net.techandgraphics.wastical.data.local.database.dashboard.account.DemographicItem
import net.techandgraphics.wastical.data.local.database.dashboard.payment.MonthYear

sealed interface CompanyReportEvent {

  data object Load : CompanyReportEvent

  sealed interface Goto : CompanyReportEvent {
    data object BackHandler : Goto
    data object Timeline : Goto
  }

  sealed interface Button : CompanyReportEvent {

    sealed interface Report : Button {
      data object ActiveClient : Report
      data object MissedPayment : Report
      data object PaidPayment : Report
      data object NewClient : Report
      data object Overpayment : Report
      data object LocationBased : Report
      data object ClientDisengagement : Report
      data object OutstandingBalance : Report
      data object RevenueSummary : Report
      data object PaymentMethodBreakdown : Report
      data object PlanPerformance : Report
      data object LocationCollection : Report
      data object GatewaySuccess : Report
      data object UpfrontPaymentsDetail : Report
      data object PaymentAging : Report
    }

    sealed interface MonthDialog : CompanyReportEvent {
      data object Close : MonthDialog
      data object Proceed : MonthDialog
      data class PickMonth(val monthYear: MonthYear) : MonthDialog
    }

    sealed interface LocationDialog : CompanyReportEvent {
      data object Close : LocationDialog
      data object Proceed : LocationDialog
      data class Pick(val item: DemographicItem) : LocationDialog
    }
  }
}
