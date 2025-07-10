package net.techandgraphics.quantcal.ui.screen.company.report

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
    }
  }
}
