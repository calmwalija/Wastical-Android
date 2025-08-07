package net.techandgraphics.wastical.ui.screen.company.report

import net.techandgraphics.wastical.data.local.database.dashboard.payment.MonthYear

sealed interface CompanyReportEvent {

  data object Load : CompanyReportEvent

  sealed interface Goto : CompanyReportEvent {
    data object BackHandler : Goto
  }

  sealed interface Button : CompanyReportEvent {

    sealed interface Export : Button {
      data object Client : Export
      data object Outstanding : Export
      data object Collected : Export
      data object Plan : Export
      data object Coverage : Export
      data object Geographic : Export
      data object NewAccount : Export
    }

    sealed interface MonthDialog : CompanyReportEvent {
      data object Close : MonthDialog
      data object Proceed : MonthDialog
      data class PickMonth(val monthYear: MonthYear) : MonthDialog
    }
  }
}
