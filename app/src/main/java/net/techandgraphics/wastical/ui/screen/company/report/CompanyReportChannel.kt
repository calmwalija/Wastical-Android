package net.techandgraphics.wastical.ui.screen.company.report

sealed interface CompanyReportChannel {

  sealed interface Pdf : CompanyReportChannel {
    data object Success : CompanyReportChannel
    data object Error : CompanyReportChannel
  }
}
