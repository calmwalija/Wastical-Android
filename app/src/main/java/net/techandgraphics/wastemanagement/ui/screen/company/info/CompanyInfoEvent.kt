package net.techandgraphics.wastemanagement.ui.screen.company.info

sealed interface CompanyInfoEvent {

  sealed interface Button : CompanyInfoEvent {
    data object BackHandler : Button
  }

  sealed interface Goto : CompanyInfoEvent {
    data object Edit : Goto
    data object Plan : Goto
    data object Method : Goto
  }
}
