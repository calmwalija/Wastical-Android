package net.techandgraphics.wastemanagement.ui.screen.company.client.plan

sealed interface CompanyClientPlanEvent {
  data class Load(val id: Long) : CompanyClientPlanEvent

  sealed interface Button : CompanyClientPlanEvent {
    data object ChangePlan : Button
  }
}
