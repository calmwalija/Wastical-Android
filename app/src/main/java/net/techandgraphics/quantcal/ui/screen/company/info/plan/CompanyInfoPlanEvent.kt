package net.techandgraphics.quantcal.ui.screen.company.info.plan

sealed interface CompanyInfoPlanEvent {
  sealed interface Button : CompanyInfoPlanEvent {
    data object Plan : Button
    data object BackHandler : Button
  }
}
