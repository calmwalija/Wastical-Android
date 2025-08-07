package net.techandgraphics.wastical.ui.screen.company.report

import net.techandgraphics.wastical.data.local.database.dashboard.payment.MonthYear

sealed interface CompanyReportEvent {

  data object Load : CompanyReportEvent

  sealed interface Goto : CompanyReportEvent {
    data object BackHandler : Goto
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
      data object PaymentCoverage : Report
    }

    sealed interface MonthDialog : CompanyReportEvent {
      data object Close : MonthDialog
      data object Proceed : MonthDialog
      data class PickMonth(val monthYear: MonthYear) : MonthDialog
    }
  }
}
