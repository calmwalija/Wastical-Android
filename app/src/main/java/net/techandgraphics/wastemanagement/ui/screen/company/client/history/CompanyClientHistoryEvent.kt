package net.techandgraphics.wastemanagement.ui.screen.company.client.history

sealed interface CompanyClientHistoryEvent {
  data class Load(val id: Long) : CompanyClientHistoryEvent
}
