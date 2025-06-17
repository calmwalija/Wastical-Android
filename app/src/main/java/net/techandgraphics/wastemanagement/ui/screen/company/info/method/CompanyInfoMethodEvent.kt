package net.techandgraphics.wastemanagement.ui.screen.company.info.method

sealed interface CompanyInfoMethodEvent {

  data object Load : CompanyInfoMethodEvent
  sealed interface Button : CompanyInfoMethodEvent {
    data object Plan : Button
    data object BackHandler : Button
  }
}
