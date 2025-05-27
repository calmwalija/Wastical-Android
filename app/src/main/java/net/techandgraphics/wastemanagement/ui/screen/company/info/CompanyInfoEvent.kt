package net.techandgraphics.wastemanagement.ui.screen.company.info

sealed interface CompanyInfoEvent {
  data object Tap : CompanyInfoEvent
}
